package org.rococo.tests.ex;

import lombok.Getter;

import javax.annotation.ParametersAreNonnullByDefault;

@Getter
@ParametersAreNonnullByDefault
public class UserAlreadyExistException extends RuntimeException {

    private final String username;

    public UserAlreadyExistException(final String username) {
        super("User with username = [%s] already exists".formatted(username));
        this.username = username;
    }

}
