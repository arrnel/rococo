package org.rococo.tests.data.repository;


import org.rococo.tests.data.entity.PaintingEntity;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@ParametersAreNonnullByDefault
public interface PaintingRepository {

    @Nonnull
    PaintingEntity create(PaintingEntity painting);

    @Nonnull
    Optional<PaintingEntity> findById(UUID id);

    @Nonnull
    Optional<PaintingEntity> findByTitle(String title);

    @Nonnull
    List<PaintingEntity> findAllByPartialTitle(String title);

    @Nonnull
    List<PaintingEntity> findAllByArtistId(UUID artistId);

    @Nonnull
    List<PaintingEntity> findAllByTitles(List<String> titles);

    @Nonnull
    List<PaintingEntity> findAll();

    @Nonnull
    PaintingEntity update(PaintingEntity painting);

    void remove(PaintingEntity painting);

    void removeAll();

}
