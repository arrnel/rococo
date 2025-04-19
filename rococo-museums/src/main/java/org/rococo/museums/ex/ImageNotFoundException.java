package org.rococo.museums.ex;

import java.util.UUID;

public class ImageNotFoundException extends RuntimeException {

    public ImageNotFoundException(UUID paintingId) {
        super("Museum image with museum id = [%s] not found".formatted(paintingId.toString()));
    }

}
