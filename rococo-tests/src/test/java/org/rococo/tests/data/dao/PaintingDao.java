package org.rococo.tests.data.dao;

import org.rococo.tests.data.entity.PaintingEntity;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@ParametersAreNonnullByDefault
public interface PaintingDao {

    @Nonnull
    PaintingEntity create(PaintingEntity user);

    @Nonnull
    Optional<PaintingEntity> findById(UUID id);

    @Nonnull
    Optional<PaintingEntity> findByTitle(String title);

    @Nonnull
    List<PaintingEntity> findAllByPartialTitle(String partialTitle);

    @Nonnull
    List<PaintingEntity> findAllByArtistId(UUID artistId);

    @Nonnull
    List<PaintingEntity> findAllByTitles(List<String> titles);

    @Nonnull
    List<PaintingEntity> findAll();

    @Nonnull
    PaintingEntity update(PaintingEntity user);

    void remove(PaintingEntity user);

    void removeAll();

}
