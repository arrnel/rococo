package org.rococo.paintings.ex;

import java.util.UUID;

public class ImageNotFoundException extends RuntimeException {

    public ImageNotFoundException(UUID paintingId) {
        super("Painting image with painting id = [%s] not found".formatted(paintingId.toString()));
    }

}
