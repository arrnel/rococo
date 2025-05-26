package org.rococo.tests.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
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

}
