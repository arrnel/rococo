package org.rococo.tests.ex;

public class TokenIsEmptyException extends RuntimeException {

    public TokenIsEmptyException(String message) {
        super(message);
    }

    public TokenIsEmptyException() {
        super("Token is empty");
    }
}
