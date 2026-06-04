package com.athlunakms.influencer.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.athlunakms.influencer.dto.RemittanceCreateDto;
import com.athlunakms.influencer.entity.ImportJob;
import com.athlunakms.influencer.entity.Influencer;
import com.athlunakms.influencer.repository.ImportJobRepository;
import com.athlunakms.influencer.repository.InfluencerRepository;
import com.athlunakms.influencer.service.storage.StorageService;
import com.athlunakms.influencer.service.storage.StorageServiceFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.usermodel.CellType;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class RemittanceImportService {

    private final ImportJobRepository importJobRepository;
    private final StorageServiceFactory storageServiceFactory;
    private final InfluencerRepository influencerRepository;
    private final RemittanceTaskService remittanceTaskService;
    private final ObjectMapper objectMapper;

    public Long createJob() {
        ImportJob job = new ImportJob();
        job.setStatus("PENDING");
        // We reuse INFLUENCER_IMPORT type or define REMITTANCE_IMPORT if backend has
        // it. Based on existing entity, type is String.
        job.setType("REMITTANCE_IMPORT");
        job.setCreatedAt(LocalDateTime.now());
        job.setTotalCount(0);
        job.setProcessedCount(0);
        job.setSuccessCount(0);
        job.setFailCount(0);
        return importJobRepository.save(job).getId();
    }

    public ImportJob getJobStatus(Long jobId) {
        return importJobRepository.findById(jobId).orElse(null);
    }

    @Async
    public void analyzeAsync(String fileKey, String originalFilename, Long jobId) {
        processImport(fileKey, originalFilename, jobId, true);
    }

    @Async
    public void importAsync(String fileKey, String originalFilename, Long jobId) {
        processImport(fileKey, originalFilename, jobId, false);
    }

    private void processImport(String fileKey, String originalFilename, Long jobId, boolean dryRun) {
        ImportJob job = importJobRepository.findById(jobId).orElse(null);
        if (job == null)
            return;

        job.setStatus("PROCESSING");
        job.setUpdatedAt(LocalDateTime.now());
        importJobRepository.save(job);

        try {
            StorageService storageService = storageServiceFactory.getStorageService();
            try (InputStream is = storageService.getFileStream(fileKey);
                    BufferedInputStream bis = new BufferedInputStream(is)) {

                boolean isCsv = originalFilename != null && originalFilename.toLowerCase().endsWith(".csv");

                Map<String, Integer> headerMap = new HashMap<>();
                List<List<String>> allRows = new ArrayList<>();
                int totalRows = 0;

                if (isCsv) {
                    org.apache.commons.csv.CSVFormat csvFormat = org.apache.commons.csv.CSVFormat.DEFAULT.builder()
                            .setHeader()
                            .setSkipHeaderRecord(true)
                            .setIgnoreHeaderCase(true)
                            .setTrim(true)
                            .build();
                    try (java.io.Reader reader = new java.io.InputStreamReader(bis,
                            java.nio.charset.StandardCharsets.UTF_8);
                            org.apache.commons.csv.CSVParser csvParser = new org.apache.commons.csv.CSVParser(reader,
                                    csvFormat)) {
                        Map<String, Integer> csvHeaderMap = csvParser.getHeaderMap();
                        if (csvHeaderMap != null) {
                            headerMap.putAll(csvHeaderMap);
                        }
                        for (org.apache.commons.csv.CSVRecord record : csvParser) {
                            List<String> rowData = new ArrayList<>();
                            for (String val : record.toList()) {
                                rowData.add(val);
                            }
                            allRows.add(rowData);
                        }
                        totalRows = allRows.size();
                    }
                } else {
                    try (Workbook workbook = WorkbookFactory.create(bis)) {
                        Sheet sheet = null;
                        // 1. 优先尝试寻找名为 "数据填写" 的 Sheet
                        for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
                            if ("数据填写".equals(workbook.getSheetName(i))) {
                                sheet = workbook.getSheetAt(i);
                                break;
                            }
                        }

                        // 2. 如果没找到，检查第一个 Sheet 是否包含必需的表头，如果不包含则尝试第二个
                        if (sheet == null) {
                            sheet = workbook.getSheetAt(0);
                            if (workbook.getNumberOfSheets() > 1) {
                                Row firstRow = sheet.getRow(0);
                                boolean hasRequiredHeader = false;
                                if (firstRow != null) {
                                    for (int i = 0; i < firstRow.getLastCellNum(); i++) {
                                        String cellVal = this.getCellValue(firstRow, i);
                                        if (cellVal != null && (cellVal.contains("邮箱") || cellVal.contains("Email"))) {
                                            hasRequiredHeader = true;
                                            break;
                                        }
                                    }
                                }
                                if (!hasRequiredHeader) {
                                    sheet = workbook.getSheetAt(1);
                                    log.info("Sheet 0 seems like a guide sheet, switching to Sheet 1");
                                }
                            }
                        }

                        totalRows = sheet.getLastRowNum();
                        Iterator<Row> rowIterator = sheet.rowIterator();

                        if (rowIterator.hasNext()) {
                            Row headerRow = rowIterator.next();
                            int maxCol = headerRow.getLastCellNum();
                            for (int i = 0; i < maxCol; i++) {
                                String header = getCellValue(headerRow, i);
                                if (header != null && !header.isEmpty()) {
                                    headerMap.put(header.trim(), i);
                                }
                            }
                        }

                        while (rowIterator.hasNext()) {
                            Row row = rowIterator.next();
                            // 使用 headerMap 中最大的索引来确定需要读取的列数
                            int maxColInMap = headerMap.values().stream().max(Integer::compareTo).orElse(0);
                            List<String> record = extractRowData(row, maxColInMap + 1);
                            allRows.add(record);
                        }
                    }
                }

                job.setTotalCount(totalRows);
                importJobRepository.save(job);

                int successCount = 0;
                int failCount = 0;
                List<String> errorMessages = new ArrayList<>();
                int rowNum = 1;

                for (List<String> record : allRows) {
                    ++rowNum;
                    try {

                        boolean isEmpty = record.stream().allMatch(val -> val == null || val.trim().isEmpty());
                        if (isEmpty)
                            continue;

                        String email = getValueByHeader(headerMap, record, "红人邮箱", 0);
                        if (email == null)
                            email = getValueByHeader(headerMap, record, "Email", 0);

                        String paymentType = getValueByHeader(headerMap, record, "付费类型", 1);
                        if (paymentType == null)
                            paymentType = getValueByHeader(headerMap, record, "Payment Type", 1);

                        String amountStr = getValueByHeader(headerMap, record, "金额", 2);
                        if (amountStr == null)
                            amountStr = getValueByHeader(headerMap, record, "Amount", 2);

                        String currency = getValueByHeader(headerMap, record, "币种", 3);
                        if (currency == null)
                            currency = getValueByHeader(headerMap, record, "Currency", 3);

                        String orderNo = getValueByHeader(headerMap, record, "关联订单", 4);
                        if (orderNo == null)
                            orderNo = getValueByHeader(headerMap, record, "Order No", 4);
                        if (orderNo != null && orderNo.startsWith("#")) {
                            orderNo = orderNo.substring(1);
                        }

                        String remark = getValueByHeader(headerMap, record, "备注", 5);
                        if (remark == null)
                            remark = getValueByHeader(headerMap, record, "Remark", 5);

                        // User requested extra fields
                        String statusStr = getValueByHeader(headerMap, record, "打款状态", 6);
                        if (statusStr == null)
                            statusStr = getValueByHeader(headerMap, record, "Status", 6);

                        String auditorName = getValueByHeader(headerMap, record, "审核人", 7);
                        if (auditorName == null)
                            auditorName = getValueByHeader(headerMap, record, "Auditor", 7);

                        String payerName = getValueByHeader(headerMap, record, "打款人", 8);
                        if (payerName == null)
                            payerName = getValueByHeader(headerMap, record, "Payer", 8);

                        if (!StringUtils.hasText(email)) {
                            throw new IllegalArgumentException("必须填写红人邮箱 Email");
                        }

                        // 处理金额，转换可能带有的逗号等格式
                        String cleanAmount = amountStr == null ? "0" : amountStr.trim().replace(",", "");
                        BigDecimal amt = new BigDecimal(cleanAmount.isEmpty() ? "0" : cleanAmount);
                        if (amt.compareTo(BigDecimal.ZERO) <= 0) {
                            throw new IllegalArgumentException("金额必须大于0");
                        }
                        if (!StringUtils.hasText(paymentType)) {
                            throw new IllegalArgumentException("付费类型(Payment Type)不能为空");
                        }

                        // Status Mapping
                        String mappedStatus = "PENDING_AUDIT"; // default
                        if (StringUtils.hasText(statusStr)) {
                            String lowerStatus = statusStr.trim().toLowerCase();
                            if (lowerStatus.contains("已汇款") || lowerStatus.contains("已打款")) {
                                mappedStatus = "PAID";
                            } else if (lowerStatus.contains("待汇款") || lowerStatus.contains("待打款")) {
                                mappedStatus = "PENDING_PAYMENT";
                            }
                        }

                        // Auditor & Payer (default to SYS if empty but mappedStatus indicates action)
                        if (!StringUtils.hasText(auditorName)
                                && ("PAID".equals(mappedStatus) || "PENDING_PAYMENT".equals(mappedStatus))) {
                            auditorName = "SYS";
                        }
                        if (!StringUtils.hasText(payerName) && "PAID".equals(mappedStatus)) {
                            payerName = "SYS";
                        }

                        // Match influencer by Email ONLY
                        Influencer matched = influencerRepository.findByEmailIgnoreCase(email.trim()).stream()
                                .findFirst()
                                .orElse(null);
                        if (matched == null) {
                            matched = influencerRepository.findByBackupEmailIgnoreCase(email.trim()).stream()
                                    .findFirst()
                                    .orElse(null);
                        }

                        if (matched == null) {
                            throw new IllegalArgumentException(
                                    "未找到匹配红人 (Email: " + (email != null ? email : "N/A") + ")");
                        }

                        if (!dryRun) {
                            RemittanceCreateDto createDto = new RemittanceCreateDto();
                            createDto.setInfluencerId(matched.getId());
                            createDto.setAmount(amt);
                            createDto.setCurrency(StringUtils.hasText(currency) ? currency.toUpperCase() : "USD");
                            createDto.setPaymentType(paymentType);
                            createDto.setOrderNo(orderNo);
                            createDto.setRemark(remark);
                            createDto.setStatus(mappedStatus);
                            createDto.setAuditorName(auditorName);
                            createDto.setPayerName(payerName);
                            remittanceTaskService.createRemittanceTask(createDto, "BatchImport");
                        }

                        successCount++;
                    } catch (Exception e) {
                        failCount++;
                        errorMessages.add(String.format("第 %d 行解析失败: %s", rowNum, e.getMessage()));
                        log.error("Row {} import failed: {}", rowNum, e.getMessage());
                    }

                    job.setProcessedCount(rowNum - 1);
                    if ((rowNum - 1) % 5 == 0) {
                        importJobRepository.saveAndFlush(job);
                    }
                }

                Map<String, Object> result = new HashMap<>();
                result.put("validCount", successCount);
                result.put("failCount", failCount);
                result.put("errorMessages", errorMessages);

                job.setResultJson(objectMapper.writeValueAsString(result));
                job.setSuccessCount(successCount);
                job.setFailCount(failCount);
                job.setStatus("COMPLETED");
                job.setUpdatedAt(LocalDateTime.now());
                importJobRepository.save(job);
            }
        } catch (Exception e) {
            log.error("Import/Analyze task failed", e);
            job.setStatus("FAILED");
            job.setErrorMessage(e.getMessage());
            job.setUpdatedAt(LocalDateTime.now());
            importJobRepository.save(job);

            if (!dryRun)
                throw new RuntimeException("Import failed", e);
        }
    }

    private List<String> extractRowData(Row row, int maxCol) {
        List<String> data = new ArrayList<>();
        for (int i = 0; i < maxCol; ++i) {
            data.add(getCellValue(row, i));
        }
        return data;
    }

    private String getCellValue(Row row, int index) {
        Cell cell = row.getCell(index);
        if (cell == null)
            return null;
        DataFormatter formatter = new DataFormatter();
        CellType cellType = cell.getCellType();
        switch (cellType) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell))
                    return cell.getDateCellValue().toString();
                return formatter.formatCellValue(cell);
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                try {
                    return formatter.formatCellValue(cell);
                } catch (Exception e) {
                    try {
                        return cell.getStringCellValue();
                    } catch (Exception e2) {
                        return String.valueOf(cell.getNumericCellValue());
                    }
                }
            case BLANK:
                return "";
            default:
                return null;
        }
    }

    private String getValueByHeader(Map<String, Integer> headerMap, List<String> record, String headerName,
            int fallbackIndex) {
        if (headerMap != null && headerMap.containsKey(headerName)) {
            int index = headerMap.get(headerName);
            if (index < record.size())
                return record.get(index);
        }
        if (fallbackIndex < record.size()) {
            String val = record.get(fallbackIndex);
            return val == null || val.trim().isEmpty() || "null".equalsIgnoreCase(val) ? null : val.trim();
        }
        return null;
    }
}
