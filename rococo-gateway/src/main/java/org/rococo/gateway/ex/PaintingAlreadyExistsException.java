package org.rococo.gateway.ex;

import lombok.Getter;

import javax.annotation.ParametersAreNonnullByDefault;

@Getter
@ParametersAreNonnullByDefault
public class PaintingAlreadyExistsException extends RuntimeException {

    private final String title;

    public PaintingAlreadyExistsException(String title) {
        super("Painting with title = [%s] already exists".formatted(title));
        this.title = title;
    }

}
