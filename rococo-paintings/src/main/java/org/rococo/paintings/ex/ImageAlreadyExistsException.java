package org.rococo.paintings.ex;

import java.util.UUID;

public class ImageAlreadyExistsException extends RuntimeException {

    public ImageAlreadyExistsException(UUID museumId) {
        super("Museum image with museum id = [%s] already exists".formatted(museumId.toString()));
    }

}
