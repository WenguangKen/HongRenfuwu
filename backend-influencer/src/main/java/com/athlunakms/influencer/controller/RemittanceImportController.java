package com.athlunakms.influencer.controller;

import com.athlunakms.influencer.dto.ApiResponse;
import com.athlunakms.influencer.repository.ImportJobRepository;
import com.athlunakms.influencer.service.RemittanceImportService;
import com.athlunakms.influencer.service.storage.StorageService;
import com.athlunakms.influencer.service.storage.StorageServiceFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;

/**
 * 汇款批量导入控制器
 *
 * 提供文件解析、确认导入、进度查询和模板下载功能。
 */
@Slf4j
@RestController
@RequestMapping("/v1/finance/remittance/import")
@RequiredArgsConstructor
public class RemittanceImportController {

    private final RemittanceImportService remittanceImportService;
    private final ImportJobRepository importJobRepository;
    private final StorageServiceFactory storageServiceFactory;

    /**
     * 解析上传文件（dry-run，不实际导入）
     */
    @PostMapping("/analyze")
    public ApiResponse<Map<String, Object>> analyzeFile(@RequestParam("file") MultipartFile file) {
        return handleImport(file, true);
    }

    private ApiResponse<Map<String, Object>> handleImport(MultipartFile file, boolean dryRun) {
        log.info("Received remittance import request (dryRun={}): {}", dryRun, file.getOriginalFilename());
        if (file.isEmpty()) {
            return ApiResponse.error("请选择要上传的文件");
        }
        try {
            StorageService storageService = storageServiceFactory.getStorageService();
            String originalFilename = file.getOriginalFilename();
            String datePath = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
            String fileKey = storageService.upload(file, "remittance-imports/" + datePath);

            Long jobId = remittanceImportService.createJob();
            if (dryRun) {
                remittanceImportService.analyzeAsync(fileKey, originalFilename, jobId);
            } else {
                remittanceImportService.importAsync(fileKey, originalFilename, jobId);
            }

            log.info("Import job created: jobId={}, fileKey={}, dryRun={}", jobId, fileKey, dryRun);
            return ApiResponse.success(Map.of(
                    "jobId", jobId,
                    "fileKey", fileKey,
                    "message", dryRun ? "解析已开始" : "导入已开始"));
        } catch (Exception e) {
            log.error("File upload failed: filename={}", file.getOriginalFilename(), e);
            return ApiResponse.error("文件上传失败: " + e.getMessage());
        }
    }

    /**
     * 确认导入（解析通过后正式写入数据）
     */
    @PostMapping("/confirm")
    public ApiResponse<Map<String, Object>> confirmImport(@RequestBody Map<String, Object> payload) {
        Object fileKeyObj = payload.get("fileKey");
        Object filenameObj = payload.get("originalFilename");
        String fileKey = fileKeyObj != null ? fileKeyObj.toString() : null;
        String originalFilename = filenameObj != null ? filenameObj.toString() : null;

        if (fileKey == null || fileKey.isBlank()) {
            return ApiResponse.error("fileKey 不能为空");
        }

        try {
            Long jobId = remittanceImportService.createJob();
            remittanceImportService.importAsync(fileKey, originalFilename, jobId);
            log.info("Confirm import started: jobId={}, fileKey={}", jobId, fileKey);
            return ApiResponse.success(Map.of("jobId", jobId, "message", "确认导入任务已提交"));
        } catch (Exception e) {
            log.error("Confirm import failed: fileKey={}", fileKey, e);
            return ApiResponse.error("确认导入失败: " + e.getMessage());
        }
    }

