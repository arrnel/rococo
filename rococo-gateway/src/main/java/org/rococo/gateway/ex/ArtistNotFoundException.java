package org.rococo.gateway.ex;

import lombok.Getter;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.UUID;

@Getter
@ParametersAreNonnullByDefault
public class ArtistNotFoundException extends RuntimeException {

    private final UUID id;

    public ArtistNotFoundException(final UUID id) {
        super("Artist with id = [%s] not found".formatted(id));
        this.id = id;
    }

}
