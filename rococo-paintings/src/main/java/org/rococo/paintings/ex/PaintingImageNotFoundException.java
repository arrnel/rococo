package org.rococo.paintings.ex;

import java.util.UUID;

public class PaintingImageNotFoundException extends RuntimeException {

    public PaintingImageNotFoundException(UUID paintingId) {
        super("Painting image with painting id = [%s] not found".formatted(paintingId.toString()));
    }

}
