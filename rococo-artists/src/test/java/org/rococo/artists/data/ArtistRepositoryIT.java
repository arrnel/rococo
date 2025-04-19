package org.rococo.artists.data;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.junit.jupiter.MockitoExtension;
import org.rococo.artists.model.ArtistFilter;
import org.rococo.artists.specs.ArtistSpecs;
import org.rococo.artists.specs.value.PartialTextSpec;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@Sql("/sql/artists.sql")
@Transactional
@DataJpaTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({
        ArtistSpecs.class,
        PartialTextSpec.class
})
@DisplayName("ArtistRepository: Integration tests")
class ArtistRepositoryIT {

    @Autowired
    ArtistRepository artistRepository;

    @Autowired
    ArtistSpecs artistSpecs;

    private final ArtistEntity expectedArtist = ArtistEntity.builder()
            .id(UUID.fromString("5d328c42-989b-47bd-8565-d265c8ea5234"))
            .name("Pablo Picasso")
            .biography("Spanish painter, sculptor, printmaker, ceramicist, and theatre designer")
            .createdDate(LocalDateTime.of(2024, 1, 1, 11, 15))
            .build();

    @Test
    @DisplayName("FindById: returns artist")
    void findById_ReturnsArtist() {

        // Steps
        var result = artistRepository.findById(expectedArtist.getId()).orElse(new ArtistEntity());

        // Assertions
        assertAll(
                () -> assertEquals(expectedArtist.getId(), result.getId()),
                () -> assertEquals(expectedArtist.getName(), result.getName()),
                () -> assertEquals(expectedArtist.getBiography(), result.getBiography())
        );

    }

    @Test
    @DisplayName("FindById: returns empty if artist not found by id")
    void findById_ReturnsEmpty() {

        // Steps
        var result = artistRepository.findById(UUID.randomUUID());

        // Assertions
        assertTrue(result.isEmpty());

    }

    @Test
    @DisplayName("FindByName: returns artist")
    void findByName_ReturnsArtist() {

        // Steps
        var result = artistRepository.findByName(expectedArtist.getName()).orElse(new ArtistEntity());

        // Assertions
        assertAll(
                () -> assertEquals(expectedArtist.getId(), result.getId()),
                () -> assertEquals(expectedArtist.getName(), result.getName()),
                () -> assertEquals(expectedArtist.getBiography(), result.getBiography())
        );

    }

    @Test
    @DisplayName("FindByName: returns empty if artist not found by name")
    void findByName_ReturnsEmpty() {

        // Steps
        var result = artistRepository.findByName("Black square");

        // Assertions
        assertTrue(result.isEmpty());

    }

    @ParameterizedTest(name = "Case: {0}")
    @MethodSource("findAllArtists_ArgumentsProvider")
    @DisplayName("FindAllByName: returns artists")
    void findAllByName_ReturnsFoundedArtists(String caseName,
                                             ArtistFilter filter,
                                             List<String> expectedArtistsNames
    ) {

        // Data
        final var specs = artistSpecs.findByCriteria(filter);
        final var pageable = PageRequest.of(0, 10);

        // Steps
        var result = artistRepository.findAll(specs, pageable);
        var actualArtistNames = result.getContent().stream()
                .map(ArtistEntity::getName)
                .toList();

        // Assertions
        assertAll(
                () -> assertEquals(expectedArtistsNames.size(), actualArtistNames.size()),
                () -> assertTrue(actualArtistNames.containsAll(expectedArtistsNames))
        );

    }

    static Stream<Arguments> findAllArtists_ArgumentsProvider() {
        return Stream.of(
                Arguments.of("empty search",
                        ArtistFilter.builder().build(),
                        List.of("Vincent Willem van Gogh", "Claude Monet", "Vanessa Cooper", "Pablo Picasso", "Ryan Sullivan")
                ),
                Arguments.of("by query",
                        ArtistFilter.builder()
                                .query("vAn")
                                .build(),
                        List.of("Vincent Willem van Gogh", "Vanessa Cooper", "Ryan Sullivan")
                ));
    }

}
