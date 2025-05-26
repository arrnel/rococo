package org.rococo.logs.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.rococo.logs.ex.UnknownServiceNameException;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Arrays;

@Getter
@RequiredArgsConstructor
@ParametersAreNonnullByDefault
public enum ServiceName {
    ARTIST("rococo-artists"),
    AUTH("rococo-auth"),
    COUNTRIES("rococo-countries"),
    FILES("rococo-files"),
    GATEWAY("rococo-gateway"),
    MUSEUMS("rococo-museums"),
    PAINTINGS("rococo-paintings"),
    USERS("rococo-users");

    private final String serviceName;

    @Nonnull
    public static ServiceName findByServiceName(String serviceName) {
        return Arrays.stream(values())
                .filter(s -> s
                        .getServiceName()
                        .equalsIgnoreCase(serviceName))
                .findFirst()
                .orElseThrow(() -> new UnknownServiceNameException("Unknown service type: %s".formatted(serviceName)));
    }

}
