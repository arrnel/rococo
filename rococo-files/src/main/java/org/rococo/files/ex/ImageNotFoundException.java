package org.rococo.files.ex;

import org.rococo.files.data.entity.EntityType;

import java.util.UUID;

public class ImageNotFoundException extends RuntimeException {

    public ImageNotFoundException(EntityType entityType, UUID entityId) {
        super("Image with entity type = [%s] and entity id = [%s] not found".formatted(entityType.name(), entityId.toString()));
    }

}
