package org.rococo.museums.data;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.junit.jupiter.MockitoExtension;
import org.rococo.museums.model.MuseumFilter;
import org.rococo.museums.specs.MuseumSpecs;
import org.rococo.museums.specs.value.EqualUuidSpec;
import org.rococo.museums.specs.value.PartialTextSpec;
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

@Sql("/sql/museums.sql")
@Transactional
@DataJpaTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({
        MuseumSpecs.class,
        PartialTextSpec.class,
        EqualUuidSpec.class
})
@DisplayName("PaintingRepository: Integration tests")
class MuseumRepositoryIT {

    @Autowired
    MuseumRepository museumRepository;

    @Autowired
    MuseumSpecs museumSpecs;

    private final MuseumEntity expectedMuseum = MuseumEntity.builder()
            .id(UUID.fromString("36f0d5e0-a0a0-4095-9547-1cd984ef1867"))
            .title("Orsay Museum")
            .description("Museum in Paris, France, on the Left Bank of the Seine")
            .city("Paris")
            .countryId(UUID.fromString("bcb125cd-159f-4e89-b954-8e6ca25ad973"))
            .createdDate(LocalDateTime.of(2024, 1, 1, 11, 0))
            .build();

    @Test
    @DisplayName("FindById: returns museum")
    void findById_ReturnsMuseum() {

        // Steps
        var result = museumRepository.findById(expectedMuseum.getId()).orElse(new MuseumEntity());

        // Assertions
        assertAll(
                () -> assertEquals(expectedMuseum.getId(), result.getId()),
                () -> assertEquals(expectedMuseum.getTitle(), result.getTitle()),
                () -> assertEquals(expectedMuseum.getDescription(), result.getDescription()),
                () -> assertEquals(expectedMuseum.getCity(), result.getCity()),
                () -> assertEquals(expectedMuseum.getCountryId(), result.getCountryId())
        );

    }

    @Test
    @DisplayName("FindById: returns empty when museum not found by id")
    void findById_ReturnsEmpty() {

        // Steps
        var result = museumRepository.findById(UUID.randomUUID());

        // Assertions
        assertTrue(result.isEmpty());

    }

    @Test
    @DisplayName("FindByTitle: returns museum")
    void findByTitle_ReturnsMuseum() {

        // Steps
        var result = museumRepository.findByTitle(expectedMuseum.getTitle()).orElse(new MuseumEntity());

        // Assertions
        assertAll(
                () -> assertEquals(expectedMuseum.getId(), result.getId()),
                () -> assertEquals(expectedMuseum.getTitle(), result.getTitle()),
                () -> assertEquals(expectedMuseum.getDescription(), result.getDescription()),
                () -> assertEquals(expectedMuseum.getCity(), result.getCity()),
                () -> assertEquals(expectedMuseum.getCountryId(), result.getCountryId())
        );

    }

    @Test
    @DisplayName("FindByTitle: returns empty when museum not found by title")
    void findByTitle_ReturnsEmpty() {

        // Steps
        var result = museumRepository.findByTitle("Black square");

        // Assertions
        assertTrue(result.isEmpty());

    }

    @ParameterizedTest(name = "Case: {0}")
    @MethodSource("findAllMuseums_ArgumentsProvider")
    @DisplayName("FindAllByTitle: returns museums")
    void findAllByTitle_ReturnsFoundedMuseums(String caseName,
                                              MuseumFilter filter,
                                              List<String> expectedMuseumsTitles
    ) {

        // Data
        final var specs = museumSpecs.findByCriteria(filter);
        final var pageable = PageRequest.of(0, 10);

        // Steps
        var result = museumRepository.findAll(specs, pageable);
        var actualMuseumTitles = result.getContent().stream()
                .map(MuseumEntity::getTitle)
                .toList();

        // Assertions
        assertAll(
                () -> assertEquals(expectedMuseumsTitles.size(), actualMuseumTitles.size()),
                () -> assertTrue(actualMuseumTitles.containsAll(expectedMuseumsTitles))
        );

    }

    static Stream<Arguments> findAllMuseums_ArgumentsProvider() {
        return Stream.of(
                Arguments.of("empty search",
                        MuseumFilter.builder().build(),
                        List.of("Orsay Museum", "Museum of Modern Art", "Hermitage Museum", "Louvre", "Metropolitan Museum of Art")
                ),
                Arguments.of("by query",
                        MuseumFilter.builder()
                                .query("museum")
                                .build(),
                        List.of("Orsay Museum", "Museum of Modern Art", "Hermitage Museum", "Metropolitan Museum of Art")
                ),
                Arguments.of("by country id",
                        MuseumFilter.builder()
                                .countryId(UUID.fromString("5298412e-2578-4780-b412-e3b189da86fe"))
                                .build(),
                        List.of("Museum of Modern Art", "Metropolitan Museum of Art")
                ),
                Arguments.of("by query and museum id",
                        MuseumFilter.builder()
                                .query("museum")
                                .countryId(UUID.fromString("bcb125cd-159f-4e89-b954-8e6ca25ad973"))
                                .build(),
                        List.of("Orsay Museum")
                ));
    }

}
