package org.rococo.gateway.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.rococo.gateway.client.CountriesGrpcClient;
import org.rococo.gateway.model.countries.CountryDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.*;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("CountriesController: Integration tests")
class CountriesControllerIT {

    private static final String COUNTRIES_URL = "/api/country";
    private static final ObjectMapper om = new ObjectMapper();

    @Value("${app.api.version}")
    private String apiVersion;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private JwtDecoder jwtDecoder;

    @MockBean
    private CountriesGrpcClient countriesClient;

    private CountryDTO country;

    @BeforeEach
    void setUp() {
        country = CountryDTO.builder()
                .id(UUID.randomUUID())
                .name("Japan")
                .code("JP")
                .build();
    }

    @Test
    @DisplayName("FindById: returns country when ID exists")
    void findById_ExistingId_ReturnsCountry() throws Exception {

        // Stubs
        when(countriesClient.findById(country.id()))
                .thenReturn(Optional.of(country));

        // Steps
        mockMvc.perform(get(COUNTRIES_URL + "/" + country.id()))
                .andDo(print())
                // Assertions
                .andExpectAll(
                        status().isOk(),
                        content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON),
                        content().json(om.writeValueAsString(country))
                );

    }

    @Test
    @DisplayName("FindById: throws NOT_FOUND when ID doesn't exist")
    void findById_ThrowsNotFound_IfCountryNotFound() throws Exception {

        // Stubs
        when(countriesClient.findById(country.id()))
                .thenReturn(Optional.empty());

        // Steps
        mockMvc.perform(get("%s/%s".formatted(COUNTRIES_URL, country.id())))
                .andDo(print())

                // Assertions
                .andExpectAll(
                        status().isNotFound(),
                        content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON),
                        content().json("""
                                {
                                  "apiVersion": "%1$s",
                                  "error": {
                                    "code": "404 NOT_FOUND",
                                    "message": "Country with id = [%2$s] not found",
                                    "errors": [
                                      {
                                        "domain": "/api/country/%2$s",
                                        "reason": "Country not found",
                                        "message": "Country with id = [%2$s] not found"
                                      }
                                    ]
                                  }
                                }""".formatted(apiVersion, country.id()))
                );

        verify(countriesClient).findById(country.id());

    }

    @Test
    @DisplayName("FindByCode: returns country when code exists")
    void findByCode_ExistingId_ReturnsCountry() throws Exception {

        // Stubs
        when(countriesClient.findByCode(country.code()))
                .thenReturn(Optional.of(country));

        // Steps
        mockMvc.perform(get(COUNTRIES_URL + "/code/" + country.code()))
                .andDo(print())
                // Assertions
                .andExpectAll(
                        status().isOk(),
                        content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON),
                        content().json(om.writeValueAsString(country))
                );

    }

    @Test
    @DisplayName("FindById: throws NOT_FOUND when ID doesn't exist")
    void findByCode_ThrowsNotFound_IfCountryNotFound() throws Exception {

        // Data
        final var countryCode = "NF";

        // Stubs
        when(countriesClient.findByCode(countryCode))
                .thenReturn(Optional.empty());

        // Steps
        mockMvc.perform(get("%s/code/%s".formatted(COUNTRIES_URL, countryCode)))
                .andDo(print())

                // Assertions
                .andExpectAll(
                        status().isNotFound(),
                        content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON),
                        content().json("""
                                {
                                   "apiVersion": "%1$s",
                                   "error": {
                                     "code": "404 NOT_FOUND",
                                     "message": "Country with code = [%2$s] not found",
                                     "errors": [
                                       {
                                         "domain": "/api/country/code/%2$s",
                                         "reason": "Country not found",
                                         "message": "Country with code = [%2$s] not found"
                                       }
                                     ]
                                   }
                                 }""".formatted(apiVersion, countryCode))
                );

    }

    @Test
    @DisplayName("FindById: returns paginated countries")
    void findAll_ReturnsPaginatedCountries() throws Exception {

        // Data
        final var country2 = CountryDTO.builder()
                .id(UUID.randomUUID())
                .name("France")
                .code("FR")
                .build();

        final Page<CountryDTO> page = new PageImpl<>(
                List.of(country, country2),
                PageRequest.of(0, 20, Sort.by("name").ascending()),
                2
        );

        // Stubs
        when(countriesClient.findAll(any(Pageable.class)))
                .thenReturn(page);

        // Steps
        mockMvc.perform(get(COUNTRIES_URL))
                .andDo(print())

                // Assertions
                .andExpectAll(
                        status().isOk(),
                        content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON),
                        content().json("""
                                {
                                   "content": [
                                     {
                                       "id": "%s",
                                       "name": "Japan",
                                       "code": "JP"
                                     },
                                     {
                                       "id": "%s",
                                       "name": "France",
                                       "code": "FR"
                                     }
                                   ],
                                   "page": {
                                     "size": 20,
                                     "number": 0,
                                     "totalElements": 2,
                                     "totalPages": 1
                                   }
                                 }""".formatted(country.id(), country2.id()))
                );

    }

    @Test
    @DisplayName("FindAll: throws BAD_REQUEST when request has validation errors")
    void findAll_ThrowsBadRequest_IfRequestHasValidationErrors() throws Exception {

        // Steps
        mockMvc.perform(get(COUNTRIES_URL)
                        .param("size", "20")
                        .param("sort", "rd,asc"))
                .andDo(print())

                // Assertions
                .andExpectAll(
                        status().isBadRequest(),
                        content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON),
                        content().json("""
                                {
                                   "apiVersion": "%s",
                                   "error": {
                                     "code": "400 BAD_REQUEST",
                                     "message": "Bad request",
                                     "errors": [
                                       {
                                         "domain": "/api/country",
                                         "reason": "pageable.Columns",
                                         "message": "[Columns] Request contains invalid columns. Available columns: [id, name, code]"
                                       }
                                     ]
                                   }
                                 }""".formatted(apiVersion))
                );

    }

}
