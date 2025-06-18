package org.rococo.tests.model;

public record Token(
        String token
) {

    public Token token(String token) {
        return new Token(token);
    }

}
