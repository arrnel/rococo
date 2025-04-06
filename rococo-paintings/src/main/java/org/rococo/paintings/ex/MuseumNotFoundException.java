package org.rococo.paintings.ex;

import java.util.UUID;

public class MuseumNotFoundException extends RuntimeException {

    public MuseumNotFoundException(UUID museumId) {
        super("Museum with id = [%s] not found".formatted(museumId.toString()));
    }

}
