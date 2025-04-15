package org.rococo.gateway.ex;

import lombok.Getter;

import javax.annotation.ParametersAreNonnullByDefault;

@Getter
@ParametersAreNonnullByDefault
public class UserAlreadyExistsException extends RuntimeException {

    private final String username;

    public UserAlreadyExistsException(final String username) {
        super("User with username = [%s] already exists".formatted(username));
        this.username = username;
    }

}
