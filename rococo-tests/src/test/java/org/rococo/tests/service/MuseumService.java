package org.rococo.tests.service;

import org.rococo.tests.model.MuseumDTO;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@ParametersAreNonnullByDefault
public interface MuseumService {

    @Nonnull
    MuseumDTO add(MuseumDTO museumDTO);

    @Nonnull
    Optional<MuseumDTO> findById(UUID id);

    @Nonnull
    Optional<MuseumDTO> findByTitle(String title);

    @Nonnull
    List<MuseumDTO> findAllByPartialTitle(String title);

    @Nonnull
    List<MuseumDTO> findAll();

    @Nonnull
    MuseumDTO update(MuseumDTO museumDTO);

    void delete(UUID id);

    void clearAll();

}
