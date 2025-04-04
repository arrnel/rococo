package org.rococo.paintings.ex;

import java.util.UUID;

public class PaintingNotFoundException extends RuntimeException {

    public PaintingNotFoundException(UUID paintingId) {
        super("Painting with id = [%s] not found".formatted(paintingId.toString()));
    }

}
