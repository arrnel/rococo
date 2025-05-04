package org.rococo.tests.config;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
enum DockerConfig implements Config {

    INSTANCE;

    @Nonnull
    @Override
    public String artistsGrpcHost() {
        return "artists.rococo.dc";
    }

    @Override
    public int artistsPort() {
        return 9002;
    }

    @Nonnull
    @Override
    public String artistsJdbcUrl() {
        return "jdbc:postgresql://rococo-db:5432/rococo-artists";
    }

    @Nonnull
    @Override
    public String authUrl() {
        return "http://auth.rococo.dc:9000";
    }

    @Nonnull
    @Override
    public String authJdbcUrl() {
        return "jdbc:postgresql://rococo-db:5432/rococo-auth";
    }

    @Nonnull
    @Override
    public String countriesGrpcHost() {
        return "countries.rococo.dc";
    }

    @Override
    public int countriesPort() {
        return 9003;
    }

    @Nonnull
    @Override
    public String countriesJdbcUrl() {
        return "jdbc:postgresql://rococo-db:5432/rococo-countries";
    }

    @Nonnull
    @Override
    public String filesJdbcUrl() {
        return "jdbc:postgresql://rococo-db:5432/rococo-files";
    }

    @Nonnull
    @Override
    public String filesGrpcHost() {
        return "files.rococo.dc";
    }

    @Override
    public int filesPort() {
        return 9004;
    }

    @Nonnull
    @Override
    public String frontUrl() {
        return "http://frontend.rococo.dc";
    }

    @Nonnull
    @Override
    public String gatewayUrl() {
        return "http://gateway.rococo.dc:9001";
    }

    @Nonnull
    @Override
    public String museumsGrpcHost() {
        return "museums.rococo.dc";
    }

    @Override
    public int museumsPort() {
        return 9005;
    }

    @Nonnull
    @Override
    public String museumsJdbcUrl() {
        return "jdbc:postgresql://rococo-db:5432/rococo-museums";
    }

    @Nonnull
    @Override
    public String paintingsGrpcHost() {
        return "paintings.rococo.dc";
    }

    @Override
    public int paintingsPort() {
        return 9006;
    }

    @Nonnull
    @Override
    public String paintingsJdbcUrl() {
        return "jdbc:postgresql://rococo-db:5432/rococo-paintings";
    }

    @Nonnull
    @Override
    public String usersJdbcUrl() {
        return "jdbc:postgresql://rococo-db:5432/rococo-users";
    }

    @Nonnull
    @Override
    public String usersGrpcHost() {
        return "users.rococo.dc";
    }

    @Nonnull
    @Override
    public String kafkaAddress() {
        return "kafka:9092";
    }

    @Override
    public int usersPort() {
        return 9007;
    }

    @Override
    public String testUserName() {
        return "test_user";
    }

    @Override
    public String testUserPassword() {
        return "12345";
    }

    @Override
    public int dbPort() {
        return 5432;
    }

    @Override
    public String dbUser() {
        return "postgres";
    }

    @Override
    public String dbPassword() {
        return "secret";
    }

    @Override
    public String allureDockerUrl() {
        final String url = System.getenv("ALLURE_DOCKER_API");
        return url == null
                ? "http://allure:5050"
                : url;
    }

    @Override
    public String screenshotBaseDir() {
        return "rococo-tests/.screen-output/screenshots/remote/";
    }

}
