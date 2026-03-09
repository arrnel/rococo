package org.rococo.tests.util;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Optional;

@ParametersAreNonnullByDefault
public class EnvUtil {

    @Nonnull
    public static String envVar(String title, String defaultValue) {
        return Optional.ofNullable(System.getenv(title)).orElse(defaultValue);
    }

    @Nonnull
    public static Integer envVar(String title, int defaultValue) {
        return Optional.ofNullable(
                        System.getenv(title)
                )
                .map(Integer::parseInt)
                .orElse(defaultValue);
    }

    @Nonnull
    public static Boolean envVar(String title, boolean defaultValue) {
        return Optional.ofNullable(
                        System.getenv(title)
                )
                .filter(v -> !v.trim().isEmpty())
                .map("true"::equalsIgnoreCase)
                .orElse(defaultValue);
    }

}
