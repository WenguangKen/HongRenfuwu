package com.athlunakms.common.util;

import com.athlunakms.common.security.encryption.AESEncryptionService;
import com.athlunakms.user.entity.User;
import com.athlunakms.user.repository.UserRepository;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.transaction.annotation.Transactional;

public class DataReEncryptionTool
implements CommandLineRunner {
    private static final Logger log = LoggerFactory.getLogger(DataReEncryptionTool.class);
    private final UserRepository userRepository;
    private final AESEncryptionService encryptionService;

    @Transactional
    public void run(String ... args) {
        log.info("========================================");
        log.info("\u5f00\u59cb\u91cd\u65b0\u52a0\u5bc6\u7528\u6237\u6570\u636e...");
        log.info("========================================");
        try {
            List<User> users = this.userRepository.findAll();
            int successCount = 0;
            int failCount = 0;
            for (User user : users) {
                try {
                    String encrypted;
                    String reEncrypted;
                    String decrypted;
                    boolean updated = false;
                    if (user.getEmailEncrypted() != null && !user.getEmailEncrypted().isEmpty()) {
                        decrypted = this.tryDecryptWithMultipleKeys(user.getEmailEncrypted());
                        if (decrypted.contains("@") && !decrypted.equals(user.getEmailEncrypted())) {
                            reEncrypted = this.encryptionService.encrypt(decrypted);
                            user.setEmailEncrypted(reEncrypted);
                            updated = true;
                            log.info("\u7528\u6237 {} \u7684\u90ae\u7bb1\u5df2\u91cd\u65b0\u52a0\u5bc6", user.getUsername());
                        } else if (decrypted.contains("@")) {
                            encrypted = this.encryptionService.encrypt(decrypted);
                            user.setEmailEncrypted(encrypted);
                            updated = true;
                            log.info("\u7528\u6237 {} \u7684\u90ae\u7bb1\u5df2\u52a0\u5bc6\uff08\u539f\u4e3a\u660e\u6587\uff09", user.getUsername());
                        } else {
                            log.warn("\u7528\u6237 {} \u7684\u90ae\u7bb1\u65e0\u6cd5\u89e3\u5bc6\uff0c\u8df3\u8fc7", user.getUsername());
                        }
                    }
                    if (user.getPhoneEncrypted() != null && !user.getPhoneEncrypted().isEmpty()) {
                        decrypted = this.tryDecryptWithMultipleKeys(user.getPhoneEncrypted());
                        if (decrypted.matches("\\d+") && !decrypted.equals(user.getPhoneEncrypted())) {
                            reEncrypted = this.encryptionService.encrypt(decrypted);
                            user.setPhoneEncrypted(reEncrypted);
                            updated = true;
                            log.info("\u7528\u6237 {} \u7684\u624b\u673a\u53f7\u5df2\u91cd\u65b0\u52a0\u5bc6", user.getUsername());
                        } else if (decrypted.matches("\\d+")) {
                            encrypted = this.encryptionService.encrypt(decrypted);
                            user.setPhoneEncrypted(encrypted);
                            updated = true;
                            log.info("\u7528\u6237 {} \u7684\u624b\u673a\u53f7\u5df2\u52a0\u5bc6\uff08\u539f\u4e3a\u660e\u6587\uff09", user.getUsername());
                        } else {
                            log.warn("\u7528\u6237 {} \u7684\u624b\u673a\u53f7\u65e0\u6cd5\u89e3\u5bc6\uff0c\u8df3\u8fc7", user.getUsername());
                        }
                    }
                    if (!updated) continue;
                    this.userRepository.save(user);
                    ++successCount;
                }
                catch (Exception e) {
                    log.error("\u5904\u7406\u7528\u6237 {} \u65f6\u51fa\u9519: {}", user.getUsername(), e.getMessage());
                    ++failCount;
                }
            }
            log.info("========================================");
            log.info("\u91cd\u65b0\u52a0\u5bc6\u5b8c\u6210\uff01");
            log.info("\u6210\u529f: {} \u4e2a\u7528\u6237", successCount);
            log.info("\u5931\u8d25: {} \u4e2a\u7528\u6237", failCount);
            log.info("\u603b\u8ba1: {} \u4e2a\u7528\u6237", users.size());
            log.info("========================================");
        }
        catch (Exception e) {
            log.error("\u91cd\u65b0\u52a0\u5bc6\u8fc7\u7a0b\u4e2d\u53d1\u751f\u9519\u8bef", e);
        }
    }

    private String tryDecryptWithMultipleKeys(String encryptedData) {
        String[] candidateKeys = new String[]{"AthlunaKMSSecretKey2025Ver256Bit", "dev-32-byte-encryption-key-change-in-production"};
        try {
            return this.encryptionService.decrypt(encryptedData);
        }
        catch (Exception exception) {
            for (String key : candidateKeys) {
                try {
                    return this.encryptionService.decryptWithKey(encryptedData, key);
                }
                catch (Exception exception2) {
                }
            }
            return encryptedData;
        }
    }

    public DataReEncryptionTool(UserRepository userRepository, AESEncryptionService encryptionService) {
        this.userRepository = userRepository;
        this.encryptionService = encryptionService;
    }
}

