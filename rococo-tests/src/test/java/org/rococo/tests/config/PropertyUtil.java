package org.rococo.tests.config;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
class PropertyUtil {

    static int getEnvVar(String key, int defaultValue) {
        String value = System.getenv(key);
        return value == null
                ? defaultValue
                : Integer.parseInt(value);
    }

    @Nonnull
    static String getEnvVar(String key, String defaultValue) {
        String value = System.getenv(key);
        return value == null
                ? defaultValue
                : value;
    }

}
