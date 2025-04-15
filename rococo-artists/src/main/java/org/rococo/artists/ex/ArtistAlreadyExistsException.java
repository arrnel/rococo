package org.rococo.artists.ex;

public class ArtistAlreadyExistsException extends RuntimeException {

    public ArtistAlreadyExistsException(String artistName) {
        super("Artist with name = [%s] already exists".formatted(artistName));
    }

}
