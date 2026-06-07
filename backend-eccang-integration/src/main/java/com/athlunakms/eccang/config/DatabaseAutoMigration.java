package com.athlunakms.eccang.config;

import jakarta.annotation.PostConstruct;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class DatabaseAutoMigration {
    private static final Logger log = LoggerFactory.getLogger(DatabaseAutoMigration.class);

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @PostConstruct
    public void migrate() {
        log.info("Starting automatic database migration for eccang tables...");
        try {
            // Drop duplicate columns if they exist
            dropColumnIfExists("eccang_products", "site");
            dropColumnIfExists("eccang_products", "user_account");
            dropColumnIfExists("eccang_product_variants", "site");
            dropColumnIfExists("eccang_product_variants", "user_account");

            // Check & add columns to eccang_products
            addColumnIfNotExists("eccang_products", "asin", "varchar(20) DEFAULT NULL COMMENT 'ASIN' AFTER parent_asin");
            addColumnIfNotExists("eccang_products", "asin_url", "varchar(500) DEFAULT NULL COMMENT 'ASIN 地址' AFTER asin");
            addColumnIfNotExists("eccang_products", "asin_type", "tinyint DEFAULT NULL COMMENT '1:子ASIN 2:独立ASIN 3:父ASIN' AFTER asin_url");
            addColumnIfNotExists("eccang_products", "small_image_url", "text DEFAULT NULL COMMENT '缩略小图' AFTER main_image");
            addColumnIfNotExists("eccang_products", "open_date_local", "datetime DEFAULT NULL COMMENT '北京创建时间' AFTER small_image_url");

            // Migrate data for eccang_products
            jdbcTemplate.update("UPDATE `eccang_products` SET `asin` = `parent_asin` WHERE `asin` IS NULL AND `parent_asin` IS NOT NULL");
            jdbcTemplate.update("UPDATE `eccang_products` SET `asin_url` = `product_link` WHERE `asin_url` IS NULL AND `product_link` IS NOT NULL");
            jdbcTemplate.update("UPDATE `eccang_products` SET `asin_type` = 3 WHERE `asin_type` IS NULL AND `parent_asin` IS NOT NULL");

            // Check & add columns to eccang_product_variants
            addColumnIfNotExists("eccang_product_variants", "parent_asin", "varchar(20) DEFAULT NULL COMMENT '父 ASIN' AFTER platform_account");
            addColumnIfNotExists("eccang_product_variants", "asin_url", "varchar(500) DEFAULT NULL COMMENT 'ASIN 地址' AFTER asin");
            addColumnIfNotExists("eccang_product_variants", "asin_type", "tinyint DEFAULT NULL COMMENT '1:子ASIN 2:独立ASIN 3:父ASIN' AFTER asin_url");
            addColumnIfNotExists("eccang_product_variants", "small_image_url", "text DEFAULT NULL COMMENT '缩略小图' AFTER image_url");
            addColumnIfNotExists("eccang_product_variants", "open_date_local", "datetime DEFAULT NULL COMMENT '北京创建时间' AFTER small_image_url");

            // Migrate data for eccang_product_variants
            jdbcTemplate.update("UPDATE `eccang_product_variants` SET `asin_url` = `product_link` WHERE `asin_url` IS NULL AND `product_link` IS NOT NULL");
            
            // Sync parentAsin, platform_account between tables
            jdbcTemplate.update(
                "UPDATE `eccang_product_variants` v " +
                "JOIN `eccang_products` p ON p.id = v.product_id " +
                "SET v.parent_asin = p.parent_asin, v.platform_account = COALESCE(v.platform_account, p.platform_account) " +
                "WHERE v.parent_asin IS NULL OR v.platform_account IS NULL"
            );

            // Recovery: Revert mistakenly set 'suspected_deleted' products back to 'active'
            int recoveredProducts = jdbcTemplate.update("UPDATE `eccang_products` SET `status` = 'active' WHERE `status` = 'suspected_deleted'");
            int recoveredVariants = jdbcTemplate.update("UPDATE `eccang_product_variants` SET `status` = 'active' WHERE `status` = 'suspected_deleted'");
            if (recoveredProducts > 0 || recoveredVariants > 0) {
                log.info("Recovered {} products and {} variants from 'suspected_deleted' to 'active'.", recoveredProducts, recoveredVariants);
            }

            log.info("Automatic database migration for eccang tables completed successfully.");

            // Debug print users and roles to a file
            try {
                StringBuilder sb = new StringBuilder();
                List<java.util.Map<String, Object>> users = jdbcTemplate.queryForList("SELECT id, username, email_encrypted FROM users");
                sb.append("=== USERS ===\n");
                for (java.util.Map<String, Object> u : users) {
                    sb.append(String.format("id=%s, username=%s, email=%s\n", u.get("id"), u.get("username"), u.get("email_encrypted")));
                }
                List<java.util.Map<String, Object>> roles = jdbcTemplate.queryForList("SELECT id, name, description FROM roles");
                sb.append("\n=== ROLES ===\n");
                for (java.util.Map<String, Object> r : roles) {
                    sb.append(String.format("id=%s, name=%s, description=%s\n", r.get("id"), r.get("name"), r.get("description")));
                }
                List<java.util.Map<String, Object>> userRoles = jdbcTemplate.queryForList("SELECT user_id, role_id FROM user_roles");
                sb.append("\n=== USER_ROLES ===\n");
                for (java.util.Map<String, Object> ur : userRoles) {
                    sb.append(String.format("user_id=%s, role_id=%s\n", ur.get("user_id"), ur.get("role_id")));
                }
                java.nio.file.Files.write(java.nio.file.Paths.get("c:\\Users\\Administrator\\Desktop\\pengnihongrenfuwu\\debug_users.txt"), sb.toString().getBytes(java.nio.charset.StandardCharsets.UTF_8));
            } catch (Exception ex) {
                log.error("Failed to log debug user info", ex);
            }

            // Test decryption of all users with both keys
            try {
                StringBuilder dsb = new StringBuilder();
                String devKey = "Influencer-AES-Master-Key-32-Dev";
                String prodKey = "InfluencerMarketingSecretKey2025Ver256Bit";
                
                com.athlunakms.common.security.encryption.AESEncryptionService aes = new com.athlunakms.common.security.encryption.AESEncryptionService();
                List<java.util.Map<String, Object>> users = jdbcTemplate.queryForList("SELECT id, username, email_encrypted FROM users");
                
                for (java.util.Map<String, Object> u : users) {
                    Long id = (Long) u.get("id");
                    String username = (String) u.get("username");
                    String emailEnc = (String) u.get("email_encrypted");
                    dsb.append("User ID: ").append(id).append(" (").append(username).append(")\n");
                    dsb.append("  Raw Encrypted: ").append(emailEnc).append("\n");
                    
                    try {
                        String decDev = aes.decryptWithKey(emailEnc, devKey);
                        if (decDev != null && !decDev.equals(emailEnc)) {
                            dsb.append("  DEV KEY SUCCESS: ").append(decDev).append("\n");
                        } else {
                            dsb.append("  DEV KEY RETURNED SAME VALUE\n");
                        }
                    } catch (Exception ex) {
                        dsb.append("  DEV KEY ERROR: ").append(ex.getMessage()).append("\n");
                    }
                    
                    try {
                        String decProd = aes.decryptWithKey(emailEnc, prodKey);
                        if (decProd != null && !decProd.equals(emailEnc)) {
                            dsb.append("  PROD KEY SUCCESS: ").append(decProd).append("\n");
                        } else {
                            dsb.append("  PROD KEY RETURNED SAME VALUE\n");
                        }
                    } catch (Exception ex) {
                        dsb.append("  PROD KEY ERROR: ").append(ex.getMessage()).append("\n");
                    }
                    dsb.append("\n");
                }
                java.nio.file.Files.write(java.nio.file.Paths.get("c:\\Users\\Administrator\\Desktop\\pengnihongrenfuwu\\debug_decrypt.txt"), dsb.toString().getBytes(java.nio.charset.StandardCharsets.UTF_8));
            } catch (Exception ex) {
                log.error("Test decryption error", ex);
            }
        } catch (Exception e) {
            log.error("Failed to perform automatic database migration", e);
        }
    }

    private void addColumnIfNotExists(String tableName, String columnName, String columnDefinition) {
        String checkSql = "SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = ? AND COLUMN_NAME = ?";
        Integer count = jdbcTemplate.queryForObject(checkSql, Integer.class, tableName, columnName);
        if (count == null || count == 0) {
            log.info("Adding column '{}' to table '{}'...", columnName, tableName);
            String alterSql = String.format("ALTER TABLE `%s` ADD COLUMN `%s` %s", tableName, columnName, columnDefinition);
            jdbcTemplate.execute(alterSql);
        }
    }

    private void dropColumnIfExists(String tableName, String columnName) {
        String checkSql = "SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = ? AND COLUMN_NAME = ?";
        Integer count = jdbcTemplate.queryForObject(checkSql, Integer.class, tableName, columnName);
        if (count != null && count > 0) {
            log.info("Dropping duplicate column '{}' from table '{}'...", columnName, tableName);
            String alterSql = String.format("ALTER TABLE `%s` DROP COLUMN `%s`", tableName, columnName);
            jdbcTemplate.execute(alterSql);
        }
    }
}
