package org.rococo.museum.ex;

import java.util.UUID;

public class MuseumNotFoundException extends RuntimeException {

    public MuseumNotFoundException(UUID museumId) {
        super("Museum with id = [%s] not found".formatted(museumId.toString()));
    }

    public MuseumNotFoundException(String title) {
        super("Museum with title = [%s] not found".formatted(title));
    }

}
