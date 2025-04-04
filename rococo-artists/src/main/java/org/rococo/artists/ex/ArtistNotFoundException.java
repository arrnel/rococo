package org.rococo.artists.ex;

import java.util.UUID;

public class ArtistNotFoundException extends RuntimeException {

    public ArtistNotFoundException(UUID artistId) {
        super("Artist with id = [%s] not found".formatted(artistId.toString()));
    }

    public ArtistNotFoundException(String name) {
        super("Artist with name = [%s] not found".formatted(name));
    }


}
