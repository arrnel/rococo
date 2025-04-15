package org.rococo.museum.ex;

public class MuseumAlreadyExistsException extends RuntimeException {

    public MuseumAlreadyExistsException(String museumName) {
        super("Museum with name = [%s] already exists".formatted(museumName));
    }

}
