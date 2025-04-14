package org.rococo.tests.tests.fake.grpc;

import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import net.datafaker.Faker;
import org.hamcrest.Matcher;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.rococo.tests.ex.ArtistNotFoundException;
import org.rococo.tests.ex.MuseumNotFoundException;
import org.rococo.tests.ex.PaintingAlreadyExistsException;
import org.rococo.tests.jupiter.annotation.Artist;
import org.rococo.tests.jupiter.annotation.Museum;
import org.rococo.tests.jupiter.annotation.Painting;
import org.rococo.tests.jupiter.annotation.Paintings;
import org.rococo.tests.jupiter.annotation.meta.GrpcTest;
import org.rococo.tests.jupiter.annotation.meta.InjectService;
import org.rococo.tests.model.ArtistDTO;
import org.rococo.tests.model.MuseumDTO;
import org.rococo.tests.model.PaintingDTO;
import org.rococo.tests.service.PaintingService;
import org.rococo.tests.util.DataGenerator;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.rococo.tests.enums.ServiceType.GRPC;

@GrpcTest
@Feature("FAKE")
@Story("[GRPC] Paintings tests")
@DisplayName("[GRPC] Paintings tests")
@ParametersAreNonnullByDefault
class PaintingGrpcTest {

    @InjectService(GRPC)
    private PaintingService paintingService;

    @Artist
    @Museum
    @Test
    @DisplayName("Can create painting")
    void canCreatePaintingTest(ArtistDTO artist, MuseumDTO museum) {

        // Data
        var painting = DataGenerator.generatePainting()
                .setArtist(artist)
                .setMuseum(museum);

        // Steps
        var result = paintingService.add(painting);

        // Assertions
        assertNotNull(result.getId());

    }

    @Painting
    @Test
    @DisplayName("Can not create painting with exists name")
    void canNotCreatePaintingWithExistsNameTest(PaintingDTO painting) {

        // Steps
        var result = assertThrows(PaintingAlreadyExistsException.class, () -> paintingService.add(painting));

        // Assertions
        assertEquals("Painting with title = [%s] already exists".formatted(painting.getTitle()), result.getMessage());

    }

    @Museum
    @Test
    @DisplayName("Can not update painting with unknown artist")
    void canNotCreatePaintingWithUnknownArtistTest(MuseumDTO museum) {
        // Data
        var painting = DataGenerator.generatePainting();
        painting.setMuseum(museum)
                .getArtist().setId(UUID.randomUUID());

        // Steps & Assertions
        assertThrows(ArtistNotFoundException.class, () -> paintingService.add(painting));
    }

    @Artist
    @Test
    @DisplayName("Can not create painting with unknown museum")
    void canNotCreatePaintingWithUnknownMuseumTest(ArtistDTO artist) {
        // Data
        var painting = DataGenerator.generatePainting();
        painting.setArtist(artist)
                .getMuseum().setId(UUID.randomUUID());

        // Steps & Assertions
        assertThrows(MuseumNotFoundException.class, () -> paintingService.add(painting));
    }

    @Painting
    @Test
    @DisplayName("Can get painting by id")
    void canGetPaintingByIdTest(PaintingDTO painting) {

        // Steps
        var result = paintingService.findById(painting.getId()).orElse(null);

        // Assertions
        assertThat(result, allOf(
                hasProperty("id", is(painting.getId())),
                hasProperty("title", is(painting.getTitle())),
                hasProperty("description", is(painting.getDescription())),
                hasProperty("artist", hasProperty("id", is(painting.getArtist().getId()))),
                hasProperty("museum", hasProperty("id", is(painting.getMuseum().getId())))
        ));

    }

    @Test
    @DisplayName("Returns Optional.empty() if search painting by unknown id")
    void canGetEmptyPaintingByUnknownIdTest() {

        // Steps
        var result = paintingService.findById(UUID.randomUUID());

        // Assertions
        assertTrue(result.isEmpty());

    }

    @Painting
    @Test
    @DisplayName("Can get painting by title")
    void canGetPaintingByTitle(PaintingDTO painting) {

        // Steps
        var result = paintingService.findByTitle(painting.getTitle()).orElse(null);

        // Assertions
        assertThat(result, allOf(
                hasProperty("id", is(painting.getId())),
                hasProperty("title", is(painting.getTitle())),
                hasProperty("description", is(painting.getDescription())),
                hasProperty("artist", hasProperty("id", is(painting.getArtist().getId()))),
                hasProperty("museum", hasProperty("id", is(painting.getMuseum().getId())))
        ));

    }

