package org.rococo.tests.tests.fake.grpc;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import lombok.extern.slf4j.Slf4j;
import net.datafaker.Faker;
import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Isolated;
import org.rococo.tests.ex.MuseumAlreadyExistException;
import org.rococo.tests.jupiter.annotation.Country;
import org.rococo.tests.jupiter.annotation.Museum;
import org.rococo.tests.jupiter.annotation.Museums;
import org.rococo.tests.jupiter.annotation.meta.GrpcTest;
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
import static org.rococo.tests.enums.ServiceType.GRPC;

@Isolated
@GrpcTest
@Feature("FAKE")
@Story("[GRPC] Museums tests")
@DisplayName("[GRPC] Museums tests")
@ParametersAreNonnullByDefault
class MuseumGrpcTest {

    @InjectService(GRPC)
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
    @DisplayName("Can not create museum with exists title")
    void canNotCreateMuseumWithExistsTitleTest(MuseumDTO museum) {

        // Steps
        var result = assertThrows(MuseumAlreadyExistException.class, () -> museumService.add(museum));

        // Assertions
        assertEquals("Museum with title = [%s] already exists".formatted(museum.getTitle()), result.getMessage());

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
                                hasProperty("description", is(museum.getDescription())),
                                hasProperty("location", hasProperty("country", hasProperty("id", is(museum.getLocation().getCountry().getId()))))
                        ))
                        .toArray(Matcher[]::new)
                ));

    }

    @Museum
    @Test
    @DisplayName("Can update museum")
    void canUpdateMuseumTest(MuseumDTO oldMuseum) {

        // Data
        var newMuseum = DataGenerator.generateMuseum()
                .setId(oldMuseum.getId());

        // Steps
        var result = museumService.update(newMuseum);

        // Assertions
        assertThat(newMuseum, Matchers.equalTo(result));

    }

    @Museums(count = 2)
    @Test
    @DisplayName("Can not update museum name to exist")
    void canNotUpdateMuseumTitleToExistTest(List<MuseumDTO> museums) {

        // Data
        var museum = museums.getFirst()
                .setTitle(museums.getLast().getTitle());

        // Steps & Assertions
        var result = assertThrows(MuseumAlreadyExistException.class, () -> museumService.update(museum));

        // Assertions
        assertEquals("Museum with title = [%s] already exists".formatted(museum.getTitle()), result.getMessage());

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

    @Test
    @DisplayName("Can delete all museums and museums images")
    void canDeleteAllMuseumsAndMuseumImagesTest() {

        // Steps
        museumService.clearAll();

        // Assertions
        assertTrue(museumService.findAll().isEmpty());

    }

}
