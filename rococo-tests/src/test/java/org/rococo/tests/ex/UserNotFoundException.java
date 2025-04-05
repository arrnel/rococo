package org.rococo.tests.ex;

import lombok.Getter;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.UUID;

@Getter
@ParametersAreNonnullByDefault
public class UserNotFoundException extends RuntimeException {

    public UserNotFoundException(final UUID id) {
        super("User with id = [%s] not found".formatted(id));
    }

    public UserNotFoundException(final String username) {
        super("User with username = [%s] not found".formatted(username));
    }

}
