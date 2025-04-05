package org.rococo.tests.ex;

import lombok.Getter;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.UUID;

@Getter
@ParametersAreNonnullByDefault
public class MuseumNotFoundException extends RuntimeException {

    public MuseumNotFoundException(final UUID id) {
        super("Museum with id = [%s] not found".formatted(id));
    }

    public MuseumNotFoundException(final String title) {
        super("Museum with title = [%s] not found".formatted(title));
    }

}
