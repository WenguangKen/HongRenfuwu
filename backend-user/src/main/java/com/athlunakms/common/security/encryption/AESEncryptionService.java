package com.athlunakms.common.security.encryption;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;
import javax.crypto.AEADBadTagException;
import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class AESEncryptionService {
    private static final Logger log = LoggerFactory.getLogger(AESEncryptionService.class);
    private static final String ALGORITHM = "AES/GCM/NoPadding";
    private static final int GCM_IV_LENGTH = 12;
    private static final int GCM_TAG_LENGTH = 16;
    @Value(value="${app.encryption.master-key}")
    private String masterKey;

    private SecretKeySpec getKeySpec(String keyStr) throws Exception {
        MessageDigest sha = MessageDigest.getInstance("SHA-256");
        byte[] key = sha.digest(keyStr.getBytes(StandardCharsets.UTF_8));
        return new SecretKeySpec(key, "AES");
    }

    public String encrypt(String data) {
        if (data == null || data.isEmpty()) {
            return data;
        }
        try {
            SecretKeySpec keySpec = this.getKeySpec(this.masterKey);
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            byte[] iv = new byte[12];
            new SecureRandom().nextBytes(iv);
            GCMParameterSpec parameterSpec = new GCMParameterSpec(128, iv);
            cipher.init(1, (Key)keySpec, parameterSpec);
            byte[] encrypted = cipher.doFinal(data.getBytes(StandardCharsets.UTF_8));
            byte[] combined = new byte[12 + encrypted.length];
            System.arraycopy(iv, 0, combined, 0, 12);
            System.arraycopy(encrypted, 0, combined, 12, encrypted.length);
            return Base64.getEncoder().encodeToString(combined);
        }
        catch (Exception e) {
            log.error("\u52a0\u5bc6\u5931\u8d25", (Throwable)e);
            return data;
        }
    }

    public String decrypt(String encryptedData) {
        try {
            return this.decryptWithKey(encryptedData, this.masterKey);
        }
        catch (Exception e) {
            return encryptedData;
        }
    }

    public String decryptWithKey(String encryptedData, String keyStr) {
        if (encryptedData == null || encryptedData.isEmpty()) {
            return encryptedData;
        }
        try {
            SecretKeySpec keySpec = this.getKeySpec(keyStr);
            byte[] combined = Base64.getDecoder().decode(encryptedData);
            if (combined.length < 28) {
                return encryptedData;
            }
            byte[] iv = new byte[12];
            System.arraycopy(combined, 0, iv, 0, 12);
            byte[] cipherText = new byte[combined.length - 12];
            System.arraycopy(combined, 12, cipherText, 0, cipherText.length);
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            GCMParameterSpec spec = new GCMParameterSpec(128, iv);
            cipher.init(2, (Key)keySpec, spec);
            byte[] decodedText = cipher.doFinal(cipherText);
            return new String(decodedText, StandardCharsets.UTF_8);
        }
        catch (AEADBadTagException e) {
            throw new RuntimeException("Tag mismatch");
        }
        catch (Exception e) {
            throw new RuntimeException("Decryption error", e);
        }
    }
}

