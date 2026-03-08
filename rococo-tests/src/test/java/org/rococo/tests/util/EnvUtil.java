package org.rococo.tests.util;

import java.util.Optional;

public class EnvUtil {

    public static String envVar(String title, String defaultValue) {
        return Optional.ofNullable(System.getenv(title)).orElse(defaultValue);
    }

    public static Integer envVar(String title, int defaultValue) {
        return Optional.ofNullable(
                        System.getenv(title)
                )
                .map(Integer::parseInt)
                .orElse(defaultValue);
    }

    public static Double envVar(String title, double defaultValue) {
        return Optional.ofNullable(
                        System.getenv(title)
                )
                .map(Double::parseDouble)
                .orElse(defaultValue);
    }

    public static Boolean envVar(String title, boolean defaultValue) {
        return Optional.ofNullable(
                        System.getenv(title)
                )
                .filter(v -> !v.trim().isEmpty())
                .map("true"::equals)
                .orElse(defaultValue);
    }

    public static Long envVar(String title, long defaultValue) {
        return Optional.ofNullable(
                        System.getenv(title)
                )
                .map(Long::parseLong)
                .orElse(defaultValue);
    }

}
