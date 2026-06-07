package com.athlunakms.eccang.order.service;

import com.athlunakms.eccang.influencer.entity.InfluencerReadOnly;
import com.athlunakms.eccang.influencer.entity.SocialMediaReadOnly;
import com.athlunakms.eccang.influencer.repository.InfluencerReadOnlyRepository;
import com.athlunakms.eccang.influencer.repository.SocialMediaReadOnlyRepository;
import com.athlunakms.eccang.order.entity.EccangOrder;
import com.athlunakms.eccang.order.entity.InfluencerConversionOrder;
import com.athlunakms.eccang.order.entity.InfluencerConversionImportLog;
import com.athlunakms.eccang.order.repository.EccangOrderRepository;
import com.athlunakms.eccang.order.repository.InfluencerConversionOrderRepository;
import com.athlunakms.eccang.order.repository.InfluencerSampleOrderRepository;
import com.athlunakms.eccang.order.repository.InfluencerConversionImportLogRepository;
import com.athlunakms.eccang.store.entity.EccangStore;
import com.athlunakms.eccang.store.repository.EccangStoreRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.poi.ss.usermodel.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class ConversionOrderImportService {

    private final InfluencerConversionOrderRepository conversionOrderRepository;
    private final InfluencerSampleOrderRepository sampleOrderRepository;
    private final EccangOrderRepository orderRepository;
    private final InfluencerReadOnlyRepository influencerReadOnlyRepository;
    private final SocialMediaReadOnlyRepository socialMediaReadOnlyRepository;
    private final InfluencerConversionImportLogRepository importLogRepository;
    private final EccangStoreRepository storeRepository;
    private final ObjectMapper objectMapper;

    @Transactional
    public Map<String, Object> importConversionOrders(MultipartFile file, Long operatorId) {
        String fileName = file.getOriginalFilename();
        log.info("Starting conversion orders import from file: {}, operatorId={}", fileName, operatorId);

        List<ImportRow> rows = new ArrayList<>();
        try {
            if (fileName != null && (fileName.endsWith(".xls") || fileName.endsWith(".xlsx"))) {
                rows = parseExcel(file.getInputStream());
            } else if (fileName != null && fileName.endsWith(".csv")) {
                rows = parseCsv(file.getInputStream());
            } else {
                throw new IllegalArgumentException("不支持的文件格式，仅支持 Excel (.xls, .xlsx) 或 CSV 文件");
            }
        } catch (Exception e) {
            log.error("Failed to parse file: {}", e.getMessage(), e);
            throw new RuntimeException("解析导入文件失败: " + e.getMessage());
        }

        int totalCount = rows.size();
        int successCount = 0;
        int skippedCount = 0;
        int failedCount = 0;
        String batchNo = "BATCH_" + System.currentTimeMillis();

        List<Map<String, Object>> errorDetails = new ArrayList<>();

        // Resolve default store for mock orders
        Long storeId = 1L;
        String storeName = "默认店铺";
        List<EccangStore> stores = storeRepository.findAll();
        if (!stores.isEmpty()) {
            storeId = stores.get(0).getId();
            storeName = stores.get(0).getStoreName();
        }

        for (int i = 0; i < rows.size(); i++) {
            ImportRow row = rows.get(i);
            int rowNum = i + 2; // human readable 1-based, assuming header is row 1
            
            // 1. Basic validation
            if (row.getOrderId() == null || row.getOrderId().isBlank()) {
                failedCount++;
                errorDetails.add(Map.of("rowNum", rowNum, "orderId", "", "error", "订单ID不能为空"));
                continue;
            }
            if (row.getInfluencerHandle() == null || row.getInfluencerHandle().isBlank()) {
                failedCount++;
                errorDetails.add(Map.of("rowNum", rowNum, "orderId", row.getOrderId(), "error", "红人Handle不能为空"));
                continue;
            }

            // 2. Validate and resolve influencer
            InfluencerReadOnly influencer = resolveInfluencerByHandle(row.getInfluencerHandle());
            if (influencer == null) {
                failedCount++;
                errorDetails.add(Map.of(
                    "rowNum", rowNum, 
                    "orderId", row.getOrderId(), 
                    "error", "未匹配到对应的红人账号(Handle: " + row.getInfluencerHandle() + ")"
                ));
                continue;
            }

            // 3. Skip-on-duplicate logic (Sample Orders)
            boolean existsInSample = checkDuplicateInSampleOrders(row.getOrderId());
            if (existsInSample) {
                skippedCount++;
                errorDetails.add(Map.of(
                    "rowNum", rowNum, 
                    "orderId", row.getOrderId(), 
                    "error", "在样品单中已存在，跳过导入"
                ));
                continue;
            }

            // 4. Skip-on-duplicate logic (Conversion Orders)
            boolean existsInConversion = checkDuplicateInConversionOrders(row.getOrderId());
            if (existsInConversion) {
                skippedCount++;
                errorDetails.add(Map.of(
                    "rowNum", rowNum, 
                    "orderId", row.getOrderId(), 
                    "error", "在转化单中已存在，跳过导入"
                ));
                continue;
            }

            try {
                // 5. Retrieve or create mock base order in `orders`
                EccangOrder baseOrder = getOrCreateBaseOrder(row, storeId, storeName);

                // 6. Create InfluencerConversionOrder
                InfluencerConversionOrder convOrder = new InfluencerConversionOrder();
                convOrder.setOrderId(baseOrder.getId());
                convOrder.setEccangOrderId(baseOrder.getEccangOrderId());
                convOrder.setEccangOrderNumber(baseOrder.getEccangOrderNumber());
                convOrder.setOrderName(baseOrder.getName());
                
                convOrder.setInfluencerId(influencer.getId());
                convOrder.setInfluencerName(influencer.getRealName() != null ? influencer.getRealName() : influencer.getNickName());
                convOrder.setTotalPrice(row.getOrderAmount() != null ? row.getOrderAmount() : baseOrder.getTotalPrice());
                convOrder.setCommissionableAmount(row.getOrderAmount() != null ? row.getOrderAmount() : baseOrder.getTotalPrice());
                convOrder.setCommissionAmount(row.getCommissionAmount() != null ? row.getCommissionAmount() : BigDecimal.ZERO);
                convOrder.setCurrency("USD");
                convOrder.setCommissionStatus("calculated");
                
                // Set import audit metadata
                convOrder.setImportedHandle(row.getInfluencerHandle());
                convOrder.setIsImported(true);
                convOrder.setImportSource(fileName);
                convOrder.setImportBatchNo(batchNo);
                convOrder.setOrderCreatedAt(LocalDateTime.now()); // placeholder if none exists

                conversionOrderRepository.save(convOrder);
                successCount++;
            } catch (Exception e) {
                failedCount++;
                log.error("Failed to import row {}: {}", rowNum, e.getMessage(), e);
                errorDetails.add(Map.of(
                    "rowNum", rowNum, 
                    "orderId", row.getOrderId(), 
                    "error", "系统保存失败: " + e.getMessage()
                ));
            }
        }

        // 7. Save summary log to DB
        String errorDetailsJson = null;
        try {
            errorDetailsJson = objectMapper.writeValueAsString(errorDetails);
        } catch (Exception e) {
            log.error("Failed to serialize error details", e);
        }

        InfluencerConversionImportLog importLog = InfluencerConversionImportLog.builder()
                .importType("CONVERSION_EXCEL")
                .fileName(fileName)
                .totalCount(totalCount)
                .successCount(successCount)
                .skippedCount(skippedCount)
                .failedCount(failedCount)
                .errorDetailsJson(errorDetailsJson)
                .createdBy(operatorId)
                .build();
        importLogRepository.save(importLog);

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("success", true);
        result.put("batchNo", batchNo);
        result.put("totalCount", totalCount);
        result.put("successCount", successCount);
        result.put("skippedCount", skippedCount);
        result.put("failedCount", failedCount);
        result.put("errors", errorDetails);
        return result;
    }

    private InfluencerReadOnly resolveInfluencerByHandle(String handle) {
        if (handle == null || handle.isBlank()) {
            return null;
        }
        String cleanHandle = handle.trim().toLowerCase();
        if (cleanHandle.startsWith("@")) {
            cleanHandle = cleanHandle.substring(1);
        }
        // 1. Check SocialMediaReadOnly
        List<SocialMediaReadOnly> sms = socialMediaReadOnlyRepository.findByHandleIgnoreCase(cleanHandle);
        if (!sms.isEmpty()) {
            Long infId = sms.get(0).getInfluencerId();
            return influencerReadOnlyRepository.findById(infId).orElse(null);
        }
        // 2. Check defaultHandle
        Optional<InfluencerReadOnly> opt = influencerReadOnlyRepository.findByDefaultHandleIgnoreCase(cleanHandle);
        return opt.orElse(null);
    }

    private boolean checkDuplicateInSampleOrders(String orderId) {
        String cleanId = orderId.trim();
        // Check orders table first to see if order exists
        Optional<EccangOrder> orderOpt = orderRepository.findByEccangOrderId(cleanId)
                .or(() -> orderRepository.findByName(cleanId));
        if (orderOpt.isPresent()) {
            if (sampleOrderRepository.findByOrderId(orderOpt.get().getId()).isPresent()) {
                return true;
            }
        }
        
        // Fallback checks on string identifiers directly
        return sampleOrderRepository.existsByOrderName(cleanId) ||
               sampleOrderRepository.existsByOrderName("#" + cleanId);
    }

    private boolean checkDuplicateInConversionOrders(String orderId) {
        String cleanId = orderId.trim();
        Optional<EccangOrder> orderOpt = orderRepository.findByEccangOrderId(cleanId)
                .or(() -> orderRepository.findByName(cleanId));
        if (orderOpt.isPresent()) {
            if (conversionOrderRepository.findByOrderId(orderOpt.get().getId()).isPresent()) {
                return true;
            }
        }
        return conversionOrderRepository.existsByOrderName(cleanId) ||
               conversionOrderRepository.existsByOrderName("#" + cleanId);
    }

    private EccangOrder getOrCreateBaseOrder(ImportRow row, Long storeId, String storeName) {
        String cleanId = row.getOrderId().trim();
        
        // Find existing
        Optional<EccangOrder> existingOpt = orderRepository.findByEccangOrderId(cleanId)
                .or(() -> orderRepository.findByName(cleanId));
        if (existingOpt.isPresent()) {
            return existingOpt.get();
        }

        // Create placeholder order
        EccangOrder order = new EccangOrder();
        order.setStoreId(storeId);
        order.setStoreName(storeName);
        order.setShopDomain(storeName);
        order.setEccangOrderId(cleanId);
        
        // Try parsing number
        try {
            order.setEccangOrderNumber(Long.parseLong(cleanId.replaceAll("[^0-9]", "")));
        } catch (Exception e) {
            order.setEccangOrderNumber(0L);
        }
        
        order.setName(cleanId);
        order.setTotalPrice(row.getOrderAmount() != null ? row.getOrderAmount() : BigDecimal.ZERO);
        order.setCurrency("USD");
        order.setOrderSource("imported_conversion");
        order.setCreationMode("imported");
        order.setFinancialStatus("paid");
        order.setFulfillmentStatus("fulfilled");
        order.setCreatedAtEccang(LocalDateTime.now());
        
        return orderRepository.save(order);
    }

    private List<ImportRow> parseExcel(InputStream is) throws Exception {
        List<ImportRow> list = new ArrayList<>();
        try (Workbook wb = WorkbookFactory.create(is)) {
            Sheet sheet = wb.getSheetAt(0);
            if (sheet == null) {
                return list;
            }
            
            Row headerRow = sheet.getRow(0);
            if (headerRow == null) {
                return list;
            }

            int orderIdCol = -1;
            int amountCol = -1;
            int commissionCol = -1;
            int handleCol = -1;

            // Header mapping
            for (int cellIdx = 0; cellIdx < headerRow.getLastCellNum(); cellIdx++) {
                Cell cell = headerRow.getCell(cellIdx);
                if (cell == null) continue;
                String val = getCellValueAsString(cell).trim().toLowerCase();
                if (val.contains("order id") || val.contains("订单id") || val.contains("订单编号") || val.contains("订单号")) {
                    orderIdCol = cellIdx;
                } else if (val.contains("amount") || val.contains("金额") || val.contains("总金额") || val.contains("order amount")) {
                    amountCol = cellIdx;
                } else if (val.contains("commission") || val.contains("分佣") || val.contains("佣金")) {
                    commissionCol = cellIdx;
                } else if (val.contains("handle") || val.contains("红人") || val.contains("社媒")) {
                    handleCol = cellIdx;
                }
            }

            // Fallbacks
            if (orderIdCol == -1) orderIdCol = 0;
            if (amountCol == -1) amountCol = 1;
            if (commissionCol == -1) commissionCol = 2;
            if (handleCol == -1) handleCol = 3;

            for (int rowIdx = 1; rowIdx <= sheet.getLastRowNum(); rowIdx++) {
                Row row = sheet.getRow(rowIdx);
                if (row == null || isRowEmpty(row)) continue;

                String orderId = getCellValueAsString(row.getCell(orderIdCol));
                String amountStr = getCellValueAsString(row.getCell(amountCol));
                String commissionStr = getCellValueAsString(row.getCell(commissionCol));
                String handle = getCellValueAsString(row.getCell(handleCol));

                BigDecimal amount = parseBigDecimal(amountStr);
                BigDecimal commission = parseBigDecimal(commissionStr);

                list.add(new ImportRow(orderId, amount, commission, handle));
            }
        }
        return list;
    }

    private List<ImportRow> parseCsv(InputStream is) throws Exception {
        List<ImportRow> list = new ArrayList<>();
        try (CSVParser parser = new CSVParser(new InputStreamReader(is, StandardCharsets.UTF_8), 
                CSVFormat.DEFAULT.withFirstRecordAsHeader().withIgnoreHeaderCase().withTrim())) {
            
            // Map headers
            Map<String, Integer> headerMap = parser.getHeaderMap();
            String orderIdKey = findHeaderKey(headerMap, "order id", "订单id", "订单编号", "订单号");
            String amountKey = findHeaderKey(headerMap, "amount", "金额", "总金额", "order amount");
            String commissionKey = findHeaderKey(headerMap, "commission", "分佣", "佣金");
            String handleKey = findHeaderKey(headerMap, "handle", "红人", "社媒");

            for (CSVRecord record : parser) {
                String orderId = orderIdKey != null ? record.get(orderIdKey) : record.get(0);
                String amountStr = amountKey != null ? record.get(amountKey) : record.get(1);
                String commissionStr = commissionKey != null ? record.get(commissionKey) : record.get(2);
                String handle = handleKey != null ? record.get(handleKey) : record.get(3);

                BigDecimal amount = parseBigDecimal(amountStr);
                BigDecimal commission = parseBigDecimal(commissionStr);

                list.add(new ImportRow(orderId, amount, commission, handle));
            }
        }
        return list;
    }

    private String findHeaderKey(Map<String, Integer> map, String... keywords) {
        if (map == null) return null;
        for (String k : map.keySet()) {
            for (String kw : keywords) {
                if (k.toLowerCase().contains(kw)) {
                    return k;
                }
            }
        }
        return null;
    }

    private String getCellValueAsString(Cell cell) {
        if (cell == null) return "";
        CellType type = cell.getCellType();
        if (type == CellType.FORMULA) {
            type = cell.getCachedFormulaResultType();
        }
        switch (type) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getDateCellValue().toString();
                }
                return String.valueOf(cell.getNumericCellValue());
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case BLANK:
                return "";
            default:
                return "";
        }
    }

    private boolean isRowEmpty(Row row) {
        if (row == null) return true;
        for (int c = row.getFirstCellNum(); c < row.getLastCellNum(); c++) {
            Cell cell = row.getCell(c);
            if (cell != null && cell.getCellType() != CellType.BLANK) {
                String val = getCellValueAsString(cell).trim();
                if (!val.isEmpty()) {
                    return false;
                }
            }
        }
        return true;
    }

    private BigDecimal parseBigDecimal(String str) {
        if (str == null || str.isBlank()) return BigDecimal.ZERO;
        try {
            // Clean up currency characters like $, ¥, etc.
            String clean = str.replaceAll("[^0-9.\\-]", "");
            return new BigDecimal(clean);
        } catch (Exception e) {
            return BigDecimal.ZERO;
        }
    }

    @lombok.Getter
    @lombok.Setter
    @lombok.AllArgsConstructor
    private static class ImportRow {
        private String orderId;
        private BigDecimal orderAmount;
        private BigDecimal commissionAmount;
        private String influencerHandle;
    }
}
