package org.rococo.artists.ex;

import java.util.UUID;

public class ImageNotFoundException extends RuntimeException {

    public ImageNotFoundException(UUID artistId) {
        super("Image with entity_type = ARTIST and id = [%s] not found".formatted(artistId.toString()));
    }

}
