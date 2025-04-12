package org.rococo.tests.ex;

import lombok.Getter;

import javax.annotation.ParametersAreNonnullByDefault;

@Getter
@ParametersAreNonnullByDefault
public class MuseumAlreadyExistsException extends RuntimeException {

    public MuseumAlreadyExistsException(final String title) {
        super("Museum with title = [%s] already exists".formatted(title));
    }

}
