package com.amr.chatservice.service;

import org.apache.commons.codec.binary.Hex;
import org.json.JSONObject;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class OgpCrypto {

    static public String getSignature(String msg, String secret) throws UnsupportedEncodingException {

        byte[] message = msg.getBytes(StandardCharsets.UTF_8);
        byte[] secretKey = secret.getBytes(StandardCharsets.UTF_8);
        byte[] hmacSha256 = null;
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKeySpec = new SecretKeySpec(secretKey, "HmacSHA256");
            mac.init(secretKeySpec);
            hmacSha256 = mac.doFinal(message);
        } catch (Exception e) {
            throw new RuntimeException("Failed to calculate hmac-sha256", e);
        }
        String base64HmacSha256 = Hex.encodeHexString(hmacSha256);

        return base64HmacSha256;
    }

    static public byte[] calcHmacSha256(byte[] message, byte[] secretKey) {
        byte[] hmacSha256 = null;
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKeySpec = new SecretKeySpec(secretKey, "HmacSHA256");
            mac.init(secretKeySpec);
            hmacSha256 = mac.doFinal(message);
        } catch (Exception e) {
            throw new RuntimeException("Failed to calculate hmac-sha256", e);
        }
        return hmacSha256;
    }


    public static String createJWT(String payload, String secretKey) {
        String header = new JSONObject().put("alg", "HS256").put("typ", "JWT").toString();
        Base64.Encoder encoder = Base64.getUrlEncoder();
        // Encoding URL

        String eStr = encoder.encodeToString(header.getBytes()).replace("+", "-").replace("/", "_").replace("=", "");
        String eStr2 = encoder.encodeToString(payload.getBytes()).replace("+", "-").replace("/", "_").replace("=", "");

        String headerPayload = eStr + "." + eStr2;
        String jwt = "";
        byte[] hmacSha256 = calcHmacSha256(headerPayload.getBytes(StandardCharsets.UTF_8), secretKey.getBytes(StandardCharsets.UTF_8));
        String base64HmacSha256 = Base64.getEncoder().encodeToString(hmacSha256);
        jwt = headerPayload + "." + base64HmacSha256.replace("+", "-").replace("/", "_").replace("=", "");
        return jwt;
    }

}
