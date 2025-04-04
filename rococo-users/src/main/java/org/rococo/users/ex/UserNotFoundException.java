package org.rococo.users.ex;

import java.util.UUID;

public class UserNotFoundException extends RuntimeException {

    public UserNotFoundException(UUID userId) {
        super("User with id = [%s] not found".formatted(userId.toString()));
    }

    public UserNotFoundException(String username) {
        super("User with username = [%s] not found".formatted(username));
    }

}
