package org.rococo.museum.ex;

public class MuseumAlreadyExistException extends RuntimeException {

    public MuseumAlreadyExistException(String museumName) {
        super("Museum with name = [%s] already exists".formatted(museumName));
    }

}
