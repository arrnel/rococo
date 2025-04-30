package org.rococo.gateway.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.rococo.gateway.client.ArtistsGrpcClient;
import org.rococo.gateway.ex.ArtistAlreadyExistsException;
import org.rococo.gateway.ex.ArtistNotFoundException;
import org.rococo.gateway.model.artists.AddArtistRequestDTO;
import org.rococo.gateway.model.artists.ArtistDTO;
import org.rococo.gateway.model.artists.UpdateArtistRequestDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.anyBoolean;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("ArtistsController: Integration tests")
class ArtistsControllerIT {

    private static final String NEW_IMAGE = "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAQAAAC1HAwCAAAAC0lEQVR42mNk+A8AAQUBAScY42YAAAAASUVORK5CYII=";
    private static final String UPDATED_IMAGE = "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAYAAAAfFcSJAAAADUlEQVR42mNMefj/PwAHOgNF1x8QkwAAAABJRU5ErkJggg==";

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
    private ArtistDTO updatedArtist;
    private AddArtistRequestDTO addArtistDTO;
    private UpdateArtistRequestDTO updateArtistDTO;
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

        addArtistDTO = AddArtistRequestDTO.builder()
                .name("John Doe")
                .biography("Biography of John Doe")
                .photo(NEW_IMAGE)
                .build();

        updatedArtist = ArtistDTO.builder()
                .id(artistId)
                .name("Vincent Van Gogh")
                .biography("Biography of Vincent Van Gogh")
                .photo(UPDATED_IMAGE)
                .build();

