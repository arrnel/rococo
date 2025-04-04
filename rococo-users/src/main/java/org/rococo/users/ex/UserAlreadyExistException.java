package org.rococo.users.ex;

public class UserAlreadyExistException extends RuntimeException {

    public UserAlreadyExistException(String username) {
        super("User with username = [%s] already exists".formatted(username));
    }

}
