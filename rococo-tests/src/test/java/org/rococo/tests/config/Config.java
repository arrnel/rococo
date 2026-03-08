package org.rococo.tests.config;

import org.apache.commons.lang3.EnumUtils;
import org.rococo.tests.enums.ServiceType;
import org.rococo.tests.util.EnvUtil;

import javax.annotation.ParametersAreNonnullByDefault;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

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

    String PROJECT_NAME = EnvUtil.envVar("PROJECT_NAME", "rococo-arrnel");

    // =============== BACKEND
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

    String logsUrl();

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

    // =============== Allure
    String allureDockerUrl();

    default Path pathToAllureResults() {
        return Path.of("./rococo-tests/build/allure-results");
    }

    default boolean addServicesLogsToAllure() {
        return "true".equalsIgnoreCase(System.getenv("ADD_SERVICES_LOGS_TO_ALLURE"));
    }

    // =============== Test
    default ServiceType serviceType() {
        return EnumUtils.getEnum(ServiceType.class, System.getProperty("services.type", "DB"), ServiceType.DB);
    }

    default String originalPhotoBaseDir() {
        return "img/original/";
    }

    String screenshotBaseDir();

    default boolean rewriteAllImages() {
        return EnvUtil.envVar("REWRITE_ALL_IMAGES", false);
    }

    default int updateTokenTimeoutMillis() {
        return EnvUtil.envVar("UPDATE_TOKEN_TIMEOUT", 60_000);
    }

    // ===============  Browser Settings

    default String browserName() {
        return Optional.ofNullable(System.getenv("BROWSER_NAME"))
                .orElse("chrome");
    }

    default String browserVersion() {
        return Optional.ofNullable(System.getenv("BROWSER_VERSION"))
                .orElse("127.0");
    }

    default int timeout() {
        return EnvUtil.envVar("BROWSER_TIMEOUT", 8_000);
    }

    default int pageLoadTimeout() {
        return EnvUtil.envVar("BROWSER_PAGE_LOAD_TIMEOUT", 15_000);
    }

    default String pageLoadStrategy() {
        return EnvUtil.envVar("BROWSER_PAGE_LOAD_STRATEGY", "eager");
    }

    default String browserSize() {
        return EnvUtil.envVar("BROWSER_SIZE", "1920x1080");
    }


    // =============== GitHub
    default String gitHubUrl() {
        return "https://api.github.com";
    }

}
