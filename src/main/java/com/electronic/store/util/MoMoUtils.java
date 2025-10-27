package com.electronic.store.util;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

public class MoMoUtils {

    private static final String HMAC_SHA256 = "HmacSHA256";

    /**
     * Tạo HMAC SHA256 signature cho MoMo request
     */
    public static String createSignature(String data, String secretKey) {
        try {
            Mac mac = Mac.getInstance(HMAC_SHA256);
            SecretKeySpec secretKeySpec = new SecretKeySpec(secretKey.getBytes(StandardCharsets.UTF_8), HMAC_SHA256);
            mac.init(secretKeySpec);

            byte[] hash = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
            return bytesToHex(hash);
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            throw new RuntimeException("Error creating MoMo signature", e);
        }
    }

    /**
     * Convert byte array to hex string
     */
    private static String bytesToHex(byte[] bytes) {
        StringBuilder result = new StringBuilder();
        for (byte b : bytes) {
            result.append(String.format("%02x", b));
        }
        return result.toString();
    }

    /**
     * Tạo raw signature string cho MoMo payment request
     */
    public static String createRawSignature(String accessKey, String amount, String extraData,
                                           String ipnUrl, String orderId, String orderInfo,
                                           String partnerCode, String redirectUrl,
                                           String requestId, String requestType) {
        return String.format("accessKey=%s&amount=%s&extraData=%s&ipnUrl=%s&orderId=%s&orderInfo=%s&partnerCode=%s&redirectUrl=%s&requestId=%s&requestType=%s",
                accessKey, amount, extraData, ipnUrl, orderId, orderInfo, partnerCode, redirectUrl, requestId, requestType);
    }

    /**
     * Tạo raw signature string cho MoMo IPN verification
     */
    public static String createIpnRawSignature(String accessKey, String amount, String extraData,
                                              String message, String orderId, String orderInfo,
                                              String orderType, String partnerCode, String payType,
                                              String requestId, String responseTime, String resultCode,
                                              String transId) {
        return String.format("accessKey=%s&amount=%s&extraData=%s&message=%s&orderId=%s&orderInfo=%s&orderType=%s&partnerCode=%s&payType=%s&requestId=%s&responseTime=%s&resultCode=%s&transId=%s",
                accessKey, amount, extraData, message, orderId, orderInfo, orderType, partnerCode, payType, requestId, responseTime, resultCode, transId);
    }

    /**
     * Validate MoMo IPN signature
     */
    public static boolean validateIpnSignature(String signature, String rawSignature, String secretKey) {
        String expectedSignature = createSignature(rawSignature, secretKey);
        return signature.equals(expectedSignature);
    }

    /**
     * Generate unique order ID
     */
    public static String generateOrderId() {
        return "ORDER_" + System.currentTimeMillis();
    }

    /**
     * Generate unique request ID
     */
    public static String generateRequestId() {
        return "REQ_" + System.currentTimeMillis();
    }
}