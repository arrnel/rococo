package org.rococo.tests.service;

import org.rococo.tests.model.ArtistDTO;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@ParametersAreNonnullByDefault
public interface ArtistService {

    @Nonnull
    ArtistDTO add(ArtistDTO artist);

    @Nonnull
    Optional<ArtistDTO> findById(UUID id);

    @Nonnull
    Optional<ArtistDTO> findByName(String name);

    @Nonnull
    List<ArtistDTO> findAllByPartialName(String partialName);

    @Nonnull
    List<ArtistDTO> findAll();

    @Nonnull
    ArtistDTO update(ArtistDTO artist);

    void delete(UUID id);

    void clearAll();
}
