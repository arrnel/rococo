package org.rococo.tests.ex;

import lombok.Getter;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.UUID;

@Getter
@ParametersAreNonnullByDefault
public class PaintingNotFoundException extends RuntimeException {

    public PaintingNotFoundException(final UUID id) {
        super("Painting with id = [%s] not found".formatted(id));
    }

    public PaintingNotFoundException(final String title) {
        super("Painting with title = [%s] not found".formatted(title));
    }

}