        updateArtistDTO = UpdateArtistRequestDTO.builder()
                .id(artistId)
                .name("Vincent Van Gogh")
                .biography("Biography of Vincent Van Gogh")
                .photo(NEW_IMAGE)
                .build();

    }

    @Test
    @DisplayName("Add: returns created artist when request is valid")
    void add_ValidRequest_ReturnsCreatedArtist() throws Exception {

        // Stub
        when(artistsClient.add(addArtistDTO))
                .thenReturn(artist);

        // Steps
        mockMvc.perform(MockMvcRequestBuilders.post("/api/artist")
                        .with(jwt().jwt(builder -> builder.claim("roles", "ROLE_USER")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(addArtistDTO)))
                .andDo(print())

                // Assertions
                .andExpectAll(
                        status().isCreated(),
                        content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON),
                        content().json(om.writeValueAsString(artist))
                );

    }

    @Disabled("Invalid status code if not use jwt builder")
    @Test
    @DisplayName("Add: throws UNAUTHORIZED when request doesn't have token")
    void add_ThrowsUnauthorized_IfSendRequestWithoutToken() throws Exception {

        // Steps
        mockMvc.perform(MockMvcRequestBuilders.post("/api/artist")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(addArtistDTO)))
                .andDo(print())

                // Assertions
                .andExpect(status().isUnauthorized());

    }

    @Test
    @DisplayName("Add: throws BAD_REQUEST when request has validation errors")
    void add_ThrowsBadRequest_IfRequestHasValidationErrors() throws Exception {

        // Data
        final var requestDTO = AddArtistRequestDTO.builder()
                .name("Jo")
                .biography("B")
                .photo("image")
                .build();

        // Steps
        mockMvc.perform(MockMvcRequestBuilders.post("/api/artist")
                        .with(jwt().jwt(builder -> builder.claim("roles", "ROLE_USER")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(requestDTO)))
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
                                    "message": "Bad request. Multiple validation errors",
                                    "errors": [
                                      {
                                        "domain": "/api/artist",
                                        "reason": "photo.Image",
                                        "message": "[Image] Image base64 has invalid regex pattern"
                                      },
                                      {
                                        "domain": "/api/artist",
                                        "reason": "name",
                                        "message": "[Size] Artist name should have length between [3; 255]"
                                      },
                                      {
                                        "domain": "/api/artist",
                                        "reason": "biography",
                                        "message": "[Size] Artist biography should have length between [10; 2000]"
                                      }
                                    ]
                                  }
                                }""".formatted(apiVersion))
                );

    }

    @Test
    @DisplayName("Add: throws CONFLICT when artist already exists")
    void add_ThrowsConflict_IfArtistNameAlreadyTaken() throws Exception {

        // Stubs
        when(artistsClient.add(addArtistDTO))
                .thenThrow(new ArtistAlreadyExistsException(addArtistDTO.name()));

        // Steps
        mockMvc.perform(MockMvcRequestBuilders.post("/api/artist")
                        .with(jwt().jwt(builder -> builder.claim("roles", "ROLE_USER")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(addArtistDTO)))
                .andDo(print())

                // Assertions
                .andExpectAll(
                        status().isConflict(),
                        content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON),
                        content().json("""
                                {
                                   "apiVersion": "%1$s",
                                   "error": {
                                     "code": "409 CONFLICT",
                                     "message": "Artist name = [%2$s] is already taken",
                                     "errors": [
                                       {
                                         "domain": "/api/artist",
                                         "reason": "Artist already exists",
                                         "message": "Artist name = [%2$s] is already taken"
                                       }
                                     ]
                                   }
                                 }""".formatted(apiVersion, addArtistDTO.name()))
                );

    }

    @Test
    @DisplayName("FindAll: returns paginated artists")
    void findAll_ValidRequest_ReturnsPaginatedArtists() throws Exception {

        // Data
        final var artist2 = ArtistDTO.builder()
                .id(UUID.randomUUID())
                .name("Vincent Van Gogh")
                .biography("Biography of Vincent Van Gogh")
                .photo(UPDATED_IMAGE)
                .build();

        final Page<ArtistDTO> page = new PageImpl<>(
                List.of(artist, artist2),
                PageRequest.of(0, 20, Sort.by("name").ascending()),
                2
        );

        // Stubs
        when(artistsClient.findAll(any(), anyBoolean(), any()))
                .thenReturn(page);

        // Steps
        mockMvc.perform(get(ARTIST_URL)
                        .param("size", "20")
                        .param("sort", "name,asc"))
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
                                      "name": "%s",
                                      "biography": "%s",
                                      "photo": "%s"
                                    },
                                    {
                                      "id": "%s",
                                      "name": "%s",
                                      "biography": "%s",
                                      "photo": "%s"
                                    }
                                  ],
                                  "page": {
                                    "size": 20,
                                    "number": 0,
                                    "totalElements": 2,
                                    "totalPages": 1
                                  }
                                }""".formatted(artistId, artist.getName(), artist.getBiography(), NEW_IMAGE, artist2.getId(), artist2.getName(), artist2.getBiography(), UPDATED_IMAGE))
                );

    }

    @Test
    @DisplayName("FindAll: throws BAD_REQUEST when request has validation errors")
    void findAll_ThrowsBadRequest_IfRequestHasValidationErrors() throws Exception {

        // Steps
        mockMvc.perform(get(ARTIST_URL)
                        .param("name", "as")
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
                                    "message": "Bad request. Multiple validation errors",
                                    "errors": [
                                      {
                                        "domain": "/api/artist",
                                        "reason": "name",
                                        "message": "[Size] Param [name] must have length [3; 255]"
                                      },
                                      {
                                        "domain": "/api/artist",
                                        "reason": "pageable.Columns",
                                        "message": "[Columns] Request contains invalid columns. Available columns: [id, name, createdDate]"
                                      }
                                    ]
                                  }
                                }""".formatted(apiVersion))
                );

    }

    @Test
    @DisplayName("Update: successfully updates existing artist")
    void update_ValidRequest_Success() throws Exception {

        // Stubs
        when(artistsClient.update(updateArtistDTO))
                .thenReturn(updatedArtist);

        // Steps
        mockMvc.perform(patch(ARTIST_URL)
                        .with(jwt().jwt(builder -> builder.claim("roles", "ROLE_USER")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(updateArtistDTO)))
                .andDo(print())

                // Assertions
                .andExpectAll(
                        status().isOk(),
                        content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON),
                        content().json(om.writeValueAsString(updatedArtist))
                );

    }

    @Test
    @DisplayName("Update: throws NOT_FOUND when artist doesn't exist")
    void update_ThrowsNotFound_IfArtistNotExists() throws Exception {

        // Data
        final var updateDTO = UpdateArtistRequestDTO.builder()
                .id(artistId)
                .name("Updated Artist")
                .biography("Updated Biography")
                .photo(UPDATED_IMAGE)
                .build();

        // Stubs
        when(artistsClient.update(updateDTO))
                .thenThrow(new ArtistNotFoundException(artistId));

        // Steps
        mockMvc.perform(patch(ARTIST_URL)
                        .with(jwt().jwt(builder -> builder.claim("roles", "ROLE_USER")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(updateDTO)))
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
                                         "domain": "/api/artist",
                                         "reason": "Artist not found",
                                         "message": "Artist with id = [%2$s] not found"
                                       }
                                     ]
                                   }
                                 }""".formatted(apiVersion, artistId.toString()))
                );

    }

    @Disabled("Invalid status code if not use jwt builder")
    @Test
    @DisplayName("Update: throws UNAUTHORIZED when request doesn't have token")
    void update_ThrowsUnauthorized_IfSendRequestWithoutToken() throws Exception {

        // Steps
        mockMvc.perform(patch(ARTIST_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(updatedArtist)))

                // Assertions
                .andExpect(status().isUnauthorized());

    }

    @Test
    @DisplayName("Update: throws BAD_REQUEST when request has validation errors")
    void update_ThrowsBadRequest_IfRequestHasValidationErrors() throws Exception {

        // Data
        final var requestDTO = UpdateArtistRequestDTO.builder()
                .id(artistId)
                .name("Jo")
                .biography("B")
                .photo("image")
                .build();

        // Steps
        mockMvc.perform(MockMvcRequestBuilders.post("/api/artist")
                        .with(jwt().jwt(builder -> builder.claim("roles", "ROLE_USER")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(requestDTO)))
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
                                    "message": "Bad request. Multiple validation errors",
                                    "errors": [
                                      {
                                        "domain": "/api/artist",
                                        "reason": "photo.Image",
                                        "message": "[Image] Image base64 has invalid regex pattern"
                                      },
                                      {
                                        "domain": "/api/artist",
                                        "reason": "name",
                                        "message": "[Size] Artist name should have length between [3; 255]"
                                      },
                                      {
                                        "domain": "/api/artist",
                                        "reason": "biography",
                                        "message": "[Size] Artist biography should have length between [10; 2000]"
                                      }
                                    ]
                                  }
                                }""".formatted(apiVersion))
                );

    }

    @Test
    @DisplayName("Update: throws CONFLICT when artist already exists")
    void update_ThrowsConflict_IfArtistNameAlreadyTaken() throws Exception {

        // Stubs
        when(artistsClient.update(updateArtistDTO))
                .thenThrow(new ArtistAlreadyExistsException(updateArtistDTO.name()));

        // Steps
        mockMvc.perform(MockMvcRequestBuilders.patch("/api/artist")
                        .with(jwt().jwt(builder -> builder.claim("roles", "ROLE_USER")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(updateArtistDTO)))
                .andDo(print())

                // Assertions
                .andExpectAll(
                        status().isConflict(),
                        content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON),
                        content().json("""
                                {
                                  "apiVersion": "%s",
                                  "error": {
                                    "code": "409 CONFLICT",
                                    "message": "Artist name = [%2$s] is already taken",
                                    "errors": [
                                      {
                                        "domain": "/api/artist",
                                        "reason": "Artist already exists",
                                        "message": "Artist name = [%2$s] is already taken"
                                      }
                                    ]
                                  }
                                }""".formatted(apiVersion, updateArtistDTO.name()))
                );

    }

}
