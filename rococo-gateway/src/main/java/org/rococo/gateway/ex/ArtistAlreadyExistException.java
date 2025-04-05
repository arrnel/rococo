package org.rococo.gateway.ex;

import lombok.Getter;

import javax.annotation.ParametersAreNonnullByDefault;

@Getter
@ParametersAreNonnullByDefault
public class ArtistAlreadyExistException extends RuntimeException {

    private final String name;

    public ArtistAlreadyExistException(final String name) {
        super("Artist with name = [%s] not found".formatted(name));
        this.name = name;
    }

}
