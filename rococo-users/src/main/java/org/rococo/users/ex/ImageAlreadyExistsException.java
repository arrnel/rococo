package org.rococo.users.ex;

import java.util.UUID;

public class ImageAlreadyExistsException extends RuntimeException {

    public ImageAlreadyExistsException(UUID userId) {
        super("User image with user id = [%s] already exists".formatted(userId.toString()));
    }

}
