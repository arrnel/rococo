package org.rococo.tests.data.repository;


import org.rococo.tests.data.entity.ArtistEntity;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@ParametersAreNonnullByDefault
public interface ArtistRepository {

    @Nonnull
    ArtistEntity add(ArtistEntity user);

    @Nonnull
    Optional<ArtistEntity> findById(UUID id);

    @Nonnull
    Optional<ArtistEntity> findByName(String name);

    @Nonnull
    List<ArtistEntity> findAllByPartialName(String partialName);

    @Nonnull
    List<ArtistEntity> findAllByIds(List<UUID> ids);

    @Nonnull
    List<ArtistEntity> findAll();

    @Nonnull
    ArtistEntity update(ArtistEntity user);

    void remove(ArtistEntity user);

    void removeAll();
}
