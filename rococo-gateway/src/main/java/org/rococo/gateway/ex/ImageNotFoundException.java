package org.rococo.gateway.ex;

import lombok.Getter;
import org.rococo.gateway.model.EntityType;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.UUID;

@Getter
@ParametersAreNonnullByDefault
public class ImageNotFoundException extends RuntimeException {

    private final EntityType entityType;
    private final UUID entityId;

    public ImageNotFoundException(final EntityType entityType, final UUID entityId) {
        super("Image with entityType = [%s] and entityId = [%s] not found".formatted(entityType, entityId));
        this.entityType = entityType;
        this.entityId = entityId;
    }

}
