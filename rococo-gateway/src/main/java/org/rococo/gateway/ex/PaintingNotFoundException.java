package org.rococo.gateway.ex;

import lombok.Getter;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.UUID;

@Getter
@ParametersAreNonnullByDefault
public class PaintingNotFoundException extends RuntimeException {

    private final UUID id;

    public PaintingNotFoundException(final UUID id) {
        super("Painting with id = [%s] not found".formatted(id));
        this.id = id;
    }

}
