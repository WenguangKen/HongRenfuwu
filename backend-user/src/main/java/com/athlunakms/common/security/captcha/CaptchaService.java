package com.athlunakms.common.security.captcha;

import com.athlunakms.common.exception.BusinessException;
import com.athlunakms.common.exception.ErrorCode;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.UUID;
import javax.imageio.ImageIO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service(value="captchaGenerator")
public class CaptchaService {
    private static final Logger log = LoggerFactory.getLogger(CaptchaService.class);
    @Value(value="${app.captcha.length:6}")
    private int captchaLength;
    @Value(value="${app.captcha.allowed-chars:ABCDEFGHJKLMNPQRSTUVWXYZabcdefghjkmnpqrstuvwxyz23456789}")
    private String allowedChars;
    @Value(value="${app.captcha.expire-minutes:5}")
    private int expireMinutes;

    public String generateCaptchaCode() {
        SecureRandom random = new SecureRandom();
        StringBuilder captcha = new StringBuilder();
        for (int i = 0; i < this.captchaLength; ++i) {
            int index = random.nextInt(this.allowedChars.length());
            captcha.append(this.allowedChars.charAt(index));
        }
        return captcha.toString();
    }

    public String generateCaptchaKey() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    public String hashCaptchaCode(String captchaCode) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(captchaCode.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xFF & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        }
        catch (Exception e) {
            log.error("\u8ba1\u7b97\u9a8c\u8bc1\u7801\u54c8\u5e0c\u5931\u8d25", (Throwable)e);
            throw new BusinessException(ErrorCode.INTERNAL_ERROR, "\u8ba1\u7b97\u9a8c\u8bc1\u7801\u54c8\u5e0c\u5931\u8d25");
        }
    }

    public LocalDateTime getExpireTime() {
        return LocalDateTime.now().plusMinutes(this.expireMinutes);
    }

    public boolean validateCaptchaFormat(String captchaCode) {
        if (captchaCode == null || captchaCode.length() != this.captchaLength) {
            return false;
        }
        for (char c : captchaCode.toCharArray()) {
            if (this.allowedChars.indexOf(c) != -1) continue;
            return false;
        }
        return true;
    }

    public String generateCaptchaImageBase64(String captchaCode) {
        try {
            int i;
            int i2;
            int width = 160;
            int height = 56;
            BufferedImage image = new BufferedImage(width, height, 1);
            Graphics2D g2 = image.createGraphics();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            g2.setColor(new Color(248, 250, 252));
            g2.fillRect(0, 0, width, height);
            SecureRandom random = new SecureRandom();
            for (i2 = 0; i2 < 10; ++i2) {
                g2.setColor(new Color(220 + random.nextInt(30), 220 + random.nextInt(30), 220 + random.nextInt(30)));
                int r = 10 + random.nextInt(20);
                g2.drawOval(random.nextInt(width), random.nextInt(height), r, r);
            }
            for (i2 = 0; i2 < 6; ++i2) {
                g2.setColor(new Color(180 + random.nextInt(50), 180 + random.nextInt(50), 180 + random.nextInt(50)));
                int x1 = random.nextInt(width);
                int y1 = random.nextInt(height);
                int x2 = random.nextInt(width);
                int y2 = random.nextInt(height);
                g2.drawLine(x1, y1, x2, y2);
            }
            String[] fonts = new String[]{"Consolas", "Arial", "Verdana"};
            int charWidth = width / (captchaCode.length() + 1);
            int baseBaselineY = height / 2 + 10;
            for (i = 0; i < captchaCode.length(); ++i) {
                char ch = captchaCode.charAt(i);
                g2.setFont(new Font(fonts[random.nextInt(fonts.length)], 1, 32 + random.nextInt(8)));
                g2.setColor(new Color(30 + random.nextInt(80), 30 + random.nextInt(80), 30 + random.nextInt(80)));
                AffineTransform origExpr = g2.getTransform();
                double angle = (random.nextDouble() - 0.5) * 0.6;
                int x = 10 + i * charWidth + random.nextInt(10);
                int y = baseBaselineY + (random.nextInt(10) - 5);
                g2.translate(x, y);
                g2.rotate(angle);
                g2.drawString(String.valueOf(ch), 0, 0);
                g2.setTransform(origExpr);
            }
            for (i = 0; i < 3; ++i) {
                g2.setColor(new Color(100 + random.nextInt(100), 100 + random.nextInt(100), 100 + random.nextInt(100)));
                g2.drawLine(random.nextInt(width), random.nextInt(height), random.nextInt(width), random.nextInt(height));
            }
            for (i = 0; i < 100; ++i) {
                g2.setColor(new Color(150 + random.nextInt(100), 150 + random.nextInt(100), 150 + random.nextInt(100)));
                int x = random.nextInt(width);
                int y = random.nextInt(height);
                g2.fillRect(x, y, 1, 1);
            }
            g2.dispose();
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ImageIO.write((RenderedImage)image, "png", bos);
            String base64 = Base64.getEncoder().encodeToString(bos.toByteArray());
            return "data:image/png;base64," + base64;
        }
        catch (Exception e) {
            log.error("\u751f\u6210\u9a8c\u8bc1\u7801\u56fe\u7247\u5931\u8d25", (Throwable)e);
            throw new BusinessException(ErrorCode.INTERNAL_ERROR, "\u751f\u6210\u9a8c\u8bc1\u7801\u56fe\u7247\u5931\u8d25");
        }
    }
}