    /**
     * 查询导入任务状态
     */
    @GetMapping("/status/{jobId}")
    public ResponseEntity<?> getImportStatus(@PathVariable("jobId") Long jobId) {
        return importJobRepository.findById(jobId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * 下载汇款导入 Excel 模板
     */
    @GetMapping("/template")
    public ResponseEntity<byte[]> downloadTemplate() {
        try (XSSFWorkbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            // ===== 导入说明 Sheet =====
            Sheet guideSheet = workbook.createSheet("导入说明");
            guideSheet.setColumnWidth(0, 6400);
            guideSheet.setColumnWidth(1, 17920);

            CellStyle titleStyle = workbook.createCellStyle();
            Font titleFont = workbook.createFont();
            titleFont.setBold(true);
            titleFont.setFontHeightInPoints((short) 14);
            titleStyle.setFont(titleFont);
            titleStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
            titleStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            CellStyle wrapStyle = workbook.createCellStyle();
            wrapStyle.setWrapText(true);
            wrapStyle.setVerticalAlignment(VerticalAlignment.TOP);

            Row r0 = guideSheet.createRow(0);
            Cell c0 = r0.createCell(0);
            c0.setCellValue("汇款任务 - 批量导入规范说明");
            c0.setCellStyle(titleStyle);
            guideSheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 1));

            String[][] guides = {
                    {"字段名称", "填写要求与规范"},
                    {"邮箱 (Email)", "[必填] 用于在系统中查找对应红人并关联账单记录"},
                    {"付费类型 (Payment Type)", "[必填] 如：视频定金，视频全款等"},
                    {"金额 (Amount)", "[必填] 仅填写数字，如：1000"},
                    {"币种 (Currency)", "[选填] 默认使用 USD"},
                    {"关联订单 (Order No)", "[选填] 涉及的订单号，如：1234 (可带#也可不带)"},
                    {"备注 (Remark)", "[选填] 说明或其他"},
                    {"打款状态 (Status)", "[选填] 取值：待打款 / 已打款"},
                    {"审核人 (Auditor)", "[选填] 系统自动标记状态变更的审核人"},
                    {"打款人 (Payer)", "[选填] 系统自动标记执行付款的打款人"}
            };

            for (int i = 0; i < guides.length; i++) {
                Row row = guideSheet.createRow(i + 2);
                Cell cName = row.createCell(0);
                cName.setCellValue(guides[i][0]);
                Cell cDesc = row.createCell(1);
                cDesc.setCellValue(guides[i][1]);
                cDesc.setCellStyle(wrapStyle);
                if (i == 0) {
                    cName.setCellStyle(titleStyle);
                    cDesc.setCellStyle(titleStyle);
                }
            }

            // ===== 数据填写 Sheet =====
            Sheet dataSheet = workbook.createSheet("数据填写");
            String[] headers = {
                    "红人邮箱", "付费类型", "金额", "币种", "关联订单", "备注", "打款状态", "审核人", "打款人"
            };

            Row hRow = dataSheet.createRow(0);
            hRow.setHeightInPoints(30);

            CellStyle hStyle = workbook.createCellStyle();
            Font hFont = workbook.createFont();
            hFont.setColor(IndexedColors.WHITE.getIndex());
            hFont.setBold(true);
            hStyle.setFont(hFont);
            hStyle.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
            hStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            hStyle.setAlignment(HorizontalAlignment.CENTER);
            hStyle.setVerticalAlignment(VerticalAlignment.CENTER);

            for (int i = 0; i < headers.length; i++) {
                Cell cell = hRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(hStyle);
                dataSheet.setColumnWidth(i, 6400);
            }

            // 示例行（列顺序与表头严格对齐）
            Row exampleRow = dataSheet.createRow(1);
            exampleRow.createCell(0).setCellValue("example@influencer.com"); // 红人邮箱
            exampleRow.createCell(1).setCellValue("视频全款");                // 付费类型
            exampleRow.createCell(2).setCellValue("500");                    // 金额
            exampleRow.createCell(3).setCellValue("USD");                    // 币种
            exampleRow.createCell(4).setCellValue("#1234");                  // 关联订单
            exampleRow.createCell(5).setCellValue("加急处理");                // 备注
            exampleRow.createCell(6).setCellValue("待打款");                  // 打款状态

            workbook.write(out);

            return ResponseEntity.ok()
                    .header("Content-Disposition", "attachment; filename=\"remittance_import_template.xlsx\"")
                    .contentType(MediaType
                            .parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                    .body(out.toByteArray());

        } catch (Exception e) {
            log.error("Generate template failed", e);
            return ResponseEntity.internalServerError().build();
        }
    }
}
