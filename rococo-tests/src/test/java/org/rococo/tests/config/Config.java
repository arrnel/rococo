package org.rococo.tests.config;

import org.apache.commons.lang3.EnumUtils;
import org.rococo.tests.enums.ServiceType;

import javax.annotation.ParametersAreNonnullByDefault;
import java.nio.file.Path;
import java.util.List;

@ParametersAreNonnullByDefault
public interface Config {

    static Config getInstance() {
        var env = System.getProperty("test.env", "docker").toLowerCase();
        return switch (env) {
            case "local" -> LocalConfig.INSTANCE;
            case "docker" -> DockerConfig.INSTANCE;
            default -> throw new IllegalArgumentException("Unknown env: " + env);
        };
    }

    String PROJECT_NAME = "rococo-arrnel";

    String artistsJdbcUrl();

    String artistsGrpcHost();

    int artistsPort();

    String authUrl();

    String authJdbcUrl();

    String countriesJdbcUrl();

    String countriesGrpcHost();

    int countriesPort();

    String filesJdbcUrl();

    String filesGrpcHost();

    int filesPort();

    String frontUrl();

    String gatewayUrl();

    String museumsJdbcUrl();

    String museumsGrpcHost();

    int museumsPort();

    String paintingsJdbcUrl();

    String paintingsGrpcHost();

    int paintingsPort();

    String usersJdbcUrl();

    String usersGrpcHost();

    String kafkaAddress();

    default List<String> kafkaTopics() {
        return List.of("users");
    }

    int usersPort();

    String testUserName();

    String testUserPassword();

    int dbPort();

    String dbUser();

    String dbPassword();

    String allureDockerUrl();

    default Path pathToAllureResults() {
        return Path.of("./rococo-tests/build/allure-results");
    }

    default String originalPhotoBaseDir() {
        return "img/original/";
    }

    String screenshotBaseDir();

    default boolean rewriteAllImages() {
        return "true".equalsIgnoreCase(System.getenv("REWRITE_ALL_IMAGES"));
    }

    default ServiceType serviceType() {
        return EnumUtils.getEnum(ServiceType.class, System.getProperty("services.type", "DB"), ServiceType.DB);
    }

    default String gitHubUrl() {
        return "https://api.github.com";
    }

    default boolean addServicesLogsToAllure() {
        return "true".equalsIgnoreCase(System.getenv("ADD_SERVICES_LOGS_TO_ALLURE"));
    }

    default int updateTokenTimeoutMillis() {
        return 60_000;
    }
}