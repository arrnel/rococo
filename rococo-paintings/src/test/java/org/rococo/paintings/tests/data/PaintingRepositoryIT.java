package org.rococo.paintings.tests.data;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.junit.jupiter.MockitoExtension;
import org.rococo.paintings.data.PaintingEntity;
import org.rococo.paintings.data.PaintingRepository;
import org.rococo.paintings.model.PaintingFilter;
import org.rococo.paintings.specs.PaintingSpecs;
import org.rococo.paintings.specs.value.EqualUuidSpec;
import org.rococo.paintings.specs.value.PartialTextSpec;
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

@Sql("/sql/paintings.sql")
@Transactional
@DataJpaTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({
        PaintingSpecs.class,
        PartialTextSpec.class,
        EqualUuidSpec.class
})
@DisplayName("PaintingRepository: Integration tests")
class PaintingRepositoryIT {

    @Autowired
    PaintingRepository paintingRepository;

    @Autowired
    PaintingSpecs paintingSpecs;

    private final PaintingEntity expectedPainting = PaintingEntity.builder()
            .id(UUID.fromString("b073bda0-7adb-46bc-8305-7fc88823f56d"))
            .title("Still Life with Pheasant")
            .description("")
            .artistId(UUID.fromString("d6cc349d-0e4c-4b53-99c5-cc80d6772831"))
            .museumId(UUID.fromString("01ff5516-59f0-4fd8-aabd-f1dc100640f4"))
            .createdDate(LocalDateTime.of(2024, 1, 1, 11, 30))
            .build();

    @Test
    @DisplayName("FindById: returns painting")
    void findById_ReturnsPainting() {

        // Steps
        var result = paintingRepository.findById(expectedPainting.getId()).orElse(new PaintingEntity());

        // Assertions
        assertAll(
                () -> assertEquals(expectedPainting.getId(), result.getId()),
                () -> assertEquals(expectedPainting.getTitle(), result.getTitle()),
                () -> assertEquals(expectedPainting.getDescription(), result.getDescription()),
                () -> assertEquals(expectedPainting.getArtistId(), result.getArtistId()),
                () -> assertEquals(expectedPainting.getMuseumId(), result.getMuseumId())
        );

    }

    @Test
    @DisplayName("FindById: returns empty when painting not found by id")
    void findById_ReturnsEmpty() {

        // Steps
        var result = paintingRepository.findById(UUID.randomUUID());

        // Assertions
        assertTrue(result.isEmpty());

    }

    @Test
    @DisplayName("FindByTitle: returns painting")
    void findByTitle_ReturnsPainting() {

        // Steps
        var result = paintingRepository.findByTitle(expectedPainting.getTitle()).orElse(new PaintingEntity());

        // Assertions
        assertAll(
                () -> assertEquals(expectedPainting.getId(), result.getId()),
                () -> assertEquals(expectedPainting.getTitle(), result.getTitle()),
                () -> assertEquals(expectedPainting.getDescription(), result.getDescription()),
                () -> assertEquals(expectedPainting.getArtistId(), result.getArtistId()),
                () -> assertEquals(expectedPainting.getMuseumId(), result.getMuseumId())
        );

    }

    @Test
    @DisplayName("FindByTitle: returnsEmpty when painting not found by title")
    void findByTitle_ReturnsEmpty() {

        // Steps
        var result = paintingRepository.findByTitle("Black square");

        // Assertions
        assertTrue(result.isEmpty());

    }

    @ParameterizedTest(name = "Case: {0}")
    @MethodSource("findAllPaintings_ArgumentsProvider")
    @DisplayName("FindAllByTitle: returns paintings")
    void findAllByTitle_ReturnsFoundedPaintings(String caseName,
                                                PaintingFilter filter,
                                                List<String> expectedPaintingsTitles
    ) {

        // Data
        final var specs = paintingSpecs.findByCriteria(filter);
        final var pageable = PageRequest.of(0, 10);

        // Steps
        var result = paintingRepository.findAll(specs, pageable);
        var actualPaintingTitles = result.getContent().stream()
                .map(PaintingEntity::getTitle)
                .toList();

        // Assertions
        assertAll(
                () -> assertEquals(expectedPaintingsTitles.size(), actualPaintingTitles.size()),
                () -> assertTrue(actualPaintingTitles.containsAll(expectedPaintingsTitles))
        );

    }

    static Stream<Arguments> findAllPaintings_ArgumentsProvider() {
        return Stream.of(
                Arguments.of("empty search",
                        PaintingFilter.builder().build(),
                        List.of("Corner of a Studio", "Starry Night", "Still Life with Pheasant", "Girl in a Chemise", "Girl with Dog")
                ),
                Arguments.of("by query",
                        PaintingFilter.builder()
                                .query("girl")
                                .build(),
                        List.of("Girl in a Chemise", "Girl with Dog")
                ),
                Arguments.of("by artist id",
                        PaintingFilter.builder()
                                .artistId(UUID.fromString("d6cc349d-0e4c-4b53-99c5-cc80d6772831"))
                                .build(),
                        List.of("Corner of a Studio", "Still Life with Pheasant", "Girl with Dog")
                ),
                Arguments.of("by query and artist id",
                        PaintingFilter.builder()
                                .query("girl")
                                .artistId(UUID.fromString("aa657e50-ab00-496a-a125-a7a8b090c141"))
                                .build(),
                        List.of("Girl in a Chemise")
                ));
    }

}
