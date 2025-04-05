package org.rococo.tests.service;

import org.rococo.tests.model.ArtistDTO;
import org.rococo.tests.model.MuseumDTO;
import org.rococo.tests.model.PaintingDTO;
import org.rococo.tests.model.UserDTO;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

@ParametersAreNonnullByDefault
public interface RococoService {

    @Nonnull
    ArtistDTO addArtist(ArtistDTO artist);

    @Nonnull
    List<ArtistDTO> addArtists(List<ArtistDTO> artist);

    @Nonnull
    List<ArtistDTO> addArtists(int count);

    @Nonnull
    MuseumDTO addMuseum(MuseumDTO museum);

    @Nonnull
    List<MuseumDTO> addMuseums(List<MuseumDTO> museums);

    @Nonnull
    List<MuseumDTO> addMuseums(int count);

    @Nonnull
    PaintingDTO addPainting(PaintingDTO painting);

    @Nonnull
    List<PaintingDTO> addPaintings(List<PaintingDTO> paintings);

    @Nonnull
    List<PaintingDTO> addPaintings(int count);

    @Nonnull
    UserDTO addUser(UserDTO user);

    @Nonnull
    List<UserDTO> addUsers(List<UserDTO> users);

    @Nonnull
    List<UserDTO> addUsers(int count);

}
