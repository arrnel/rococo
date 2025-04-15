package org.rococo.tests.ex;

import lombok.Getter;

import javax.annotation.ParametersAreNonnullByDefault;

@Getter
@ParametersAreNonnullByDefault
public class ArtistAlreadyExistsException extends RuntimeException {

    private final String name;

    public ArtistAlreadyExistsException(final String name) {
        super("Artist with name = [%s] already exists".formatted(name));
        this.name = name;
    }

}
