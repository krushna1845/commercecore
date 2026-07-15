package com.krushna.commercecore.service;

import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.lang.reflect.Array;
import java.nio.ByteBuffer;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;

@Service
public class MfaService {

    private static final String ALLOWED_CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ234567"; // Base32 alphabet
    private static final SecureRandom random = new SecureRandom();

    /**
     * Generates a 16-character Base32 secret key.
     */
    public String generateSecretKey() {
        StringBuilder sb = new StringBuilder(16);
        for (int i = 0; i < 16; i++) {
            sb.append(ALLOWED_CHARS.charAt(random.nextInt(ALLOWED_CHARS.length())));
        }
        return sb.toString();
    }

    /**
     * Generates the otpauth URI for QR codes.
     */
    public String getQrCodeUrl(String username, String secret) {
        return "otpauth://totp/CommerceCore:" + username + "?secret=" + secret + "&issuer=CommerceCore";
    }

    /**
     * Verifies the given 6-digit TOTP code.
     */
    public boolean verifyCode(String secret, String code) {
        if (secret == null || code == null) {
            return false;
        }
        try {
            int codeValue = Integer.parseInt(code);
            byte[] key = decodeBase32(secret);
            long currentTimeIndex = System.currentTimeMillis() / 1000 / 30;

            // Allow clock-skew window of -1, 0, +1 time steps
            for (int i = -1; i <= 1; i++) {
                if (verifyTimeStep(key, currentTimeIndex + i, codeValue)) {
                    return true;
                }
            }
        } catch (NumberFormatException e) {
            return false;
        }
        return false;
    }

    private boolean verifyTimeStep(byte[] key, long timeIndex, int expectedCode) {
        byte[] data = ByteBuffer.allocate(8).putLong(timeIndex).array();
        try {
            Mac mac = Mac.getInstance("HmacSHA1");
            SecretKeySpec signKey = new SecretKeySpec(key, "HmacSHA1");
            mac.init(signKey);
            byte[] hash = mac.doFinal(data);

            int offset = hash[hash.length - 1] & 0xf;
            long truncatedHash = 0;
            for (int i = 0; i < 4; ++i) {
                truncatedHash <<= 8;
                truncatedHash |= (hash[offset + i] & 0xff);
            }

            truncatedHash &= 0x7fffffff;
            truncatedHash %= 1000000;

            return truncatedHash == expectedCode;
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            return false;
        }
    }

    private byte[] decodeBase32(String base32) {
        String base32Upper = base32.toUpperCase();
        int size = (base32Upper.length() * 5) / 8;
        byte[] bytes = new byte[size];
        int buffer = 0;
        int bitsLeft = 0;
        int count = 0;

        for (int i = 0; i < base32Upper.length(); i++) {
            char c = base32Upper.charAt(i);
            int val = ALLOWED_CHARS.indexOf(c);
            if (val == -1) {
                continue; // ignore padding or invalid chars
            }
            buffer = (buffer << 5) | val;
            bitsLeft += 5;
            if (bitsLeft >= 8) {
                bytes[count++] = (byte) ((buffer >> (bitsLeft - 8)) & 0xff);
                bitsLeft -= 8;
            }
        }
        return Arrays.copyOf(bytes, count);
    }
}
