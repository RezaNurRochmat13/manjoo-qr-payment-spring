package com.manjoo.qr.payment.utils.signature;

import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

@Component
public class SignatureUtil {
    private final String SECRET_KEY = "my_secret_key";

    public boolean isValid(String payload, String expectedSignature) {
        try {
            Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
            SecretKeySpec secret_key = new SecretKeySpec(SECRET_KEY.getBytes(), "HmacSHA256");
            sha256_HMAC.init(secret_key);

            byte[] hash = sha256_HMAC.doFinal(payload.getBytes());
            String actualSignature = Base64.getEncoder().encodeToString(hash);

            return actualSignature.equals(expectedSignature);
        } catch (Exception e) {
            return false;
        }
    }
}
