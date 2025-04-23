package org.rococo.gateway.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.rococo.gateway.client.PaintingsGrpcClient;
import org.rococo.gateway.model.artists.ArtistDTO;
import org.rococo.gateway.model.countries.CountryDTO;
import org.rococo.gateway.model.countries.LocationResponseDTO;
import org.rococo.gateway.model.museums.MuseumDTO;
import org.rococo.gateway.model.paintings.PaintingDTO;
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
@DisplayName("PaintingController: Integration tests")
class PaintingControllerIT {

    private static final String NEW_IMAGE = "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAQAAAC1HAwCAAAAC0lEQVR42mNk+A8AAQUBAScY42YAAAAASUVORK5CYII=";

    private static final String PAINTING_URL = "/api/painting";
    private static final ObjectMapper om = new ObjectMapper();

    @Value("${app.api.version}")
    private String apiVersion;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private JwtDecoder jwtDecoder;

    @MockBean
    private PaintingsGrpcClient paintingsClient;

    private PaintingDTO painting;
    private UUID paintingId;
    private UUID artistId;
    private UUID museumId;
    private UUID countryId;

    @BeforeEach
    void setUp() {

        paintingId = UUID.randomUUID();
        artistId = UUID.randomUUID();
        museumId = UUID.randomUUID();
        countryId = UUID.randomUUID();

        painting = PaintingDTO.builder()
                .id(paintingId)
                .title("Impression, Sunrise")
                .description("Description of Impression, Sunrise")
                .artist(ArtistDTO.builder()
                        .id(artistId)
                        .name("Claude Monet")
                        .biography("Biography of Claude Monet")
                        .build())
                .museum(MuseumDTO.builder()
                        .id(museumId)
                        .title("Orsay Museum")
                        .description("Description of Orsay Museum")
                        .location(LocationResponseDTO.builder()
                                .city("Paris")
                                .country(CountryDTO.builder()
                                        .id(countryId)
                                        .name("France")
                                        .code("FR")
                                        .build())
                                .build())
                        .build())
                .photo(NEW_IMAGE)
                .build();

    }

    @Test
    @DisplayName("FindById: returns painting when ID exists")
    void findById_ExistingId_ReturnsPainting() throws Exception {

        // Stubs
        when(paintingsClient.findById(paintingId))
                .thenReturn(Optional.of(painting));

        // Steps
        mockMvc.perform(get(PAINTING_URL + "/" + paintingId))
                .andDo(print())
                // Assertions
                .andExpectAll(
                        status().isOk(),
                        content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON),
                        content().json(om.writeValueAsString(painting))
                );

    }

    @Test
    @DisplayName("FindById: throws NOT_FOUND when ID doesn't exist")
    void findById_ThrowsNotFound_IfPaintingNotFound() throws Exception {

        // Stubs
        when(paintingsClient.findById(paintingId))
                .thenReturn(Optional.empty());

        // Steps
        mockMvc.perform(get(PAINTING_URL + "/" + paintingId))
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
                                    "message": "Painting with id = [%2$s] not found",
                                    "errors": [
                                      {
                                        "domain": "/api/painting/%2$s",
                                        "reason": "Painting not found",
                                        "message": "Painting with id = [%2$s] not found"
                                      }
                                    ]
                                  }
                                }""".formatted(apiVersion, paintingId))
                );

    }

    @Test
    @DisplayName("Delete: successfully deletes existing painting")
    void delete_ExistingPainting_Success() throws Exception {

        // Stubs
        when(paintingsClient.findById(paintingId))
                .thenReturn(Optional.of(painting));
        doNothing().when(paintingsClient)
                .delete(paintingId);

        // Steps
        mockMvc.perform(delete(PAINTING_URL + "/" + paintingId)
                        .with(jwt().jwt(builder -> builder.claim("roles", "ROLE_USER"))))
                .andDo(print())

                // Assertions
                .andExpect(status().isOk());

    }

}
