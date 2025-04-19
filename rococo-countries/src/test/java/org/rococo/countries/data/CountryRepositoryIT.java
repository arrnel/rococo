package org.rococo.countries.data;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@Transactional
@DataJpaTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DisplayName("CountryRepository: Integration tests")
class CountryRepositoryIT {

    private static final CountryCode REMOVE_COUNTRY_CODE = CountryCode.DK;

    @Autowired
    private CountryRepository countryRepository;

    private CountryEntity expectedCountry;

    @BeforeEach
    void setUp() {

        countryRepository.findByCode(CountryCode.JP)
                .ifPresentOrElse(
                        country -> expectedCountry = country,
                        () -> {
                            throw new RuntimeException("Expected country not found");
                        });

        countryRepository.findByCode(REMOVE_COUNTRY_CODE)
                .ifPresent(countryRepository::delete);

    }

    @Test
    @DisplayName("FindById: returns country")
    void findById_ReturnsCountry() {

        // Steps
        var result = countryRepository.findById(expectedCountry.getId()).orElse(new CountryEntity());

        // Assertions
        assertAll(
                () -> assertEquals(expectedCountry.getId(), result.getId()),
                () -> assertEquals(expectedCountry.getName(), result.getName()),
                () -> assertEquals(expectedCountry.getCode(), result.getCode())
        );

    }

    @Test
    @DisplayName("FindById: returns empty when country not found by id")
    void findById_ReturnsEmpty() {

        // Steps
        var result = countryRepository.findById(UUID.randomUUID());

        // Assertions
        assertTrue(result.isEmpty());

    }

    @Test
    @DisplayName("FindByCode: returns country")
    void findByCode_ReturnsCountry() {

        // Steps
        var result = countryRepository.findByCode(expectedCountry.getCode()).orElse(new CountryEntity());

        // Assertions
        assertAll(
                () -> assertEquals(expectedCountry.getId(), result.getId()),
                () -> assertEquals(expectedCountry.getName(), result.getName()),
                () -> assertEquals(expectedCountry.getCode(), result.getCode())
        );

    }

    @Test
    @DisplayName("FindByCode: returns empty when country not found by code")
    void findByCode_ReturnsEmpty() {

        // Steps
        var result = countryRepository.findByCode(REMOVE_COUNTRY_CODE);

        // Assertions
        assertTrue(result.isEmpty());

    }

}
