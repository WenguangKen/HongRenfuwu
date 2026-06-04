package com.athlunakms.influencer.controller;

import com.athlunakms.influencer.repository.ImportJobRepository;
import com.athlunakms.influencer.service.InfluencerImportService;
import com.athlunakms.influencer.service.storage.StorageService;
import com.athlunakms.influencer.service.storage.StorageServiceFactory;
import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping(value={"/v1/influencer"})
public class InfluencerImportController {
    private static final Logger log = LoggerFactory.getLogger(InfluencerImportController.class);
    private final InfluencerImportService influencerImportService;
    private final ImportJobRepository importJobRepository;
    private final StorageServiceFactory storageServiceFactory;

    @PostMapping(value={"/import"})
    public ResponseEntity<Map<String, Object>> uploadFile(@RequestParam(value="file") MultipartFile file, @RequestParam(value="storeId", required=false) Long storeId, @RequestParam(value="stage", required=false) String stage) {
        return this.handleImport(file, storeId, false, stage);
    }

    @PostMapping(value={"/import/analyze"})
    public ResponseEntity<Map<String, Object>> analyzeFile(@RequestParam(value="file") MultipartFile file, @RequestParam(value="storeId", required=false) Long storeId, @RequestParam(value="stage", required=false) String stage) {
        return this.handleImport(file, storeId, true, stage);
    }

