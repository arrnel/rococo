package org.rococo.gateway.ex;

import lombok.Getter;

import javax.annotation.ParametersAreNonnullByDefault;

@Getter
@ParametersAreNonnullByDefault
public class PaintingAlreadyExistException extends RuntimeException {

    private final String title;

    public PaintingAlreadyExistException(String title) {
        super("Painting with title = [%s] already exists".formatted(title));
        this.title = title;
    }

}
