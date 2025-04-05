package org.rococo.tests.data.dao;

import org.rococo.tests.data.entity.ImageContentEntity;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@ParametersAreNonnullByDefault
public interface ImageContentDao {

    @Nonnull
    ImageContentEntity create(ImageContentEntity user);

    @Nonnull
    Optional<ImageContentEntity> findById(UUID id);

    @Nonnull
    List<ImageContentEntity> findAll();

    @Nonnull
    ImageContentEntity update(ImageContentEntity user);

    void remove(ImageContentEntity user);

    void removeAll();

}
