package org.rococo.tests.tests.fake.grpc;

import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import net.datafaker.Faker;
import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.rococo.tests.ex.ArtistAlreadyExistsException;
import org.rococo.tests.jupiter.annotation.Artist;
import org.rococo.tests.jupiter.annotation.Artists;
import org.rococo.tests.jupiter.annotation.meta.GrpcTest;
import org.rococo.tests.jupiter.annotation.meta.InjectService;
import org.rococo.tests.model.ArtistDTO;
import org.rococo.tests.service.ArtistService;
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
@Story("[GRPC] Artists tests")
@DisplayName("[GRPC] Artists tests")
@ParametersAreNonnullByDefault
class ArtistGrpcTest {

    @InjectService(GRPC)
    private ArtistService artistService;

    @Test
    @DisplayName("Can create artist")
    void canCreateArtistTest() {

        // Data
        var artist = DataGenerator.generateArtist();

        // Steps
        var result = artistService.add(artist);

        // Assertions
        assertNotNull(result.getId());

    }

    @Artist
    @Test
    @DisplayName("Can not create artist with exists name")
    void canNotCreateArtistWithExistsNameTest(ArtistDTO artist) {

        // Steps
        var result = assertThrows(ArtistAlreadyExistsException.class, () -> artistService.add(artist));

        // Assertions
        assertEquals("Artist with name = [%s] already exists".formatted(artist.getName()), result.getMessage());

    }

    @Artist
    @Test
    @DisplayName("Can get artist by id")
    void canGetArtistByIdTest(ArtistDTO artist) {

        // Steps
        var result = artistService.findById(artist.getId()).orElse(null);

        // Assertions
        assertEquals(artist, result);

    }

    @Test
    @DisplayName("Returns Optional.empty() if search artist by unknown id")
    void canGetEmptyArtistByUnknownIdTest() {

        // Steps
        var result = artistService.findById(UUID.randomUUID());

        // Assertions
        assertTrue(result.isEmpty());

    }

    @Artist
    @Test
    @DisplayName("Can get artist by id")
    void canGetArtistByName(ArtistDTO artist) {

        // Steps
        var result = artistService.findByName(artist.getName()).orElse(null);

        // Assertions
        assertEquals(artist, result);

    }

    @Test
    @DisplayName("Returns Optional.empty() if search artist by unknown name")
    void canGetEmptyArtistByUnknownNameTest() {

        // Steps
        var result = artistService.findByName(new Faker().detectiveConan().characters());

        // Assertions
        assertTrue(result.isEmpty());

    }

    @Artists(count = 3)
    @Test
    @DisplayName("Can get all artist")
    void canGetAllArtistsTest(
            List<ArtistDTO> artists
    ) {

        // Steps
        var result = artistService.findAll();

        // Assertions
        assertThat(result,
                hasItems(artists.stream()
                        .map(artist -> allOf(
                                hasProperty("id", is(artist.getId())),
                                hasProperty("name", is(artist.getName())),
                                hasProperty("biography", is(artist.getBiography()))
                        ))
                        .toArray(Matcher[]::new)
                ));

    }

    @Artist
    @Test
    @DisplayName("Can update artist")
    void canUpdateArtistTest(ArtistDTO oldArtist) {

        // Data
        var newArtist = DataGenerator.generateArtist()
                .setId(oldArtist.getId());

        // Steps
        var result = artistService.update(newArtist);

        // Assertions
        assertThat(newArtist, Matchers.equalTo(result));

    }

    @Artists(count = 2)
    @Test
    @DisplayName("Can not update artist name to exist")
    void canNotUpdateArtistNameToExistTest(List<ArtistDTO> artists) {

        // Data
        var artist = artists.getFirst()
                .setName(artists.getLast().getName());

        // Steps & Assertions
        var result = assertThrows(ArtistAlreadyExistsException.class, () -> artistService.update(artist));

        // Assertions
        assertThat(result.getMessage(), containsString("[%s] already exists".formatted(artist.getName())));

    }

    @Artist
    @Test
    @DisplayName("Can delete artist")
    void canDeleteArtistTest(ArtistDTO artist) {

        // Steps
        artistService.delete(artist.getId());

        // Assertions
        assertTrue(artistService.findById(artist.getId()).isEmpty());

    }

}
