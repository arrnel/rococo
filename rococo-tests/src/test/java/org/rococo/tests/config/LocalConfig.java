package org.rococo.tests.config;

import org.rococo.tests.util.EnvUtil;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;


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
        return EnvUtil.envVar("ROCOCO_ARTISTS_PORT", 9002);
    }

    @Nonnull
    @Override
    public String authJdbcUrl() {
        return "jdbc:postgresql://127.0.0.1:%d/rococo-auth".formatted(dbPort());
    }

    @Nonnull
    @Override
    public String authUrl() {
        return "http://127.0.0.1:%d".formatted(EnvUtil.envVar("ROCOCO_AUTH_PORT", 9000));
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
        return EnvUtil.envVar("ROCOCO_COUNTRIES_PORT", 9003);
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
        return EnvUtil.envVar("ROCOCO_FILES_PORT", 9004);
    }

    @Nonnull
    @Override
    public String frontUrl() {
        return "http://127.0.0.1:%d".formatted(EnvUtil.envVar("ROCOCO_FRONT_PORT", 3000));
    }

    @Nonnull
    @Override
    public String gatewayUrl() {
        return "http://127.0.0.1:%d".formatted(EnvUtil.envVar("ROCOCO_GATEWAY_PORT", 9001));
    }

    @Nonnull
    @Override
    public String logsUrl() {
        return "http://127.0.0.1:%d".formatted(EnvUtil.envVar("ROCOCO_LOGS_PORT", 9008));
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
        return EnvUtil.envVar("ROCOCO_MUSEUMS_PORT", 9005);
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
        return EnvUtil.envVar("ROCOCO_PAINTINGS_PORT", 9006);
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
        return "127.0.0.1:" + EnvUtil.envVar("ROCOCO_KAFKA_PORT", 9092);
    }

    @Override
    public int usersPort() {
        return EnvUtil.envVar("ROCOCO_USERS_PORT", 9007);
    }

    public int dbPort() {
        return EnvUtil.envVar("ROCOCO_DB_PORT", 5432);
    }

    public String dbUser() {
        return EnvUtil.envVar("ROCOCO_DB_USER", "postgres");
    }

    public String dbPassword() {
        return EnvUtil.envVar("ROCOCO_DB_PASSWORD", "secret");
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
        return EnvUtil.envVar("ROCOCO_TEST_USERNAME", "test_user");
    }

    @Nonnull
    public String testUserPassword() {
        return EnvUtil.envVar("ROCOCO_TEST_USER_PASSWORD", "12345");
    }

    @Override
    public String remoteUrl() {
        throw new RuntimeException("Unavailable to use remote url in local env");
    }

    @Override
    public String browserDownloadDir(){
        return "${HOME}/Downloads/test_temp_dir";
    }

}
