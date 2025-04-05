package org.rococo.tests.data.repository.impl.springJdbc;

import org.rococo.tests.data.dao.ImageContentDao;
import org.rococo.tests.data.dao.ImageMetadataDao;
import org.rococo.tests.data.dao.impl.springJdbc.ImageContentDaoSpringJdbc;
import org.rococo.tests.data.dao.impl.springJdbc.ImageMetadataDaoSpringJdbc;
import org.rococo.tests.data.entity.ImageMetadataEntity;
import org.rococo.tests.data.repository.FilesRepository;
import org.rococo.tests.enums.EntityType;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@ParametersAreNonnullByDefault
public class FilesRepositorySpringJdbc implements FilesRepository {

    private final ImageMetadataDao imageMetadataDao = new ImageMetadataDaoSpringJdbc();
    private final ImageContentDao imageContentDao = new ImageContentDaoSpringJdbc();

    @Nonnull
    @Override
    public ImageMetadataEntity create(ImageMetadataEntity imageMetadata) {
        return imageMetadataDao.create(
                imageMetadata.setContent(
                        imageContentDao.create(imageMetadata.getContent())
                ));
    }

    @Override
    public Optional<ImageMetadataEntity> findByEntityTypeAndEntityId(EntityType entityType, UUID entityId) {
        return imageMetadataDao.findByEntityTypeAndEntityId(entityType, entityId);
    }

    @Override
    public List<ImageMetadataEntity> findAllByEntityTypeAndEntityIds(EntityType entityType, List<UUID> entitiesId) {
        return List.of();
    }

    @Nonnull
    @Override
    public ImageMetadataEntity update(ImageMetadataEntity imageMetadata) {
        return imageMetadataDao.update(imageMetadata);
    }

    @Override
    public void remove(ImageMetadataEntity imageMetadata) {
        imageMetadataDao.remove(imageMetadata);
    }

    @Override
    public void removeAll(EntityType entityType) {
        imageMetadataDao.remove(entityType);
    }

    @Override
    public void removeAll() {
        imageMetadataDao.removeAll();
    }

}
