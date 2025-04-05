package org.rococo.gateway.ex;

import lombok.Getter;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.UUID;

@Getter
@ParametersAreNonnullByDefault
public class MuseumNotFoundException extends RuntimeException {

    private final UUID id;

    public MuseumNotFoundException(final UUID id) {
        super("Museum with id = [%s] not found".formatted(id));
        this.id = id;
    }

}
