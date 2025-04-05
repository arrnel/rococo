package org.rococo.tests.ex;

import lombok.Getter;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.UUID;

@Getter
@ParametersAreNonnullByDefault
public class ArtistNotFoundException extends RuntimeException {


    public ArtistNotFoundException(final UUID id) {
        super("Artist with id = [%s] not found".formatted(id));
    }

    public ArtistNotFoundException(final String name) {
        super("Artist with name = [%s] not found".formatted(name));
    }

}
