package org.rococo.gateway.ex;

import lombok.Getter;

import javax.annotation.ParametersAreNonnullByDefault;

@Getter
@ParametersAreNonnullByDefault
public class CurrentUserNotFoundException extends RuntimeException {

    private final String username;

    public CurrentUserNotFoundException(final String username) {
        super("Current user with username = [%s] not found".formatted(username));
        this.username = username;
    }

}
