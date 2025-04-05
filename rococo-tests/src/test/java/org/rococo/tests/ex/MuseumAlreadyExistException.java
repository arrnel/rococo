package org.rococo.tests.ex;

import lombok.Getter;

import javax.annotation.ParametersAreNonnullByDefault;

@Getter
@ParametersAreNonnullByDefault
public class MuseumAlreadyExistException extends RuntimeException {

    public MuseumAlreadyExistException(final String title) {
        super("Museum with title = [%s] already exists".formatted(title));
    }

}
