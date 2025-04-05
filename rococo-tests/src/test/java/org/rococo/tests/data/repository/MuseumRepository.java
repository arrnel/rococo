package org.rococo.tests.data.repository;


import org.rococo.tests.data.entity.MuseumEntity;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@ParametersAreNonnullByDefault
public interface MuseumRepository {

    @Nonnull
    MuseumEntity add(MuseumEntity user);

    @Nonnull
    Optional<MuseumEntity> findById(UUID id);

    @Nonnull
    Optional<MuseumEntity> findByTitle(String title);

    @Nonnull
    List<MuseumEntity> findAllByPartialTitle(String title);

    @Nonnull
    List<MuseumEntity> findAll();

    @Nonnull
    MuseumEntity update(MuseumEntity user);

    void remove(MuseumEntity user);

    void removeAll();

}
