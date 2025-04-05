package org.rococo.tests.util;

import org.apache.commons.lang3.StringUtils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

@ParametersAreNonnullByDefault
public class OAuthUtil {

    @Nonnull
    public static String generateCodeVerifier() {
        byte[] randomBytes = new byte[32];
        new SecureRandom().nextBytes(randomBytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);
    }

    @Nonnull
    public static String generateCodeChallenge(String codeVerifier) {
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
            byte[] hashedBytes = messageDigest.digest(codeVerifier.getBytes(StandardCharsets.US_ASCII));
            return Base64.getUrlEncoder().withoutPadding().encodeToString(hashedBytes);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Unable to generate code_challenge", e);
        }
    }

    @Nullable
    public static String getUsernameFromJwt(String token) {
        try {
            var base64Payload = token.split("\\.")[1];
            var payload = new String(Base64.getUrlDecoder().decode(base64Payload));
            return StringUtils.substringBetween(payload, "sub\":\"", "\",");
        } catch (Exception e) {
            return null;
        }
    }

}
