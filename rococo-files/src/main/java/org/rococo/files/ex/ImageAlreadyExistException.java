package org.rococo.files.ex;

import org.rococo.files.data.entity.EntityType;

import java.util.UUID;

public class ImageAlreadyExistException extends RuntimeException {

    public ImageAlreadyExistException(EntityType entityType, UUID entityId) {
        super("Entity [%s] with id [%s] already have image".formatted(entityType.name(), entityId.toString()));
    }

}
