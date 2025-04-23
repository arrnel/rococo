package org.rococo.gateway.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.rococo.gateway.client.CountriesGrpcClient;
import org.rococo.gateway.ex.CountryNotFoundException;
import org.rococo.gateway.model.countries.CountryDTO;
import org.rococo.gateway.service.ValidationService;
import org.springframework.data.domain.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CountriesController: Module tests")
class CountriesControllerTests {

    @Mock
    private CountriesGrpcClient countriesClient;

    @Mock
    private ValidationService validationService;

    @InjectMocks
    private CountriesController countriesController;

    @Test
    @DisplayName("FindById: returns country when country exists")
    void findById_Success() {

        // Data
        final UUID id = UUID.randomUUID();
        final CountryDTO expectedCountry = CountryDTO.builder()
                .id(id)
                .name("Test Country")
                .code("TC")
                .build();

        // Stubs
        when(countriesClient.findById(id))
                .thenReturn(Optional.of(expectedCountry));

        // Steps
        CountryDTO result = countriesController.findById(id);

        // Assertions
        assertEquals(expectedCountry, result);
        verify(countriesClient).findById(id);

    }

    @Test
    @DisplayName("FindById: throws CountryNotFoundException when country does not exist")
    void findById_ThrowsCountryNotFoundException_WhenCountryNotFound() {

        // Data
        final UUID id = UUID.randomUUID();

        // Stubs
        when(countriesClient.findById(id))
                .thenReturn(Optional.empty());

        // Steps & Assertions
        assertThrows(CountryNotFoundException.class, () -> countriesController.findById(id));
        verify(countriesClient).findById(id);

    }

    @Test
    @DisplayName("FindAll: returns page of countries when valid pageable")
    void findAll_Success() {

        // Data
        final Pageable pageable = PageRequest.of(0, 20, Sort.by("name").ascending());
        final CountryDTO country = CountryDTO.builder()
                .id(UUID.randomUUID())
                .name("Test Country")
                .code("TC")
                .build();
        final Page<CountryDTO> expectedPage = new PageImpl<>(List.of(country), pageable, 1);

        // Stubs
        when(countriesClient.findAll(pageable))
                .thenReturn(expectedPage);
        doNothing().when(validationService)
                .validateObject(any(), eq("CountriesFindAllParamsValidationObject"));

        // Steps
        Page<CountryDTO> result = countriesController.findAll(pageable);

        // Assertions
        assertEquals(expectedPage, result);
        assertEquals(1, result.getContent().size());
        verify(countriesClient).findAll(pageable);
        verify(validationService).validateObject(any(), eq("CountriesFindAllParamsValidationObject"));

    }

    @Test
    @DisplayName("FindAll: returns empty page when no countries found")
    void findAll_ReturnsEmptyPage_WhenNoCountriesFound() {

        // Data
        final Pageable pageable = PageRequest.of(0, 20, Sort.by("name").ascending());
        final Page<CountryDTO> emptyPage = new PageImpl<>(List.of(), pageable, 0);

        // Stubs
        when(countriesClient.findAll(pageable))
                .thenReturn(emptyPage);
        doNothing().when(validationService)
                .validateObject(any(), eq("CountriesFindAllParamsValidationObject"));

        // Steps
        Page<CountryDTO> result = countriesController.findAll(pageable);

        // Assertions
        assertTrue(result.getContent().isEmpty());
        assertEquals(0, result.getTotalElements());
        verify(countriesClient).findAll(pageable);
        verify(validationService).validateObject(any(), eq("CountriesFindAllParamsValidationObject"));

    }

}