    @Test
    @DisplayName("Returns Optional.empty() if search painting by unknown title")
    void canGetEmptyPaintingByUnknownTitleTest() {

        // Steps
        var result = paintingService.findByTitle(new Faker().detectiveConan().characters());

        // Assertions
        assertTrue(result.isEmpty());

    }

    @Artist(name = "Claude Monet")
    @Paintings({
            @Painting(artist = @Artist(name = "Claude Monet")),
            @Painting(artist = @Artist(name = "Claude Monet")),
            @Painting(artist = @Artist(name = "Claude Monet"))}
    )
    @Test
    @DisplayName("Can get all artist paintings")
    void canGetAllArtistPaintingsTest(ArtistDTO artist, List<PaintingDTO> paintings) {

        // Steps
        var result = paintingService.findAllByArtistId(artist.getId());

        // Assertions
        assertThat(result,
                hasItems(paintings.stream()
                        .map(painting -> allOf(
                                hasProperty("id", is(painting.getId())),
                                hasProperty("title", is(painting.getTitle())),
                                hasProperty("description", is(painting.getDescription())),
                                hasProperty("artist", hasProperty("id", is(painting.getArtist().getId()))),
                                hasProperty("museum", hasProperty("id", is(painting.getMuseum().getId())))
                        ))
                        .toArray(Matcher[]::new)
                ));

    }

    @Paintings(count = 3)
    @Test
    @DisplayName("Can get all paintings")
    void canGetAllPaintingsTest(List<PaintingDTO> paintings) {

        // Steps
        var result = paintingService.findAll();

        // Assertions
        assertThat(result,
                hasItems(paintings.stream()
                        .map(painting -> allOf(
                                hasProperty("id", is(painting.getId())),
                                hasProperty("title", is(painting.getTitle())),
                                hasProperty("description", is(painting.getDescription())),
                                hasProperty("artist", hasProperty("id", is(painting.getArtist().getId()))),
                                hasProperty("museum", hasProperty("id", is(painting.getMuseum().getId())))
                        ))
                        .toArray(Matcher[]::new)
                ));

    }

    @Artist
    @Museum
    @Painting
    @Test
    @DisplayName("Can update painting")
    void canUpdatePaintingTest(PaintingDTO oldPainting,
                               ArtistDTO newArtist,
                               MuseumDTO newMuseum
    ) {

        // Data
        var newPainting = DataGenerator.generatePainting()
                .setId(oldPainting.getId())
                .setArtist(newArtist)
                .setMuseum(newMuseum);

        // Steps
        paintingService.update(newPainting);
        var result = paintingService.findById(newPainting.getId()).orElse(null);

        // Assertions
        assertThat(result, allOf(
                hasProperty("id", is(newPainting.getId())),
                hasProperty("title", is(newPainting.getTitle())),
                hasProperty("description", is(newPainting.getDescription())),
                hasProperty("artist", hasProperty("id", is(newPainting.getArtist().getId()))),
                hasProperty("museum", hasProperty("id", is(newPainting.getMuseum().getId())))
        ));

    }

    @Paintings(count = 2)
    @Test
    @DisplayName("Can not update painting name to exist")
    void canNotUpdatePaintingNameToExistTest(List<PaintingDTO> paintings) {

        // Data
        var painting = paintings.getFirst()
                .setTitle(paintings.getLast().getTitle());

        // Steps & Assertions
        var result = assertThrows(PaintingAlreadyExistsException.class, () -> paintingService.update(painting));

        // Assertions
        assertEquals("Painting with title = [%s] already exists".formatted(painting.getTitle()), result.getMessage());

    }

    @Painting
    @Test
    @DisplayName("Can not update painting with unknown artist")
    void canNotUpdatePaintingWithUnknownArtistTest(PaintingDTO painting) {
        // Data
        painting.getArtist().setId(UUID.randomUUID());

        // Steps & Assertions
        assertThrows(ArtistNotFoundException.class, () -> paintingService.update(painting));
    }

    @Painting
    @Test
    @DisplayName("Can not update painting with unknown museum")
    void canNotUpdatePaintingWithUnknownMuseumTest(PaintingDTO painting) {
        // Data
        painting.getMuseum().setId(UUID.randomUUID());

        // Steps & Assertions
        assertThrows(MuseumNotFoundException.class, () -> paintingService.update(painting));
    }

    @Painting
    @Test
    @DisplayName("Can delete painting")
    void canDeletePaintingTest(PaintingDTO painting) {

        // Steps
        paintingService.delete(painting.getId());

        // Assertions
        assertTrue(paintingService.findById(painting.getId()).isEmpty());

    }

}
