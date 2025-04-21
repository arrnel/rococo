package org.rococo.gateway.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.rococo.gateway.client.MuseumsGrpcClient;
import org.rococo.gateway.model.countries.CountryDTO;
import org.rococo.gateway.model.countries.LocationResponseDTO;
import org.rococo.gateway.model.museums.MuseumDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;
import java.util.UUID;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("MuseumController: Integration tests")
class MuseumControllerIT {

    private static final String NEW_IMAGE = "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAQAAAC1HAwCAAAAC0lEQVR42mNk+A8AAQUBAScY42YAAAAASUVORK5CYII=";

    private static final String MUSEUM_URL = "/api/museum";
    private static final ObjectMapper om = new ObjectMapper();

    @Value("${app.api.version}")
    private String apiVersion;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private JwtDecoder jwtDecoder;

    @MockBean
    private MuseumsGrpcClient museumsClient;

    private MuseumDTO museum;
    private UUID museumId;

    @BeforeEach
    void setUp() {

        museumId = UUID.randomUUID();
        museum = MuseumDTO.builder()
                .id(museumId)
                .title("Orsay Museum")
                .description("Description of Orsay Museum")
                .location(
                        LocationResponseDTO.builder()
                                .city("Paris")
                                .country(CountryDTO.builder()
                                        .id(UUID.randomUUID())
                                        .name("France")
                                        .code("FR")
                                        .build())
                                .build())
                .photo(NEW_IMAGE)
                .build();

    }

    @Test
    @DisplayName("FindById: returns museum when ID exists")
    void findById_ExistingId_ReturnsMuseum() throws Exception {

        // Stubs
        when(museumsClient.findById(museumId))
                .thenReturn(Optional.of(museum));

        // Steps
        mockMvc.perform(get(MUSEUM_URL + "/" + museumId))
                .andDo(print())
                // Assertions
                .andExpectAll(
                        status().isOk(),
                        content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON),
                        content().json(om.writeValueAsString(museum))
                );

    }

    @Test
    @DisplayName("FindById: throws NOT_FOUND when ID doesn't exist")
    void findById_ThrowsNotFound_IfMuseumNotFound() throws Exception {

        // Stubs
        when(museumsClient.findById(museumId))
                .thenReturn(Optional.empty());

        // Steps
        mockMvc.perform(get(MUSEUM_URL + "/" + museumId))
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
                                    "message": "Museum with id = [%2$s] not found",
                                    "errors": [
                                      {
                                        "domain": "/api/museum/%2$s",
                                        "reason": "Museum not found",
                                        "message": "Museum with id = [%2$s] not found"
                                      }
                                    ]
                                  }
                                }""".formatted(apiVersion, museumId))
                );

    }

    @Test
    @DisplayName("Delete: successfully deletes existing museum")
    void delete_ExistingMuseum_Success() throws Exception {

        // Stubs
        when(museumsClient.findById(museumId))
                .thenReturn(Optional.of(museum));
        doNothing().when(museumsClient)
                .delete(museumId);

        // Steps
        mockMvc.perform(delete(MUSEUM_URL + "/" + museumId)
                        .with(jwt().jwt(builder -> builder.claim("roles", "ROLE_USER"))))
                .andDo(print())

                // Assertions
                .andExpect(status().isOk());

    }

}
