package org.rococo.tests.tests.fake.db;

import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import net.datafaker.Faker;
import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Isolated;
import org.rococo.tests.data.repository.impl.springJdbc.MuseumRepositorySpringJdbc;
import org.rococo.tests.ex.MuseumAlreadyExistsException;
import org.rococo.tests.jupiter.annotation.Country;
import org.rococo.tests.jupiter.annotation.Museum;
import org.rococo.tests.jupiter.annotation.Museums;
import org.rococo.tests.jupiter.annotation.meta.DbTest;
import org.rococo.tests.jupiter.annotation.meta.InjectService;
import org.rococo.tests.model.CountryDTO;
import org.rococo.tests.model.MuseumDTO;
import org.rococo.tests.service.MuseumService;
import org.rococo.tests.util.DataGenerator;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.rococo.tests.enums.ServiceType.DB;

@Isolated
@DbTest
@Feature("FAKE")
@Story("[DB] Museums tests")
@DisplayName("[DB] Museums tests")
@ParametersAreNonnullByDefault
class MuseumDbTest {

    @InjectService(DB)
    private MuseumService museumService;

    @Country
    @Test
    @DisplayName("Can create museum")
    void canCreateMuseumTest(CountryDTO country) {

        // Data
        var museum = DataGenerator.generateMuseum()
                .setCountry(country);

        // Steps
        var result = museumService.add(museum);

        // Assertions
        assertNotNull(result.getId());


    }

    @Museum
    @Test
    @DisplayName("Can not create museum with exists name")
    void canNotCreateMuseumWithExistsNameTest(MuseumDTO museum) {
        // Steps & Assertions
        var result = assertThrows(RuntimeException.class, () -> museumService.add(museum));
        assertInstanceOf(MuseumAlreadyExistsException.class, result.getCause());
    }

    @Museum
    @Test
    @DisplayName("Can get museum by id")
    void canGetMuseumByIdTest(MuseumDTO museum) {

        // Steps
        var result = museumService.findById(museum.getId()).orElse(null);

        // Assertions
        assertEquals(museum, result);

    }

    @Test
    @DisplayName("Returns Optional.empty() if search museum by unknown id")
    void canGetEmptyMuseumByUnknownIdTest() {

        // Steps
        var result = museumService.findById(UUID.randomUUID());

        // Assertions
        assertTrue(result.isEmpty());

    }

    @Museum
    @Test
    @DisplayName("Can get museum by title")
    void canGetMuseumByTitle(MuseumDTO museum) {

        // Steps
        var result = museumService.findByTitle(museum.getTitle()).orElse(null);

        // Assertions
        assertEquals(museum, result);

    }

    @Test
    @DisplayName("Returns Optional.empty() if search museum by unknown title")
    void canGetEmptyMuseumByUnknownTitleTest() {

        // Steps
        var result = museumService.findByTitle(new Faker().detectiveConan().characters());

        // Assertions
        assertTrue(result.isEmpty());

    }

    @Museums(count = 3)
    @Test
    @DisplayName("Can get all museum")
    void canGetAllMuseumsTest(
            List<MuseumDTO> museums
    ) {

        // Steps
        var result = museumService.findAll();

        // Assertions
        assertThat(result,
                hasItems(museums.stream()
                        .map(museum -> allOf(
                                hasProperty("id", is(museum.getId())),
                                hasProperty("title", is(museum.getTitle())),
                                hasProperty("description", is(museum.getDescription()))
                        ))
                        .toArray(Matcher[]::new)
                ));

    }

    @Country
    @Museum
    @Test
    @DisplayName("Can update museum")
    void canUpdateMuseumTest(MuseumDTO oldMuseum, CountryDTO country) {

        // Data
        var newMuseum = DataGenerator.generateMuseum()
                .setId(oldMuseum.getId())
                .setCountry(country);

        // Steps
        var result = museumService.update(newMuseum);

        // Assertions
        assertThat(result, Matchers.allOf(
                hasProperty("id", is(newMuseum.getId())),
                hasProperty("title", is(newMuseum.getTitle())),
                hasProperty("description", is(newMuseum.getDescription())),
                hasProperty("location", hasProperty("city", is(newMuseum.getLocation().getCity()))),
                hasProperty("location", hasProperty("country", hasProperty("id", notNullValue()))),
                hasProperty("location", hasProperty("country", hasProperty("code", is(newMuseum.getLocation().getCountry().getCode())))),
                hasProperty("photo", is(newMuseum.getPhoto()))
        ));

    }

    @Museums(count = 2)
    @Test
    @DisplayName("Can not update museum name to exist")
    void canNotUpdateMuseumNameToExistTest(List<MuseumDTO> museums) {

        // Data
        var museum = museums.getFirst()
                .setTitle(museums.getLast().getTitle());

        // Steps & Assertions
        var result = assertThrows(RuntimeException.class, () -> museumService.update(museum));
        assertInstanceOf(MuseumAlreadyExistsException.class, result.getCause());

    }

    @Museum
    @Test
    @DisplayName("Can delete museum")
    void canDeleteMuseumTest(MuseumDTO museum) {

        // Steps
        museumService.delete(museum.getId());

        // Assertions
        assertTrue(museumService.findById(museum.getId()).isEmpty());

    }

}
