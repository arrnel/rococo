package org.rococo.tests.data.dao;

import org.rococo.tests.data.entity.MuseumEntity;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@ParametersAreNonnullByDefault
public interface MuseumDao {

    @Nonnull
    MuseumEntity create(MuseumEntity user);

    @Nonnull
    Optional<MuseumEntity> findById(UUID id);

    @Nonnull
    Optional<MuseumEntity> findByTitle(String title);

    @Nonnull
    List<MuseumEntity> findAllByPartialTitle(String partialTitle);

    @Nonnull
    List<MuseumEntity> findAllByIds(List<UUID> ids);

    @Nonnull
    List<MuseumEntity> findAll();

    @Nonnull
    MuseumEntity update(MuseumEntity user);

    void remove(MuseumEntity user);

    void removeAll();

}
