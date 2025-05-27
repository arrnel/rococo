package org.rococo.tests.config;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import static org.rococo.tests.config.PropertyUtil.getEnvVar;

@ParametersAreNonnullByDefault
enum LocalConfig implements Config {

    INSTANCE;

    @Nonnull
    @Override
    public String artistsJdbcUrl() {
        return "jdbc:postgresql://127.0.0.1:%d/rococo-artists".formatted(dbPort());
    }

    @Nonnull
    @Override
    public String artistsGrpcHost() {
        return "127.0.0.1";
    }

    @Override
    public int artistsPort() {
        return getEnvVar("ROCOCO_ARTISTS_PORT", 9002);
    }

    @Nonnull
    @Override
    public String authJdbcUrl() {
        return "jdbc:postgresql://127.0.0.1:%d/rococo-auth".formatted(dbPort());
    }

    @Nonnull
    @Override
    public String authUrl() {
        return "http://127.0.0.1:%d".formatted(getEnvVar("ROCOCO_AUTH_PORT", 9000));
    }

    @Nonnull
    @Override
    public String countriesJdbcUrl() {
        return "jdbc:postgresql://127.0.0.1:%d/rococo-countries".formatted(dbPort());
    }

    @Nonnull
    @Override
    public String countriesGrpcHost() {
        return "127.0.0.1";
    }

    @Override
    public int countriesPort() {
        return getEnvVar("ROCOCO_COUNTRIES_PORT", 9003);
    }

    @Nonnull
    @Override
    public String filesJdbcUrl() {
        return "jdbc:postgresql://127.0.0.1:%d/rococo-files".formatted(dbPort());
    }

    @Nonnull
    @Override
    public String filesGrpcHost() {
        return "127.0.0.1";
    }

    @Override
    public int filesPort() {
        return getEnvVar("ROCOCO_FILES_PORT", 9004);
    }

    @Nonnull
    @Override
    public String frontUrl() {
        return "http://127.0.0.1:%d".formatted(getEnvVar("ROCOCO_FRONT_PORT", 3000));
    }

    @Nonnull
    @Override
    public String gatewayUrl() {
        return "http://127.0.0.1:%d".formatted(getEnvVar("ROCOCO_GATEWAY_PORT", 9001));
    }

    @Nonnull
    @Override
    public String logsUrl() {
        return "http://127.0.0.1:%d".formatted(getEnvVar("ROCOCO_LOGS_PORT", 9008));
    }

    @Nonnull
    @Override
    public String museumsJdbcUrl() {
        return "jdbc:postgresql://127.0.0.1:%d/rococo-museums".formatted(dbPort());
    }

    @Nonnull
    @Override
    public String museumsGrpcHost() {
        return "127.0.0.1";
    }

    @Override
    public int museumsPort() {
        return getEnvVar("ROCOCO_MUSEUMS_PORT", 9005);
    }

    @Nonnull
    @Override
    public String paintingsJdbcUrl() {
        return "jdbc:postgresql://127.0.0.1:%d/rococo-paintings".formatted(dbPort());
    }

    @Nonnull
    @Override
    public String paintingsGrpcHost() {
        return "127.0.0.1";
    }

    @Override
    public int paintingsPort() {
        return getEnvVar("ROCOCO_PAINTINGS_PORT", 9006);
    }

    @Nonnull
    @Override
    public String usersJdbcUrl() {
        return "jdbc:postgresql://127.0.0.1:%d/rococo-users".formatted(dbPort());
    }

    @Nonnull
    @Override
    public String usersGrpcHost() {
        return "127.0.0.1";
    }

    @Nonnull
    @Override
    public String kafkaAddress() {
        return "127.0.0.1:" + getEnvVar("ROCOCO_KAFKA_PORT", 9092);
    }

    @Override
    public int usersPort() {
        return getEnvVar("ROCOCO_USERS_PORT", 9007);
    }

    public int dbPort() {
        return getEnvVar("ROCOCO_DB_PORT", 5432);
    }

    public String dbUser() {
        return getEnvVar("ROCOCO_DB_USER", "postgres");
    }

    public String dbPassword() {
        return getEnvVar("ROCOCO_DB_PASSWORD", "secret");
    }

    @Nonnull
    @Override
    public String allureDockerUrl() {
        return "http://127.0.0.1:5050";
    }

    @Nonnull
    @Override
    public String screenshotBaseDir() {
        return "rococo-tests/.screen-output/screenshots/local/";
    }

    @Nonnull
    public String testUserName() {
        return getEnvVar("ROCOCO_TEST_USERNAME", "test_user");
    }

    @Nonnull
    public String testUserPassword() {
        return getEnvVar("ROCOCO_TEST_USER_PASSWORD", "12345");
    }

}
