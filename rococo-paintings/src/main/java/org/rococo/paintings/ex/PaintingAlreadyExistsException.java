package org.rococo.paintings.ex;

public class PaintingAlreadyExistsException extends RuntimeException {

    public PaintingAlreadyExistsException(String paintingName) {
        super("Painting with title = [%s] already exists".formatted(paintingName));
    }

}
