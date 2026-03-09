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
    String ENV = EnvUtil.envVar("ENV", "docker").trim().toLowerCase();

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

    String remoteUrl();

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
        return EnvUtil.envVar("REWRITE_ALL_SCREENSHOTS", false);
    }

    default int updateTokenTimeoutMillis() {
        return EnvUtil.envVar("UPDATE_TOKEN_TIMEOUT", 60_000);
    }

    // ===============  Browser Settings

    default String browserName() {
        return Optional.ofNullable(System.getenv("BROWSER_NAME"))
                .orElse("chrome").toLowerCase();
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

    String browserDownloadDir();

    default String browser_remote_attach_id_type() {
        return EnvUtil.envVar("BROWSER_REMOTE_ATTACH_ID_TYPE", "test_name");
    }

    default boolean enableVNC() {
        return EnvUtil.envVar("BROWSER_REMOTE_VNC", true);
    }

    default boolean enableVideo() {
        return EnvUtil.envVar("BROWSER_REMOTE_VIDEO", true);
    }

    default boolean enableLogs() {
        return EnvUtil.envVar("BROWSER_REMOTE_LOGS", true);
    }

    default int browserRemoteSessionTimeout(){
        return 15_000;
    }

    default boolean isLocal() {
        return "local".equalsIgnoreCase(EnvUtil.envVar("ENV", "docker").trim());
    }

    default boolean isTwilioBrowser() {

        var browserVersion = Double.parseDouble(browserVersion());
        var isChromeTwilio = browserName().equals("chrome") && browserVersion > 128.0;
        var isFirefoxTwilio = browserName().equals("firefox") && browserVersion > 125.0;

        return isChromeTwilio || isFirefoxTwilio;

    }

    // =============== GitHub
    default String gitHubUrl() {
        return "https://api.github.com";
    }

}
