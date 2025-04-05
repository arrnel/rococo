package org.rococo.tests.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class HashUtil {

    private HashUtil() {
    }

    public static String getHash(String image) {
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
            messageDigest.update(image.getBytes(StandardCharsets.UTF_8));
            return encodeText(messageDigest.digest());
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    private static String encodeText(byte[] data) {
        final var base64 = Base64.getEncoder()
                .encodeToString(data);
        return base64.substring(0, base64.length() - 1);
    }

}