    private ResponseEntity<Map<String, Object>> handleImport(MultipartFile file, Long storeId, boolean dryRun, String stage) {
        log.info("Received import request (dryRun={}, stage={}): {}", dryRun, stage, file.getOriginalFilename());
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Please select a file to upload"));
        }
        try {
            StorageService storageService = this.storageServiceFactory.getStorageService();
            String originalFilename = file.getOriginalFilename();
            String datePath = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
            String fileKey = storageService.upload(file, "imports/" + datePath);
            Long jobId = this.influencerImportService.createJob();
            if (dryRun) {
                this.influencerImportService.analyzeAsync(fileKey, originalFilename, jobId, storeId, stage);
            } else {
                this.influencerImportService.importAsync(fileKey, originalFilename, jobId, storeId, stage);
            }
            return ResponseEntity.ok(Map.of("jobId", jobId, "fileKey", fileKey, "message", dryRun ? "Analysis started" : "Import started"));
        }
        catch (Exception e) {
            log.error("File upload failed", e);
            return ResponseEntity.internalServerError().body(Map.of("error", "File upload failed: " + e.getMessage()));
        }
    }

    @PostMapping(value={"/import/confirm"})
    public ResponseEntity<Map<String, Object>> confirmImport(@RequestBody Map<String, Object> payload) {
        String fileKey = (String)payload.get("fileKey");
        String originalFilename = (String)payload.get("originalFilename");
        String stage = (String)payload.get("stage");
        Object storeIdObj = payload.get("storeId");
        Long storeId = storeIdObj != null ? Long.valueOf(((Number)storeIdObj).longValue()) : null;
        if (fileKey == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "fileKey is required"));
        }
        try {
            Long jobId = this.influencerImportService.createJob();
            this.influencerImportService.importAsync(fileKey, originalFilename, jobId, storeId, stage);
            return ResponseEntity.ok(Map.of("jobId", jobId, "message", "Import confirmed and started"));
        }
        catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping(value={"/import/status/{jobId}"})
    public ResponseEntity<?> getImportStatus(@PathVariable(value="jobId") Long jobId) {
        return this.importJobRepository.findById(jobId).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @GetMapping(value={"/import/template"})
    public ResponseEntity<byte[]> downloadTemplate() {
        try (XSSFWorkbook workbook = new XSSFWorkbook()) {
            Sheet guideSheet = workbook.createSheet("导入说明");
            guideSheet.setColumnWidth(0, 6400);
            guideSheet.setColumnWidth(1, 17920);
            CellStyle titleStyle = workbook.createCellStyle();
            Font titleFont = workbook.createFont();
            titleFont.setBold(true);
            titleFont.setFontHeightInPoints((short)14);
            titleStyle.setFont(titleFont);
            titleStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
            titleStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            CellStyle wrapStyle = workbook.createCellStyle();
            wrapStyle.setWrapText(true);
            wrapStyle.setVerticalAlignment(VerticalAlignment.TOP);
            Row r0 = guideSheet.createRow(0);
            Cell c0 = r0.createCell(0);
            c0.setCellValue("红人管理系统 - 导入规范说明");
            c0.setCellStyle(titleStyle);
            guideSheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 1));
            String[][] guides = new String[][]{{"字段名称", "填写要求与规范"}, {"Email (必填)", "唯一主键系统会根据此字段判断是新增还是更新红人"}, {"红人状态", "选填，系统支持状态包括: PENDING(待联系), CONTACTED(已联系), COMMUNICATING(沟通中), COOPERATING(合作中), DORMANT(休眠中), PAUSED(暂不合作), BLACKLIST(黑名单), TERMINATED(不再合作)。留空则默认PENDING"}, {"对接人 (Pic)", "填写名字导入后系统会自动将其同步为 LIAISON 标签"}, {"社媒链接", "请填写完整的 URL (含 https://)支持 IG, TT, YT"}, {"粉丝数", "填写纯数字即可"}, {"层级/分类/风格", "自定义标签字段导入时会自动建立关联"}, {"折扣码", "支持多个用英文逗号 , 分隔，系统中不存在时会自动从Shopify同步"}, {"合作模式", "填 PAID(付费合作) 或 COMMISSION(佣金合作)有内容即为付费红人"}, {"已下订单号", "Shopify 已同步的订单号支持多个用英文逗号 , 分隔"}, {"PayPal账号", "填写PayPal收款邮箱或账号"}, {"银行收款信息", "包含银行名称、地址、Swift Code、Routing Number(US地区)、账户名、账户号码"}, {"红人联系电话", "填写红人的联系电话号码"}, {"红人收货地址", "填写红人的收货/寄样地址全文"}};
            for (int i = 0; i < guides.length; ++i) {
                Row row = guideSheet.createRow(i + 2);
                Cell cName = row.createCell(0);
                cName.setCellValue(guides[i][0]);
                Cell cDesc = row.createCell(1);
                cDesc.setCellValue(guides[i][1]);
                cDesc.setCellStyle(wrapStyle);
                if (i != 0) continue;
                cName.setCellStyle(titleStyle);
                cDesc.setCellStyle(titleStyle);
            }
            Sheet dataSheet = workbook.createSheet("数据填写");
            // 按用户需求排序：负责人→联络员→Email→IG→TT→YT→分类→风格→Cost→全名→地址→收款信息
            String[] headers = new String[]{
                "负责人",                                   // 0
                "联络员",                                   // 1
                "Email",                                   // 2
                "IG handle",                               // 3
                "IG link",                                 // 4
                "IG Followers",                            // 5
                "TT handle",                               // 6
                "TT link",                                 // 7
                "TT Followers",                            // 8
                "Youtube handle",                          // 9
                "YT Link",                                 // 10
                "YT Followers",                            // 11
                "红人分类",                                 // 12
                "红人风格",                                 // 13
                "Cost",                                    // 14
                "红人全名",                                 // 15
                "Country",                                 // 16
                "Address Line",                            // 17
                "Phone number",                            // 18
                "折扣码",                                   // 19
                "PayPal账号",                               // 20
                "收款银行名称",                              // 21
                "收款支行名称(选填)",                        // 22
                "收款银行地址",                              // 23
                "Swift Code(非US地区必填)",                  // 24
                "Routing Number(ACH,US美国地区)",            // 25
                "收款银行账户名",                            // 26
                "收款银行账户号码"                           // 27
            };
            Row hRow = dataSheet.createRow(0);
            hRow.setHeightInPoints(30.0f);
            CellStyle hStyle = workbook.createCellStyle();
            Font hFont = workbook.createFont();
            hFont.setColor(IndexedColors.WHITE.getIndex());
            hFont.setBold(true);
            hStyle.setFont(hFont);
            hStyle.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
            hStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            hStyle.setAlignment(HorizontalAlignment.CENTER);
            hStyle.setVerticalAlignment(VerticalAlignment.CENTER);
            hStyle.setWrapText(true);
            CellStyle paymentHStyle = workbook.createCellStyle();
            Font paymentHFont = workbook.createFont();
            paymentHFont.setBold(true);
            paymentHStyle.setFont(paymentHFont);
            paymentHStyle.setFillForegroundColor(IndexedColors.YELLOW.getIndex());
            paymentHStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            paymentHStyle.setAlignment(HorizontalAlignment.CENTER);
            paymentHStyle.setVerticalAlignment(VerticalAlignment.CENTER);
            paymentHStyle.setWrapText(true);
            // 收款信息列 (PayPal账号 开始, index 20~27) 使用黄色背景
            for (int i = 0; i < headers.length; ++i) {
                Cell cell = hRow.createCell(i);
                cell.setCellValue(headers[i]);
                if (i >= 20 && i <= 27) {
                    cell.setCellStyle(paymentHStyle);
                } else {
                    cell.setCellStyle(hStyle);
                }
                dataSheet.setColumnWidth(i, 5120);
            }
            Row exampleRow = dataSheet.createRow(1);
            exampleRow.createCell(2).setCellValue("example@influencer.com");
            exampleRow.createCell(0).setCellValue("Admin");
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            workbook.write(out);
            return ResponseEntity.ok()
                    .header("Content-Disposition", "attachment; filename=\"influencer_import_template.xlsx\"")
                    .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                    .body(out.toByteArray());
        }
        catch (Exception e) {
            log.error("Generate template failed", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    public InfluencerImportController(InfluencerImportService influencerImportService, ImportJobRepository importJobRepository, StorageServiceFactory storageServiceFactory) {
        this.influencerImportService = influencerImportService;
        this.importJobRepository = importJobRepository;
        this.storageServiceFactory = storageServiceFactory;
    }
}

