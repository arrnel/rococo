package org.rococo.gateway.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.rococo.gateway.client.ArtistsGrpcClient;
import org.rococo.gateway.model.artists.ArtistDTO;
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
@DisplayName("ArtistController: Integration tests")
class ArtistControllerIT {

    private static final String NEW_IMAGE = "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAQAAAC1HAwCAAAAC0lEQVR42mNk+A8AAQUBAScY42YAAAAASUVORK5CYII=";

    private static final String ARTIST_URL = "/api/artist";
    private static final ObjectMapper om = new ObjectMapper();

    @Value("${app.api.version}")
    private String apiVersion;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private JwtDecoder jwtDecoder;

    @MockBean
    private ArtistsGrpcClient artistsClient;

    private ArtistDTO artist;
    private UUID artistId;

    @BeforeEach
    void setUp() {

        artistId = UUID.randomUUID();
        artist = ArtistDTO.builder()
                .id(artistId)
                .name("John Doe")
                .biography("Biography of John Doe")
                .photo(NEW_IMAGE)
                .build();

    }

    @Test
    @DisplayName("FindById: returns artist when ID exists")
    void findById_ExistingId_ReturnsArtist() throws Exception {

        // Stubs
        when(artistsClient.findById(artistId))
                .thenReturn(Optional.of(artist));

        // Steps
        mockMvc.perform(get(ARTIST_URL + "/" + artistId))
                .andDo(print())
                // Assertions
                .andExpectAll(
                        status().isOk(),
                        content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON),
                        content().json(om.writeValueAsString(artist))
                );

    }

    @Test
    @DisplayName("FindById: throws NOT_FOUND when ID doesn't exist")
    void findById_ThrowsNotFound_IfArtistNotFound() throws Exception {

        // Stubs
        when(artistsClient.findById(artistId))
                .thenReturn(Optional.empty());

        // Steps
        mockMvc.perform(get(ARTIST_URL + "/" + artistId))
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
                                    "message": "Artist with id = [%2$s] not found",
                                    "errors": [
                                      {
                                        "domain": "/api/artist/%2$s",
                                        "reason": "Artist not found",
                                        "message": "Artist with id = [%2$s] not found"
                                      }
                                    ]
                                  }
                                }""".formatted(apiVersion, artistId))
                );

    }

    @Test
    @DisplayName("Delete: successfully deletes existing artist")
    void delete_ExistingArtist_Success() throws Exception {

        // Stubs
        when(artistsClient.findById(artistId))
                .thenReturn(Optional.of(artist));
        doNothing().when(artistsClient)
                .delete(artistId);

        // Steps
        mockMvc.perform(delete(ARTIST_URL + "/" + artistId)
                        .with(jwt().jwt(builder -> builder.claim("roles", "ROLE_USER"))))
                .andDo(print())

                // Assertions
                .andExpect(status().isOk());

    }

}
