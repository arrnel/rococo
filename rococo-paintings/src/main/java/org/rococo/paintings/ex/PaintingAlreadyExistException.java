package org.rococo.paintings.ex;

public class PaintingAlreadyExistException extends RuntimeException {

    public PaintingAlreadyExistException(String paintingName) {
        super("Painting with title = [%s] already exists".formatted(paintingName));
    }

}
