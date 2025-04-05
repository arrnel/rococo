package org.rococo.tests.data.dao;

import org.rococo.tests.data.entity.ImageMetadataEntity;
import org.rococo.tests.enums.EntityType;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@ParametersAreNonnullByDefault
public interface ImageMetadataDao {

    @Nonnull
    ImageMetadataEntity create(ImageMetadataEntity user);

    @Nonnull
    Optional<ImageMetadataEntity> findByEntityTypeAndEntityId(EntityType entityType, UUID entityId);

    @Nonnull
    List<ImageMetadataEntity> findAllByEntityTypeAndEntitiesId(EntityType entityType, List<UUID> entityId);

    @Nonnull
    ImageMetadataEntity update(ImageMetadataEntity user);

    void remove(ImageMetadataEntity user);

    void remove(EntityType entityType);

    void removeAll();

}
