package org.rococo.paintings.ex;

import lombok.Getter;

import java.util.UUID;

@Getter
public class ArtistNotFoundException extends RuntimeException {

    public ArtistNotFoundException(UUID artistId) {
        super("Artist with id = [%s] not found".formatted(artistId.toString()));
    }

}
