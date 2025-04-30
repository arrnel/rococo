package org.rococo.tests.config;

import javax.annotation.ParametersAreNonnullByDefault;

import static org.rococo.tests.config.PropertyUtil.getEnvVar;

@ParametersAreNonnullByDefault
enum LocalConfig implements Config {

    INSTANCE;

    @Override
    public String artistsJdbcUrl() {
        return "jdbc:postgresql://127.0.0.1:%d/rococo-artists".formatted(dbPort());
    }

    @Override
    public String artistsGrpcHost() {
        return "127.0.0.1";
    }

    @Override
    public int artistsPort() {
        return getEnvVar("ROCOCO_ARTISTS_PORT", 9002);
    }

    @Override
    public String authJdbcUrl() {
        return "jdbc:postgresql://127.0.0.1:%d/rococo-auth".formatted(dbPort());
    }

    @Override
    public String authUrl() {
        return "http://127.0.0.1:%d".formatted(getEnvVar("ROCOCO_AUTH_PORT", 9000));
    }

    @Override
    public String countriesJdbcUrl() {
        return "jdbc:postgresql://127.0.0.1:%d/rococo-countries".formatted(dbPort());
    }

    @Override
    public String countriesGrpcHost() {
        return "127.0.0.1";
    }

    @Override
    public int countriesPort() {
        return getEnvVar("ROCOCO_COUNTRIES_PORT", 9003);
    }

    @Override
    public String filesJdbcUrl() {
        return "jdbc:postgresql://127.0.0.1:%d/rococo-files".formatted(dbPort());
    }

    @Override
    public String filesGrpcHost() {
        return "127.0.0.1";
    }

    @Override
    public int filesPort() {
        return getEnvVar("ROCOCO_FILES_PORT", 9004);
    }

    @Override
    public String frontUrl() {
        return "http://127.0.0.1:%d".formatted(getEnvVar("ROCOCO_FRONT_PORT", 3000));
    }

    @Override
    public String gatewayUrl() {
        return "http://127.0.0.1:%d".formatted(getEnvVar("ROCOCO_GATEWAY_PORT", 9001));
    }

    @Override
    public String museumsJdbcUrl() {
        return "jdbc:postgresql://127.0.0.1:%d/rococo-museums".formatted(dbPort());
    }

    @Override
    public String museumsGrpcHost() {
        return "127.0.0.1";
    }

    @Override
    public int museumsPort() {
        return getEnvVar("ROCOCO_MUSEUMS_PORT", 9005);
    }

    @Override
    public String paintingsJdbcUrl() {
        return "jdbc:postgresql://127.0.0.1:%d/rococo-paintings".formatted(dbPort());
    }

    @Override
    public String paintingsGrpcHost() {
        return "127.0.0.1";
    }

    @Override
    public int paintingsPort() {
        return getEnvVar("ROCOCO_PAINTINGS_PORT", 9006);
    }

    @Override
    public String usersJdbcUrl() {
        return "jdbc:postgresql://127.0.0.1:%d/rococo-users".formatted(dbPort());
    }

    @Override
    public String usersGrpcHost() {
        return "127.0.0.1";
    }

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

    @Override
    public String allureDockerUrl() {
        return "http://127.0.0.1:5050/";
    }

    @Override
    public String screenshotBaseDir() {
        return "rococo-tests/.screen-output/screenshots/local/";
    }

    public String testUserName() {
        return getEnvVar("ROCOCO_TEST_USERNAME", "test_user");
    }

    public String testUserPassword() {
        return getEnvVar("ROCOCO_TEST_USER_PASSWORD", "12345");
    }

}
