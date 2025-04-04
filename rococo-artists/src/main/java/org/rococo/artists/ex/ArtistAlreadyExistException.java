package org.rococo.artists.ex;

public class ArtistAlreadyExistException extends RuntimeException {

    public ArtistAlreadyExistException(String artistName) {
        super("Artist with name = [%s] already exists".formatted(artistName));
    }

}
