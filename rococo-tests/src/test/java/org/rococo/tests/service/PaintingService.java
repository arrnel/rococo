package org.rococo.tests.service;

import org.rococo.tests.model.PaintingDTO;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@ParametersAreNonnullByDefault
public interface PaintingService {

    @Nonnull
    PaintingDTO add(PaintingDTO painting);

    @Nonnull
    Optional<PaintingDTO> findById(UUID id);

    @Nonnull
    Optional<PaintingDTO> findByTitle(String title);

    @Nonnull
    List<PaintingDTO> findAllByPartialTitle(String partialTitle);

    @Nonnull
    List<PaintingDTO> findAllByArtistId(UUID artistId);

    @Nonnull
    List<PaintingDTO> findAll();

    @Nonnull
    PaintingDTO update(PaintingDTO painting);

    void delete(UUID id);

    void clearAll();

}
