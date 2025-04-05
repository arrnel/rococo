package org.rococo.gateway.ex;

import lombok.Getter;

import javax.annotation.ParametersAreNonnullByDefault;

@Getter
@ParametersAreNonnullByDefault
public class MuseumAlreadyExistException extends RuntimeException {

    private final String title;

    public MuseumAlreadyExistException(final String title) {
        super("Museum with title = [%s] already exists".formatted(title));
        this.title = title;
    }

}
