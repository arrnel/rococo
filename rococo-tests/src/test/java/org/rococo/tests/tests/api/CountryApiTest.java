package org.rococo.tests.tests.api;

import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.rococo.tests.enums.CountryCode;
import org.rococo.tests.enums.ServiceType;
import org.rococo.tests.jupiter.annotation.Country;
import org.rococo.tests.jupiter.annotation.meta.ApiTest;
import org.rococo.tests.jupiter.annotation.meta.InjectService;
import org.rococo.tests.model.CountryDTO;
import org.rococo.tests.service.CountryService;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@ApiTest
@Feature("API")
@Story("[API] Countries tests")
@DisplayName("[API] Countries tests")
@ParametersAreNonnullByDefault
class CountryApiTest {

    @InjectService(ServiceType.API)
    private CountryService countryService;

    @Country
    @Test
    @DisplayName("Can get country by id")
    void canGetCountryByIdTest(CountryDTO country) {

        // Steps
        var result = countryService.findById(country.getId()).orElse(new CountryDTO());

        // Assertions
        assertAll(
                () -> assertEquals(country.getName(), result.getName()),
                () -> assertEquals(country.getCode(), result.getCode())
        );

    }

    @Test
    @DisplayName("Returns Optional.empty() if search country by unknown id")
    void canGetEmptyCountryByUnknownIdTest() {

        // Steps
        var result = countryService.findById(UUID.randomUUID());

        // Assertions
        assertTrue(result.isEmpty(), "Check country not found by unknown id");

    }

    @Country
    @Test
    @DisplayName("Can get country by code")
    void canGetCountryByCodeTest(CountryDTO country) {

        // Steps
        var result = countryService.findByCode(country.getCode()).orElse(new CountryDTO());

        // Assertions
        assertAll(
                () -> assertEquals(country.getId(), result.getId()),
                () -> assertEquals(country.getName(), result.getName())
        );

    }

    @Test
    @DisplayName("Returns Optional.empty() if search country by unknown country code")
    void canGetEmptyCountryByUnknownCountryCodeTest() {

        // Steps
        var result = assertThrows(IllegalArgumentException.class, () -> countryService.findByCode(CountryCode.EMPTY));

        // Assertions
        assertEquals("Country code cannot be empty", result.getMessage());

    }

    @Test
    @DisplayName("Throws exception if CountryCode = EMPTY")
    void shouldThrowExceptionIfCountryCodeIsEmptyTest() {

        // Steps
        var result = assertThrows(IllegalArgumentException.class, () -> countryService.findByCode(CountryCode.EMPTY));

        // Assertions
        assertEquals("Country code cannot be empty", result.getMessage());

    }

    @Test
    @DisplayName("Can get all countries")
    void canGetAllCountriesTest() {

        // Steps
        var result = countryService.findAll();

        // Assertions
        assertEquals(251, result.size(), "Check countries service has expected count of countries");

    }

}
