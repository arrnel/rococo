package org.rococo.artists.ex;

import java.util.UUID;

public class ImageAlreadyExistsException extends RuntimeException {

    public ImageAlreadyExistsException(UUID userId) {
        super("Artist image with artist id = [%s] already exists".formatted(userId.toString()));
    }

}
