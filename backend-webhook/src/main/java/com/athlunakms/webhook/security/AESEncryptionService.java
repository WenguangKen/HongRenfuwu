package com.athlunakms.webhook.security;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Base64;
import javax.crypto.AEADBadTagException;
import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class AESEncryptionService {
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
            cipher.init(2, keySpec, spec);
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

