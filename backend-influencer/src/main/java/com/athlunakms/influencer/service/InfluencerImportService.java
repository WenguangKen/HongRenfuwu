package com.athlunakms.influencer.service;

import com.athlunakms.influencer.client.ShopifyIntegrationClient;
import com.athlunakms.influencer.client.UserServiceClient;
import com.athlunakms.influencer.entity.ImportJob;
import com.athlunakms.influencer.entity.Influencer;
import com.athlunakms.influencer.entity.InfluencerAddress;
import com.athlunakms.influencer.entity.InfluencerCooperation;
import com.athlunakms.influencer.entity.InfluencerPaymentInfo;
import com.athlunakms.influencer.entity.SystemTag;
import com.athlunakms.influencer.repository.ImportJobRepository;
import com.athlunakms.influencer.repository.InfluencerAddressRepository;
import com.athlunakms.influencer.repository.InfluencerPaymentInfoRepository;
import com.athlunakms.influencer.repository.InfluencerRepository;
import com.athlunakms.influencer.service.storage.StorageService;
import com.athlunakms.influencer.service.storage.StorageServiceFactory;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.BufferedInputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Scanner;
import java.util.concurrent.CompletableFuture;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.ss.usermodel.CellType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class InfluencerImportService {
    private static final Logger log = LoggerFactory.getLogger(InfluencerImportService.class);
    private final InfluencerRepository influencerRepository;
    private final InfluencerAddressRepository addressRepository;
    private final InfluencerPaymentInfoRepository paymentInfoRepository;
    private final JdbcTemplate jdbcTemplate;
    private final ObjectMapper objectMapper;
    private final ShopifyIntegrationClient shopifyIntegrationClient;
    private final InfluencerCooperationService cooperationService;
    private final ImportJobRepository importJobRepository;
    private final StorageServiceFactory storageServiceFactory;
    private final UserServiceClient userServiceClient;
    private final InfluencerLogService logService;

    @Transactional
    public Long createJob() {
        ImportJob job = new ImportJob();
        job.setStatus("PENDING");
        job.setType("INFLUENCER_IMPORT");
        job.setProcessedCount(0);
        job.setSuccessCount(0);
        job.setFailCount(0);
        job.setTotalCount(0);
        job = this.importJobRepository.save(job);
        return job.getId();
    }

    public void importAsync(String fileKey, String originalFilename, Long jobId, Long storeId, String stage) {
        CompletableFuture.runAsync(() -> this.processImport(fileKey, originalFilename, jobId, storeId, false, stage));
    }

    public void analyzeAsync(String fileKey, String originalFilename, Long jobId, Long storeId, String stage) {
        CompletableFuture.runAsync(() -> this.processImport(fileKey, originalFilename, jobId, storeId, true, stage));
    }

    // 用户名查找缓存，避免同一批导入中对同一个名字反复查库
    private final Map<String, Long> userNameCache = new HashMap<>();

    private Long findUserIdByNameCached(String name) {
        if (name == null || name.trim().isEmpty()) return null;
        String key = name.trim();
        if (userNameCache.containsKey(key)) return userNameCache.get(key);
        Long id = this.userServiceClient.findUserIdByName(key);
        userNameCache.put(key, id);
        return id;
    }

    private void processImport(String fileKey, String originalFilename, Long jobId, Long storeId, boolean dryRun, String stage) {
        ImportJob job = this.importJobRepository.findById(jobId).orElse(null);
        if (job == null) {
            return;
        }
        job.setStatus("PROCESSING");
        this.importJobRepository.save(job);
        userNameCache.clear(); // 每次导入清空缓存
        ImportResult result = new ImportResult();
        StorageService storageService = this.storageServiceFactory.getStorageService();
        try (InputStream is = storageService.getFileStream(fileKey)) {
            if (originalFilename != null && originalFilename.toLowerCase().endsWith(".csv")) {
                this.importCsvStream(is, job, result, dryRun, stage);
            } else {
                this.importExcelStream(is, job, result, dryRun, stage);
            }
            job.setStatus("COMPLETED");
            job.setSuccessCount(result.successCount);
            job.setFailCount(result.failCount);
            // 限制 warnings/errorMessages 数量，防止 resultJson 超出数据库列长度
            final int MAX_MESSAGES = 200;
            if (result.warnings.size() > MAX_MESSAGES) {
                int total = result.warnings.size();
                result.warnings = new ArrayList<>(result.warnings.subList(0, MAX_MESSAGES));
                result.warnings.add("... 共 " + total + " 条警告，仅显示前 " + MAX_MESSAGES + " 条");
            }
            if (result.errorMessages.size() > MAX_MESSAGES) {
                int total = result.errorMessages.size();
                result.errorMessages = new ArrayList<>(result.errorMessages.subList(0, MAX_MESSAGES));
                result.errorMessages.add("... 共 " + total + " 条错误，仅显示前 " + MAX_MESSAGES + " 条");
            }
            try {
                job.setResultJson(this.objectMapper.writeValueAsString(result));
            } catch (Exception e) {
                log.warn("Failed to serialize resultJson, saving without it", e);
            }
        } catch (Exception e) {
            log.error("Job {} Failed", jobId, e);
            job.setStatus("FAILED");
            job.setErrorMessage(e.getMessage());
        } finally {
            try {
                this.importJobRepository.save(job);
            } catch (Exception e) {
                log.error("Job {} save failed, trying without resultJson", jobId, e);
                // 兜底：去掉 resultJson 再保存一次，确保 COMPLETED 状态能写入
                try {
                    job.setResultJson(null);
                    this.importJobRepository.save(job);
                } catch (Exception e2) {
                    log.error("Job {} final save also failed", jobId, e2);
                }
            }
            if (!dryRun) {
                try {
                    storageService.delete(fileKey);
                } catch (Exception e) {
                    log.warn("Failed to delete import file: {}", fileKey, e);
                }
            }
        }
    }

    private void importCsvStream(InputStream is, ImportJob job, ImportResult result, boolean dryRun, String stage) {
        try (Scanner scanner = new Scanner(is, "UTF-8")) {
            int physicalRowNum = 0;
            int logicalRowNum = 0;
            Map<String, Integer> headerMap = new HashMap<>();
            StringBuilder currentRecordLine = new StringBuilder();
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                ++physicalRowNum;
                if (currentRecordLine.length() > 0) {
                    currentRecordLine.append("\n");
                }
                currentRecordLine.append(line);
                String fullRecordLine = currentRecordLine.toString();
                boolean isOpenQuote = false;
                boolean isStartOfField = true;
                for (int i = 0; i < fullRecordLine.length(); ++i) {
                    char c = fullRecordLine.charAt(i);
                    if (c == '\"') {
                        if (isStartOfField) {
                            isOpenQuote = true;
                            isStartOfField = false;
                            continue;
                        }
                        if (!isOpenQuote)
                            continue;
                        if (i + 1 < fullRecordLine.length() && fullRecordLine.charAt(i + 1) == '\"') {
                            ++i;
                            continue;
                        }
                        isOpenQuote = false;
                        continue;
                    }
                    if (c == ',' && !isOpenQuote) {
                        isStartOfField = true;
                        continue;
                    }
                    if (Character.isWhitespace(c))
                        continue;
                    isStartOfField = false;
                }
                if (isOpenQuote)
                    continue;
                if (++logicalRowNum == 1) {
                    currentRecordLine.setLength(0);
                    continue;
                }
                currentRecordLine.setLength(0);
                if (fullRecordLine.trim().isEmpty())
                    continue;
                job.setTotalCount(job.getTotalCount() == null ? 1 : job.getTotalCount() + 1);
                try {
                    List<String> record = this.parseCsvLine(fullRecordLine);
                    if (logicalRowNum == 1) { // 如果是表头行
                        for (int i = 0; i < record.size(); i++) {
                            headerMap.put(record.get(i).trim(), i);
                        }
                        currentRecordLine.setLength(0);
                        continue;
                    }
                    boolean isEmpty = true;
                    for (String val : record) {
                        if (val != null && !val.trim().isEmpty()) {
                            isEmpty = false;
                            break;
                        }
                    }
                    if (isEmpty)
                        continue;
                    this.processRecord(record, headerMap, result, dryRun, stage);
                    ++result.successCount;
                } catch (Exception e) {
                    log.error("Logical Row {} import failed", logicalRowNum, e);
                    ++result.failCount;
                    // 格式化错误信息以满足用户显示需求
                    result.errorMessages.add("Line " + logicalRowNum + ": " + e.getMessage());
                }
                if (!dryRun) {
                    job.setProcessedCount(logicalRowNum - 1);
                    if (logicalRowNum % 20 != 0)
                        continue;
                    this.importJobRepository.saveAndFlush(job);
                    continue;
                }
                job.setProcessedCount(logicalRowNum - 1);
                if (logicalRowNum % 50 != 0)
                    continue;
                this.importJobRepository.saveAndFlush(job);
            }
        } catch (Exception e) {
            throw new RuntimeException("CSV parse error: " + e.getMessage());
        }
    }

    private void importExcelStream(InputStream is, ImportJob job, ImportResult result, boolean dryRun, String stage) {
        try (BufferedInputStream bis = new BufferedInputStream(is);
                Workbook workbook = WorkbookFactory.create(bis)) {

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

            int totalRows = sheet.getLastRowNum();
            job.setTotalCount(totalRows);
            this.importJobRepository.save(job);
            Map<String, Integer> headerMap = new HashMap<>();
            Iterator<Row> rowIterator = sheet.rowIterator();
            if (rowIterator.hasNext()) {
                Row headerRow = rowIterator.next();
                int maxCol = headerRow.getLastCellNum();
                for (int i = 0; i < maxCol; i++) {
                    String header = this.getCellValue(headerRow, i);
                    if (header != null && !header.isEmpty()) {
                        headerMap.put(header.trim(), i);
                    }
                }
            }
            int rowNum = 1;
            while (rowIterator.hasNext()) {
                ++rowNum;
                Row row = rowIterator.next();
                // 先更新进度（含空行），避免尾部空行导致进度卡住
                job.setProcessedCount(rowNum - 1);
                if ((rowNum - 1) % 20 == 0) {
                    this.importJobRepository.saveAndFlush(job);
                }
                try {
                    int maxCol = Math.max(47, headerMap.values().stream().max(Integer::compareTo).orElse(0) + 1);
                    List<String> record = this.extractRowData(row, maxCol);
                    boolean isEmpty = true;
                    for (String val : record) {
                        if (val != null && !val.trim().isEmpty()) {
                            isEmpty = false;
                            break;
                        }
                    }
                    if (isEmpty)
                        continue;
                    this.processRecord(record, headerMap, result, dryRun, stage);
                    ++result.successCount;
                } catch (Exception e) {
                    log.error("Row {} import failed: {}", rowNum, e.getMessage());
                    ++result.failCount;
                    // 格式化错误信息以满足用户显示需求
                    result.errorMessages.add("Line " + rowNum + ": " + e.getMessage());
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Excel parse error: " + e.getMessage());
        }
    }

    private List<String> extractRowData(Row row, int maxCol) {
        List<String> data = new ArrayList<>();
        for (int i = 0; i < maxCol; ++i) {
            data.add(this.getCellValue(row, i));
        }
        return data;
    }

    private void processRecord(List<String> record, Map<String, Integer> headerMap, ImportResult result,
            boolean dryRun, String stage) {
        String phone;
        String description;
        Long userId;
        String sourceHashtag;
        String source;
        String type;
        String race;
        String country;
        String backupEmail;
        boolean isNew;
        String email = this.getValueByHeader(headerMap, record, "Email", 2);  // col 2
        if (email == null)
            email = this.getValueByHeader(headerMap, record, "红人邮箱(必填)", 2);
        if (email == null)
            email = this.getValueByHeader(headerMap, record, "邮箱", 2);
        if (email != null) email = email.trim();
        if (email != null && email.isEmpty()) email = null;

        // 1. 提取社媒信息和 Handle
        String igUrl = this.getValueByHeader(headerMap, record, "IG link", 4);
        if (igUrl == null)
            igUrl = this.getValueByHeader(headerMap, record, "Instagram", 4);

        String ttUrl = this.getValueByHeader(headerMap, record, "TT link", 7);
        if (ttUrl == null)
            ttUrl = this.getValueByHeader(headerMap, record, "TikTok", 7);

        String ytUrl = this.getValueByHeader(headerMap, record, "YT link", 10);
        if (ytUrl == null)
            ytUrl = this.getValueByHeader(headerMap, record, "YouTube", 10);

        // 新增：识别账号名列并优先提取 Handle
        String igAccount = this.getValueByHeader(headerMap, record, "IG handle", 3);
        if (igAccount == null) igAccount = this.getValueByHeader(headerMap, record, "IG账号名", 3);
        String ttAccount = this.getValueByHeader(headerMap, record, "TT handle", 6);
        if (ttAccount == null) ttAccount = this.getValueByHeader(headerMap, record, "TT账号名", 6);
        String ytAccount = this.getValueByHeader(headerMap, record, "Youtube handle", 9);
        if (ytAccount == null) ytAccount = this.getValueByHeader(headerMap, record, "YT账号名", 9);

        String igHandle = (igAccount != null && !igAccount.trim().isEmpty()) ? this.extractHandle(igAccount)
                : this.extractHandle(igUrl);
        String ttHandle = (ttAccount != null && !ttAccount.trim().isEmpty()) ? this.extractHandle(ttAccount)
                : this.extractHandle(ttUrl);
        String ytHandle = (ytAccount != null && !ytAccount.trim().isEmpty()) ? this.extractHandle(ytAccount)
                : this.extractHandle(ytUrl);

        // 2. 优先级确定识别标识: INS > TK > YTB (用于去重和展示)
        String primaryPlatform = null;
        String primaryHandle = null;
        String primaryUrl = null;

        if (igHandle != null && !igHandle.isEmpty()) {
            primaryPlatform = "INSTAGRAM";
            primaryHandle = igHandle;
            primaryUrl = igUrl;
        } else if (ttHandle != null && !ttHandle.isEmpty()) {
            primaryPlatform = "TIKTOK";
            primaryHandle = ttHandle;
            primaryUrl = ttUrl;
        } else if (ytHandle != null && !ytHandle.isEmpty()) {
            primaryPlatform = "YOUTUBE";
            primaryHandle = ytHandle;
            primaryUrl = ytUrl;
        }

        // 3. 校验必填项：如果 INS/TK/YTB 全都没有，则上传失败
        if (primaryPlatform == null) {
            String errorInfo = (email != null ? email : "未知邮箱") + " | 信息缺失";
            throw new IllegalArgumentException(errorInfo);
        }

        // 4. 先按 平台 + Handle 查重（跨资源池+红人列表，只要存在就跳过）
        Optional<Influencer> existingByPlatformHandle = this.influencerRepository
                .findFirstByDefaultPlatformAndDefaultHandle(primaryPlatform, primaryHandle);
        if (existingByPlatformHandle.isPresent()) {
            Influencer existing = existingByPlatformHandle.get();
            result.getWarnings().add("跳过: " + primaryPlatform + " @" + primaryHandle
                    + (email != null ? " (" + email + ")" : "")
                    + " — 已存在 (红人ID:" + existing.getId() + ")");

            // 即使跳过，也补充社媒信息（如果缺失）
            if (!dryRun) {
                Long igFansVal = this.parseLong(this.getValueByHeader(headerMap, record, "IG Followers", 5));
                if (igFansVal == null) igFansVal = this.parseLong(this.getValueByHeader(headerMap, record, "IG粉丝量", 5));
                if (igFansVal == null) igFansVal = this.parseLong(this.getValueByHeader(headerMap, record, "Instagram Followers", 5));
                Long ttFansVal = this.parseLong(this.getValueByHeader(headerMap, record, "TT Followers", 8));
                if (ttFansVal == null) ttFansVal = this.parseLong(this.getValueByHeader(headerMap, record, "TT粉丝量", 8));
                if (ttFansVal == null) ttFansVal = this.parseLong(this.getValueByHeader(headerMap, record, "TikTok Followers", 8));
                Long ytFansVal = this.parseLong(this.getValueByHeader(headerMap, record, "YT Followers", 11));
                if (ytFansVal == null) ytFansVal = this.parseLong(this.getValueByHeader(headerMap, record, "YT粉丝量", 11));
                if (ytFansVal == null) ytFansVal = this.parseLong(this.getValueByHeader(headerMap, record, "YouTube Followers", 11));

                String igUrlVal = this.getValueByHeader(headerMap, record, "IG link", 4);
                if (igUrlVal == null) igUrlVal = this.getValueByHeader(headerMap, record, "Instagram", 4);
                this.updateSocialMedia(existing.getId(), "INSTAGRAM", igUrlVal, igHandle, igFansVal);

                String ttUrlVal = this.getValueByHeader(headerMap, record, "TT link", 7);
                if (ttUrlVal == null) ttUrlVal = this.getValueByHeader(headerMap, record, "TikTok", 7);
                this.updateSocialMedia(existing.getId(), "TIKTOK", ttUrlVal, ttHandle, ttFansVal);

                String ytUrlVal = this.getValueByHeader(headerMap, record, "YT link", 10);
                if (ytUrlVal == null) ytUrlVal = this.getValueByHeader(headerMap, record, "YouTube", 10);
                this.updateSocialMedia(existing.getId(), "YOUTUBE", ytUrlVal, ytHandle, ytFansVal);

                // 同步 totalFans
                Long maxFansVal = igFansVal;
                if (ttFansVal != null && (maxFansVal == null || ttFansVal > maxFansVal)) maxFansVal = ttFansVal;
                if (ytFansVal != null && (maxFansVal == null || ytFansVal > maxFansVal)) maxFansVal = ytFansVal;

                // 补充更新负责人和联络员（即使红人已存在也需要同步）
                boolean needSave = false;
                String existOwnerName = this.getValueByHeader(headerMap, record, "负责人", 0);
                if (existOwnerName == null) existOwnerName = this.getValueByHeader(headerMap, record, "Owner", 0);
                if (existOwnerName == null) existOwnerName = this.getValueByHeader(headerMap, record, "负责人员", 0);
                if (existOwnerName != null && !existOwnerName.trim().isEmpty()) {
                    Long ownerUserId = this.findUserIdByNameCached(existOwnerName);
                    if (ownerUserId != null) {
                        existing.setOwnerId(ownerUserId);
                        needSave = true;
                    }
                }

                String existLiaisonName = this.getValueByHeader(headerMap, record, "联络员", 1);
                if (existLiaisonName == null) existLiaisonName = this.getValueByHeader(headerMap, record, "Pic", 1);
                if (existLiaisonName == null) existLiaisonName = this.getValueByHeader(headerMap, record, "PIC", 1);
                if (existLiaisonName == null) existLiaisonName = this.getValueByHeader(headerMap, record, "Pic (联络员)", 1);
                if (existLiaisonName == null) existLiaisonName = this.getValueByHeader(headerMap, record, "Liaison", 1);
                if (existLiaisonName == null) existLiaisonName = this.getValueByHeader(headerMap, record, "联络人", 1);
                if (existLiaisonName != null && !existLiaisonName.trim().isEmpty()) {
                    Long liaisonUserId = this.findUserIdByNameCached(existLiaisonName);
                    if (liaisonUserId != null) {
                        existing.setContactPersonId(liaisonUserId);
                        needSave = true;
                    }
                }

                // 联络员标签同步
                if (existLiaisonName != null && !existLiaisonName.trim().isEmpty()) {
                    try {
                        List<Long> tagIds = new ArrayList<>();
                        if (existing.getTags() != null && !existing.getTags().isEmpty()) {
                            tagIds = new ArrayList<>(this.objectMapper.readValue(existing.getTags(), new com.fasterxml.jackson.core.type.TypeReference<List<Long>>() {}));
                        }
                        this.addTag(tagIds, existLiaisonName, "LIAISON", "PIC");
                        existing.setTags(this.objectMapper.writeValueAsString(tagIds));
                        needSave = true;
                    } catch (Exception e) {
                        log.warn("Failed to update liaison tag for existing influencer {}: {}", existing.getId(), e.getMessage());
                    }
                }

                if (maxFansVal != null && (existing.getTotalFans() == null || existing.getTotalFans() == 0)) {
                    existing.setTotalFans(maxFansVal);
                    needSave = true;
                }

                if (needSave) {
                    this.influencerRepository.save(existing);
                }
            }
            return;
        }

        // 5. 根据 Email + 平台 + Handle 查重（同一红人更新，仅当 email 非空时）
        Influencer influencer = null;
        if (email != null && !email.isEmpty()) {
            influencer = this.influencerRepository
                    .findByEmailAndDefaultPlatformAndDefaultHandle(email, primaryPlatform, primaryHandle)
                    .orElse(null);
        }

        isNew = (influencer == null);

        Influencer.Status mappedStatus = Influencer.Status.PENDING;
        String statusStr = this.getValueByHeader(headerMap, record, "红人状态", -1);  // no fixed column, header-only lookup
        boolean hasStatusUpdate = false;
        if (statusStr != null && !statusStr.trim().isEmpty()) {
            hasStatusUpdate = true;
            try {
                String s = statusStr.trim().toUpperCase();
                if (s.contains("待联系") || s.equals("PENDING")) mappedStatus = Influencer.Status.PENDING;
                else if (s.contains("已联系") || s.equals("CONTACTED")) mappedStatus = Influencer.Status.CONTACTED;
                else if (s.contains("沟通中") || s.equals("COMMUNICATING")) mappedStatus = Influencer.Status.COMMUNICATING;
                else if (s.contains("合作中") || s.equals("COOPERATING")) mappedStatus = Influencer.Status.COOPERATING;
                else if (s.contains("休眠中") || s.equals("DORMANT")) mappedStatus = Influencer.Status.DORMANT;
                else if (s.contains("暂不合作") || s.equals("PAUSED")) mappedStatus = Influencer.Status.PAUSED;
                else if (s.contains("黑名单") || s.equals("BLACKLIST")) mappedStatus = Influencer.Status.BLACKLIST;
                else if (s.contains("不再合作") || s.equals("TERMINATED")) mappedStatus = Influencer.Status.TERMINATED;
                else mappedStatus = Influencer.Status.valueOf(s);
            } catch (Exception e) {
                log.warn("Invalid influencer status: {}, defaulting to PENDING", statusStr);
            }
        }

        if (isNew) {
            influencer = new Influencer();
            if (email != null) influencer.setEmail(email);
            influencer.setDefaultPlatform(primaryPlatform);
            influencer.setDefaultHandle(primaryHandle);
            influencer.setDefaultUrl(primaryUrl);
            influencer.setCreatedAt(LocalDateTime.now());
            influencer.setOrigin(Influencer.Origin.IMPORT);
            if ("POOL".equalsIgnoreCase(stage)) {
                influencer.setStage(Influencer.Stage.POOL);
                influencer.setStatus(Influencer.Status.UNSCREENED);
            } else {
                influencer.setStage(Influencer.Stage.ONBOARDED);
                influencer.setStatus(mappedStatus);
            }
            result.setAddedCount(result.getAddedCount() + 1);
        } else {
            if (hasStatusUpdate) {
                influencer.setStatus(mappedStatus);
            }
            // 修复：如果现有红人缺失 Handle 信息，则从当前导入行中补全
            if (influencer.getDefaultHandle() == null || influencer.getDefaultHandle().trim().isEmpty()) {
                influencer.setDefaultPlatform(primaryPlatform);
                influencer.setDefaultHandle(primaryHandle);
                influencer.setDefaultUrl(primaryUrl);
                log.info("Synced missing handle for existing influencer {}: {}", influencer.getId(), primaryHandle);
            }
            ++result.updatedCount;
        }
        String fullName = this.getValueByHeader(headerMap, record, "红人全名", 15);  // col 15
        if (fullName == null)
            fullName = this.getValueByHeader(headerMap, record, "全名", 15);
        if (fullName != null) {
            influencer.setRealName(fullName);
        }
        if ((backupEmail = this.getValueByHeader(headerMap, record, "红人备用邮箱", -1)) == null) {
            backupEmail = this.getValueByHeader(headerMap, record, "备用邮箱", -1);  // no fixed column
        }
        if (backupEmail != null) {
            influencer.setBackupEmail(backupEmail);
        }
        if ((country = this.getValueByHeader(headerMap, record, "Country", 16)) == null) {  // col 16
            if ((country = this.getValueByHeader(headerMap, record, "国家", 16)) == null)
                country = this.getValueByHeader(headerMap, record, "国家/地区", 16);
        }
        if (country != null) {
            influencer.setCountry(country);
        }
        if ((race = this.getValueByHeader(headerMap, record, "人种", -1)) != null) {  // no fixed column
            // 自动映射旧值 Caucasian → White
            if ("Caucasian".equalsIgnoreCase(race.trim())) {
                race = "White";
            }
            influencer.setRace(race);
        }
        if ((type = this.getValueByHeader(headerMap, record, "红人分类", 12)) == null) {  // col 12
            if ((type = this.getValueByHeader(headerMap, record, "红人类型", 12)) == null)
                type = this.getValueByHeader(headerMap, record, "红人类型（Category）", 12);
        }
        if (type != null) {
            influencer.setInfluencerType(type);
        }
        // 红人风格 col 13 — 将在 dryRun=false 分支中作为 tag 保存
        String style = this.getValueByHeader(headerMap, record, "红人风格", 13);
        if ((source = this.getValueByHeader(headerMap, record, "来源", -1)) == null) {
            source = this.getValueByHeader(headerMap, record, "Source", -1);  // no fixed column
        }
        if (source != null) {
            influencer.setSource(source);
        }
        if ((sourceHashtag = this.getValueByHeader(headerMap, record, "来源hashtag", -1)) == null) {
            sourceHashtag = this.getValueByHeader(headerMap, record, "Source#", -1);  // no fixed column
        }
        if (sourceHashtag != null) {
            influencer.setSourceValue(sourceHashtag);
        }
        String ownerName = this.getValueByHeader(headerMap, record, "负责人", 0);  // col 0
        if (ownerName == null) ownerName = this.getValueByHeader(headerMap, record, "Owner", 0);
        if (ownerName == null) ownerName = this.getValueByHeader(headerMap, record, "负责人员", 0);

        String liaisonName = this.getValueByHeader(headerMap, record, "联络员", 1);  // col 1
        if (liaisonName == null) liaisonName = this.getValueByHeader(headerMap, record, "Pic", 1);
        if (liaisonName == null) liaisonName = this.getValueByHeader(headerMap, record, "PIC", 1);
        if (liaisonName == null) liaisonName = this.getValueByHeader(headerMap, record, "Pic (联络员)", 1);
        if (liaisonName == null) liaisonName = this.getValueByHeader(headerMap, record, "Liaison", 1);
        if (liaisonName == null) liaisonName = this.getValueByHeader(headerMap, record, "联络人", 1);

        if (ownerName != null) {
            userId = this.findUserIdByNameCached(ownerName);
            if (userId != null) {
                influencer.setOwnerId(userId);
            }
        }
        if (liaisonName != null) {
            userId = this.findUserIdByNameCached(liaisonName);
            if (userId != null) {
                influencer.setContactPersonId(userId);
            }
        }
        Long igFans = this.parseLong(this.getValueByHeader(headerMap, record, "IG Followers", 5));  // col 5
        if (igFans == null)
            igFans = this.parseLong(this.getValueByHeader(headerMap, record, "IG粉丝量", 5));
        if (igFans == null)
            igFans = this.parseLong(this.getValueByHeader(headerMap, record, "Instagram Followers", 5));

        Long ttFans = this.parseLong(this.getValueByHeader(headerMap, record, "TT Followers", 8));  // col 8
        if (ttFans == null)
            ttFans = this.parseLong(this.getValueByHeader(headerMap, record, "TT粉丝量", 8));
        if (ttFans == null)
            ttFans = this.parseLong(this.getValueByHeader(headerMap, record, "TikTok Followers", 8));

        Long ytFans = this.parseLong(this.getValueByHeader(headerMap, record, "YT Followers", 11));  // col 11
        if (ytFans == null)
            ytFans = this.parseLong(this.getValueByHeader(headerMap, record, "YT粉丝量", 11));
        if (ytFans == null)
            ytFans = this.parseLong(this.getValueByHeader(headerMap, record, "YouTube Followers", 11));

        Long maxFans = null;
        if (igFans != null) {
            maxFans = igFans;
        }
        if (ttFans != null && (maxFans == null || ttFans > maxFans)) {
            maxFans = ttFans;
        }
        if (ytFans != null && (maxFans == null || ytFans > maxFans)) {
            maxFans = ytFans;
        }
        if (maxFans != null) {
            influencer.setTotalFans(maxFans);
        }
        if ((description = this.getValueByHeader(headerMap, record, "备注", -1)) != null) {  // no fixed column
            influencer.setDescription(description);
        }
        if ((phone = this.getValueByHeader(headerMap, record, "Phone number", 18)) == null) {  // col 18
            if ((phone = this.getValueByHeader(headerMap, record, "红人联系电话", 18)) == null)
                phone = this.getValueByHeader(headerMap, record, "电话号码", 18);
        }
        if (phone != null) {
            if (phone != null && phone.length() > 32) {
                phone = phone.substring(0, 32);
            }
            influencer.setPhone(phone);
        }
        if (!dryRun) {
            String addressStr;
            String orderNosStr;
            influencer = this.influencerRepository.save(influencer);

            igUrl = this.getValueByHeader(headerMap, record, "IG link", 4);
            if (igUrl == null)
                igUrl = this.getValueByHeader(headerMap, record, "Instagram", 4);
            this.updateSocialMedia(influencer.getId(), "INSTAGRAM", igUrl, igHandle, igFans);

            ttUrl = this.getValueByHeader(headerMap, record, "TT link", 7);
            if (ttUrl == null)
                ttUrl = this.getValueByHeader(headerMap, record, "TikTok", 7);
            this.updateSocialMedia(influencer.getId(), "TIKTOK", ttUrl, ttHandle, ttFans);

            ytUrl = this.getValueByHeader(headerMap, record, "YT link", 10);
            if (ytUrl == null)
                ytUrl = this.getValueByHeader(headerMap, record, "YouTube", 10);
            this.updateSocialMedia(influencer.getId(), "YOUTUBE", ytUrl, ytHandle, ytFans);

            String tagsStr = this.getValueByHeader(headerMap, record, "红人标签", -1);  // no fixed column
            if (tagsStr == null)
                tagsStr = this.getValueByHeader(headerMap, record, "内容产出标签（已合作标签）", -1);
            {
                List<Long> tagIds = new ArrayList<>();
                if (tagsStr != null) {
                    this.addTag(tagIds, tagsStr, "INFLUENCER", "Tag");
                }
                if (liaisonName != null) {
                    this.addTag(tagIds, liaisonName, "LIAISON", "PIC");
                }
                if (style != null) {
                    this.addTag(tagIds, style, "STYLE", "Style");
                }
                if (!tagIds.isEmpty()) {
                    try {
                        influencer.setTags(this.objectMapper.writeValueAsString(tagIds));
                        this.influencerRepository.save(influencer);
                    } catch (JsonProcessingException e) {
                        log.warn("Failed to process tags JSON", e);
                    }
                }
            }
            String costValue = this.getValueByHeader(headerMap, record, "Cost", 14);  // col 14
            if (costValue == null) costValue = this.getValueByHeader(headerMap, record, "付费金额", 14);
            if (costValue != null && !costValue.trim().isEmpty()) {
                try {
                    String normalizedCost = costValue.trim().replaceAll("[^0-9.]", "");
                    if (!normalizedCost.isEmpty()) {
                        BigDecimal cost = new BigDecimal(normalizedCost);
                        InfluencerCooperation coop = new InfluencerCooperation();
                        coop.setInfluencerId(influencer.getId());
                        coop.setCooperationMode("PAID");
                        coop.setAmount(cost);
                        coop.setCurrency("USD");
                        coop.setPaymentMethod("Other");
                        coop.setPaidAt(influencer.getCreatedAt());
                        coop.setRemark("Imported payment");
                        this.cooperationService.createCooperation(coop);
                        log.info("Created legacy paid cooperation record for new format influencer {} amount={}",
                                influencer.getId(), cost);
                    }
                } catch (Exception e) {
                    log.warn("Invalid cost value for influencer {}: {}", influencer.getId(), costValue);
                    result.getWarnings().add("付费金额格式错误('" + costValue + "')已跳过自动创建合作记录");
                }
            }
            String paypalAccount = this.getValueByHeader(headerMap, record, "PayPal账号", 20);  // col 20
            String bankName = this.getValueByHeader(headerMap, record, "收款银行名称", 21);  // col 21
            String branchName = this.getValueByHeader(headerMap, record, "收款支行名称(选填)", 22);  // col 22
            String bankAddress = this.getValueByHeader(headerMap, record, "收款银行地址", 23);  // col 23
            String swiftCode = this.getValueByHeader(headerMap, record, "Swift Code(非US地区必填)", 24);  // col 24
            String routingNumber = this.getValueByHeader(headerMap, record, "Routing Number(ACH,US美国地区)", 25);  // col 25
            String accountName = this.getValueByHeader(headerMap, record, "收款银行账户名", 26);  // col 26
            String accountNumber = this.getValueByHeader(headerMap, record, "收款银行账户号码", 27);  // col 27
            if (paypalAccount != null || bankName != null || accountNumber != null || swiftCode != null
                    || routingNumber != null) {
                try {
                    InfluencerPaymentInfo paymentInfo = this.paymentInfoRepository
                            .findByInfluencerId(influencer.getId()).orElse(new InfluencerPaymentInfo());
                    paymentInfo.setInfluencerId(influencer.getId());
                    if (paypalAccount != null) {
                        paymentInfo.setPaypalAccount(paypalAccount);
                    }
                    if (bankName != null) {
                        paymentInfo.setBankName(bankName);
                    }
                    if (branchName != null) {
                        paymentInfo.setBranchName(branchName);
                    }
                    if (bankAddress != null) {
                        paymentInfo.setBankAddress(bankAddress);
                    }
                    if (swiftCode != null) {
                        paymentInfo.setSwiftCode(swiftCode);
                    }
                    if (routingNumber != null) {
                        paymentInfo.setRoutingNumber(routingNumber);
                    }
                    if (accountName != null) {
                        paymentInfo.setAccountName(accountName);
                    }
                    if (accountNumber != null) {
                        paymentInfo.setAccountNumber(accountNumber);
                    }
                    this.paymentInfoRepository.save(paymentInfo);
                } catch (Exception e) {
                    log.warn("Failed to save payment info for influencer {}: {}", influencer.getId(), e.getMessage());
                    result.getWarnings().add("收款信息保存失败: " + e.getMessage());
                }
            }
            if ((orderNosStr = this.getValueByHeader(headerMap, record, "已下订单号(多个用逗号)", -1)) == null) {  // no fixed column
                orderNosStr = this.getValueByHeader(headerMap, record, "系统已有发货单号（支持多个）", -1);
            }
            if (orderNosStr != null) {
                String[] orderNos = orderNosStr.split("[,\uff0c]");
                for (String orderNo : orderNos) {
                    orderNo = orderNo.trim();
                    if (orderNo.isEmpty())
                        continue;
                    try {
                        this.bindOrder(influencer, orderNo);
                    } catch (Exception e) {
                        log.warn("Order binding failed for '{}': {}", orderNo, e.getMessage());
                        result.getWarnings().add("订单 '" + orderNo + "' 绑定失败: " + e.getMessage());
                    }
                }
            }
            if ((addressStr = this.getValueByHeader(headerMap, record, "Address Line", 17)) == null) {  // col 17
                if ((addressStr = this.getValueByHeader(headerMap, record, "红人收货地址", 17)) == null)
                    addressStr = this.getValueByHeader(headerMap, record, "收货地址", 17);
            }
            if (addressStr != null) {
                String recipientName = influencer.getRealName();
                this.updateAddress(influencer.getId(), addressStr, recipientName, phone, country);
            }
            String cooperationMode = this.getValueByHeader(headerMap, record, "合作模式(PAID/COMMISSION)", -1);  // no fixed column
            String costValueV2 = this.getValueByHeader(headerMap, record, "Cost", 14);
            if (costValueV2 == null) costValueV2 = this.getValueByHeader(headerMap, record, "付费金额", 14);

            // 只要有任意合作相关信息就尝试创建记录
            if (cooperationMode != null || costValueV2 != null) {
                try {
                    InfluencerCooperation coop = new InfluencerCooperation();
                    coop.setInfluencerId(influencer.getId());
                    coop.setCooperationMode(cooperationMode != null ? cooperationMode.toUpperCase().trim() : "PAID");
                    coop.setCurrency("USD");
                    coop.setRemark("Imported");
                    coop.setPaidAt(LocalDateTime.now());

                    if (costValueV2 != null) {
                        String normalizedCost = costValueV2.trim().replaceAll("[^0-9.]", "");
                        if (!normalizedCost.isEmpty()) {
                            try {
                                coop.setAmount(new BigDecimal(normalizedCost));
                            } catch (Exception e) {
                                log.warn("Invalid cost value: {}", costValueV2);
                            }
                        }
                    }
                    this.cooperationService.createCooperation(coop);
                    log.debug("Created cooperation record for influencer {} mode={} amount={}", influencer.getId(),
                            coop.getCooperationMode(), coop.getAmount());
                } catch (Exception e) {
                    log.warn("Failed to create cooperation for influencer {}: {}", influencer.getId(), e.getMessage());
                    result.getWarnings().add("付费合作记录创建失败: " + e.getMessage());
                }
            }
        }
    }

    private List<String> parseCsvLine(String line) {
        List<String> result = new ArrayList<>();
        if (line == null) {
            return result;
        }
        StringBuilder current = new StringBuilder();
        boolean inQuotes = false;
        boolean expectedNextCharIsField = true;
        for (int i = 0; i < line.length(); ++i) {
            char c = line.charAt(i);
            if (c == '\"') {
                if (expectedNextCharIsField && !inQuotes && current.toString().trim().isEmpty()) {
                    inQuotes = true;
                    expectedNextCharIsField = false;
                    continue;
                }
                if (inQuotes) {
                    if (i + 1 < line.length() && line.charAt(i + 1) == '\"') {
                        current.append('\"');
                        ++i;
                        continue;
                    }
                    inQuotes = false;
                    continue;
                }
                current.append(c);
                expectedNextCharIsField = false;
                continue;
            }
            if (c == ',' && !inQuotes) {
                result.add(current.toString().trim());
                current.setLength(0);
                expectedNextCharIsField = true;
                continue;
            }
            current.append(c);
            if (Character.isWhitespace(c))
                continue;
            expectedNextCharIsField = false;
        }
        result.add(current.toString().trim());
        return result;
    }

    private String getValueByHeader(Map<String, Integer> headerMap, List<String> record, String headerName,
            int fallbackIndex) {
        if (headerMap != null && headerMap.containsKey(headerName)) {
            int index = headerMap.get(headerName);
            if (index < record.size()) {
                return record.get(index);
            }
        }
        if (fallbackIndex >= 0 && fallbackIndex < record.size()) {
            String val = record.get(fallbackIndex);
            return val == null || val.trim().isEmpty() || "null".equalsIgnoreCase(val) ? null : val.trim();
        }
        return null;
    }

    private void updateAddress(Long influencerId, String addressStr, String name, String phone, String country) {
        if (addressStr == null || addressStr.trim().isEmpty()) {
            return;
        }
        List<InfluencerAddress> addresses = this.addressRepository.findByInfluencerId(influencerId);
        if (addresses.stream().noneMatch(a -> addressStr.equals(a.getAddress()))) {
            InfluencerAddress address = new InfluencerAddress();
            address.setInfluencerId(influencerId);
            address.setAddress(addressStr);
            address.setCountry(country);
            address.setRecipientName(name != null ? name : "Default");
            address.setPhone(phone);
            address.setIsDefault(addresses.isEmpty());
            address.setCreatedAt(LocalDateTime.now());
            address.setUpdatedAt(LocalDateTime.now());
            this.addressRepository.save(address);
        }
    }

    private void updateSocialMedia(Long influencerId, String platform, String url, String handle, Long followerCount) {
        if ((url == null || url.trim().isEmpty()) && (handle == null || handle.trim().isEmpty()) && followerCount == null) {
            return;
        }
        String countSql = "SELECT count(1) FROM influencer_social_media WHERE influencer_id = ? AND platform = ?";
        Integer count = this.jdbcTemplate.queryForObject(countSql, Integer.class, influencerId, platform);
        if (count != null && count > 0) {
            this.jdbcTemplate.update(
                    "UPDATE influencer_social_media SET url = COALESCE(?, url), handle = COALESCE(?, handle), follower_count = COALESCE(?, follower_count), updated_at = NOW() WHERE influencer_id = ? AND platform = ?",
                    url, handle, followerCount, influencerId, platform);
        } else {
            this.jdbcTemplate.update(
                    "INSERT INTO influencer_social_media (influencer_id, platform, url, handle, follower_count, created_at, updated_at) VALUES (?, ?, ?, ?, ?, NOW(), NOW())",
                    influencerId, platform, url, handle, followerCount);
        }
    }

    private String extractHandle(String input) {
        if (input == null || input.trim().isEmpty()) {
            return null;
        }
        String val = input.trim();

        // 1. 如果包含 http/https，说明是 URL，从中截取
        if (val.toLowerCase().contains("http")) {
            val = val.replaceAll("/$", "");
            int lastSlash = val.lastIndexOf('/');
            if (lastSlash >= 0 && lastSlash < val.length() - 1) {
                val = val.substring(lastSlash + 1);
            }
        }

        // 2. 去除 Query 参数
        int queryIdx = val.indexOf('?');
        if (queryIdx >= 0) {
            val = val.substring(0, queryIdx);
        }

        // 3. 去除首字母 @
        return val.startsWith("@") ? val.substring(1).trim() : val.trim();
    }

    private void bindOrder(Influencer influencer, String orderNo) {
        if (!this.shopifyIntegrationClient.checkOrderExists(orderNo)) {
            throw new RuntimeException("Order " + orderNo + " not found");
        }
        try {
            this.shopifyIntegrationClient.bindInfluencer(orderNo, influencer.getId(), influencer.getRealName());
        } catch (Exception e) {
            throw new RuntimeException("Failed to bind order: " + e.getMessage());
        }
    }

    private String getCellValue(Row row, int index) {
        Cell cell = row.getCell(index);
        if (cell == null) {
            return null;
        }
        DataFormatter formatter = new DataFormatter();
        CellType cellType = cell.getCellType();
        switch (cellType) {
            case STRING: {
                return cell.getStringCellValue();
            }
            case NUMERIC: {
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getDateCellValue().toString();
                }
                return formatter.formatCellValue(cell);
            }
            case BOOLEAN: {
                return String.valueOf(cell.getBooleanCellValue());
            }
            case FORMULA: {
                try {
                    return formatter.formatCellValue(cell);
                } catch (Exception e) {
                    try {
                        return cell.getStringCellValue();
                    } catch (Exception e2) {
                        return String.valueOf(cell.getNumericCellValue());
                    }
                }
            }
            default: {
                return null;
            }
        }
    }

    private Long parseLong(String val) {
        if (val == null || val.trim().isEmpty()) return null;
        try {
            String s = val.trim().toLowerCase()
                    .replace(",", "")
                    .replace("followers", "")
                    .replace("subscriber", "")
                    .replace("subscribers", "")
                    .trim();
            double multiplier = 1;
            if (s.endsWith("k")) {
                multiplier = 1_000;
                s = s.substring(0, s.length() - 1).trim();
            } else if (s.endsWith("m")) {
                multiplier = 1_000_000;
                s = s.substring(0, s.length() - 1).trim();
            } else if (s.endsWith("b")) {
                multiplier = 1_000_000_000;
                s = s.substring(0, s.length() - 1).trim();
            }
            return (long) (Double.parseDouble(s) * multiplier);
        } catch (Exception e) {
            return null;
        }
    }

    private void addTag(List<Long> tagIds, String tagName, String category, String prefix) {
        if (tagName == null || tagName.trim().isEmpty()) {
            return;
        }
        String[] names = tagName.split("[,\uff0c]");
        for (String name : names) {
            name = name.trim();
            if (name.isEmpty())
                continue;
            Optional<SystemTag> tagOpt = this.jdbcTemplate.query(
                    "SELECT id FROM system_tag WHERE name = ? AND category = ?",
                    (rs, rowNum) -> {
                        SystemTag t = new SystemTag();
                        t.setId(rs.getLong("id"));
                        return t;
                    }, name, category).stream().findFirst();

            if (tagOpt.isPresent()) {
                tagIds.add(tagOpt.get().getId());
            } else {
                log.info("Creating new tag: {} for category: {}", name, category);
                this.jdbcTemplate.update(
                        "INSERT INTO system_tag (name, category, enabled, created_at) VALUES (?, ?, true, NOW())",
                        name, category);
                Long id = this.jdbcTemplate.queryForObject("SELECT LAST_INSERT_ID()", Long.class);
                if (id != null)
                    tagIds.add(id);
            }
        }
    }

    public int repairHistoryHandles() {
        log.info("Starting history handle repair task...");
        String sql = "SELECT influencer_id, platform, handle, url FROM influencer_social_media WHERE handle IS NOT NULL OR url IS NOT NULL";
        List<Map<String, Object>> socialItems = this.jdbcTemplate.queryForList(sql);
        int fixedCount = 0;
        for (Map<String, Object> item : socialItems) {
            Long influencerId = ((Number) item.get("influencer_id")).longValue();
            String platform = (String) item.get("platform");
            String handle = (String) item.get("handle");
            String url = (String) item.get("url");
            
            if (handle == null && url != null) {
                handle = this.extractHandle(url);
            }
            
            if (handle != null) {
                int updated = this.jdbcTemplate.update(
                    "UPDATE influencer SET default_handle = ?, default_platform = ?, default_url = ? WHERE id = ? AND (default_handle IS NULL OR default_handle = '')",
                    handle, platform, url, influencerId
                );
                fixedCount += updated;
            }
        }
        log.info("History handle repair finished. Updated {} records.", fixedCount);
        return fixedCount;
    }

    public InfluencerImportService(InfluencerRepository influencerRepository,
            InfluencerAddressRepository addressRepository, InfluencerPaymentInfoRepository paymentInfoRepository,
            JdbcTemplate jdbcTemplate, ObjectMapper objectMapper, ShopifyIntegrationClient shopifyIntegrationClient,
            InfluencerCooperationService cooperationService, ImportJobRepository importJobRepository,
            StorageServiceFactory storageServiceFactory, UserServiceClient userServiceClient,
            InfluencerLogService logService) {
        this.influencerRepository = influencerRepository;
        this.addressRepository = addressRepository;
        this.paymentInfoRepository = paymentInfoRepository;
        this.jdbcTemplate = jdbcTemplate;
        this.objectMapper = objectMapper;
        this.shopifyIntegrationClient = shopifyIntegrationClient;
        this.cooperationService = cooperationService;
        this.importJobRepository = importJobRepository;
        this.storageServiceFactory = storageServiceFactory;
        this.userServiceClient = userServiceClient;
        this.logService = logService;
    }

    public static class ImportResult {
        public int successCount = 0;
        public int failCount = 0;
        public int addedCount = 0;
        public int updatedCount = 0;
        public List<String> errorMessages = new ArrayList<>();
        public List<String> warnings = new ArrayList<>();

        public int getAddedCount() {
            return this.addedCount;
        }

        public void setAddedCount(int addedCount) {
            this.addedCount = addedCount;
        }

        public List<String> getErrorMessages() {
            return this.errorMessages;
        }

        public List<String> getWarnings() {
            return this.warnings;
        }
    }
}
