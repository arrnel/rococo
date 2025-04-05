package org.rococo.tests.data.dao;

import org.rococo.tests.data.entity.ArtistEntity;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@ParametersAreNonnullByDefault
public interface ArtistDao {

    @Nonnull
    ArtistEntity create(ArtistEntity user);

    @Nonnull
    Optional<ArtistEntity> findById(UUID id);

    @Nonnull
    Optional<ArtistEntity> findByName(String name);

    @Nonnull
    List<ArtistEntity> findAllByPartialName(String partialName);

    @Nonnull
    List<ArtistEntity> findAll();

    @Nonnull
    ArtistEntity update(ArtistEntity user);

    void remove(ArtistEntity user);

    void removeAll();

}
