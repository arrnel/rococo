package org.rococo.files.ex;

import org.rococo.files.data.entity.EntityType;

import java.util.UUID;

public class ImageAlreadyExistsException extends RuntimeException {

    public ImageAlreadyExistsException(EntityType entityType, UUID entityId) {
        super("Entity [%s] with id [%s] already have image".formatted(entityType.name(), entityId.toString()));
    }

}
