package org.rococo.tests.config;

import org.apache.commons.lang3.EnumUtils;
import org.rococo.tests.enums.ServiceType;

import javax.annotation.ParametersAreNonnullByDefault;
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

    default ServiceType serviceType() {
        return EnumUtils.getEnum(ServiceType.class, System.getProperty("services.type", "DB"), ServiceType.DB);
    }

    default String gitHubUrl() {
        return "https://api.github.com";
    }

    // Properties
    default boolean addServicesLogsToAllure() {
        return Boolean.parseBoolean(System.getProperty("addServicesLogsToAllure", "false"));
    }

    default int updateTokenTimeoutMillis() {
        return 60_000;
    };
}