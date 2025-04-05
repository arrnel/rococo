package org.rococo.tests.data.repository;


import org.rococo.tests.data.entity.ImageMetadataEntity;
import org.rococo.tests.enums.EntityType;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface FilesRepository {

    ImageMetadataEntity create(ImageMetadataEntity user);

    Optional<ImageMetadataEntity> findByEntityTypeAndEntityId(EntityType entityType, UUID entityId);

    List<ImageMetadataEntity> findAllByEntityTypeAndEntityIds(EntityType entityType, List<UUID> entitiesId);

    ImageMetadataEntity update(ImageMetadataEntity user);

    void remove(ImageMetadataEntity user);

    void removeAll(EntityType entityType);

    void removeAll();

}
