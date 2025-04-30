package org.rococo.gateway.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.rococo.gateway.client.PaintingsGrpcClient;
import org.rococo.gateway.ex.ArtistNotFoundException;
import org.rococo.gateway.ex.MuseumNotFoundException;
import org.rococo.gateway.ex.PaintingAlreadyExistsException;
import org.rococo.gateway.ex.PaintingNotFoundException;
import org.rococo.gateway.model.artists.ArtistDTO;
import org.rococo.gateway.model.artists.ArtistIdDTO;
import org.rococo.gateway.model.countries.CountryDTO;
import org.rococo.gateway.model.countries.LocationResponseDTO;
import org.rococo.gateway.model.museums.MuseumDTO;
import org.rococo.gateway.model.museums.MuseumIdDTO;
import org.rococo.gateway.model.paintings.AddPaintingRequestDTO;
import org.rococo.gateway.model.paintings.PaintingDTO;
import org.rococo.gateway.model.paintings.UpdatePaintingRequestDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
@DisplayName("PaintingsController: Integration tests")
class PaintingsControllerIT {

    private static final String NEW_IMAGE = "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAQAAAC1HAwCAAAAC0lEQVR42mNk+A8AAQUBAScY42YAAAAASUVORK5CYII=";
    private static final String UPDATED_IMAGE = "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAYAAAAfFcSJAAAADUlEQVR42mNMefj/PwAHOgNF1x8QkwAAAABJRU5ErkJggg==";

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
    private PaintingDTO updatedPainting;
    private AddPaintingRequestDTO addPaintingDTO;
    private UpdatePaintingRequestDTO updatePaintingDTO;
    private UUID paintingId;
    private UUID artistId;
    private UUID updatedArtistId;
    private UUID museumId;
    private UUID updatedMuseumId;
    private UUID countryId;
    private UUID updatedCountryId;

    @BeforeEach
    void setUp() {

        paintingId = UUID.randomUUID();
        artistId = UUID.randomUUID();
        updatedArtistId = UUID.randomUUID();
        museumId = UUID.randomUUID();
        updatedMuseumId = UUID.randomUUID();
        countryId = UUID.randomUUID();
        updatedCountryId = UUID.randomUUID();

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
                        .title("Orsay Painting")
                        .description("Description of Orsay Painting")
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

        updatedPainting = PaintingDTO.builder()
                .id(paintingId)
                .title("The Starry Night")
                .description("Description of The Starry Night")
                .artist(ArtistDTO.builder()
                        .id(updatedArtistId)
                        .name("Vincent Van Gogh")
                        .biography("Biography of Vincent Van Gogh")
                        .build())
                .museum(MuseumDTO.builder()
                        .id(updatedMuseumId)
                        .title("National Gallery of Art")
                        .description("Description of National Gallery of Art")
                        .location(LocationResponseDTO.builder()
                                .city("New York City")
                                .country(CountryDTO.builder()
                                        .id(updatedCountryId)
                                        .name("United States")
                                        .code("US")
                                        .build())
                                .build())
                        .build())
                .photo(UPDATED_IMAGE)
                .build();

        addPaintingDTO = AddPaintingRequestDTO.builder()
                .title("Impression, Sunrise")
                .description("Description of Impression, Sunrise")
                .artist(new ArtistIdDTO(artistId))
                .museum(new MuseumIdDTO(paintingId))
                .photo(NEW_IMAGE)
                .build();

        updatePaintingDTO = UpdatePaintingRequestDTO.builder()
                .id(paintingId)
                .title("The Starry Night")
                .description("Description of The Starry Night")
                .artist(new ArtistIdDTO(updatedArtistId))
                .museum(new MuseumIdDTO(updatedMuseumId))
                .photo(UPDATED_IMAGE)
                .build();

    }

    @Test
    @DisplayName("Add: returns created painting when request is valid")
    void add_ValidRequest_ReturnsCreatedPainting() throws Exception {

        // Stub
        when(paintingsClient.add(addPaintingDTO))
                .thenReturn(painting);

        // Steps
        mockMvc.perform(MockMvcRequestBuilders.post("/api/painting")
                        .with(jwt().jwt(builder -> builder.claim("roles", "ROLE_USER")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(addPaintingDTO)))
                .andDo(print())

                // Assertions
                .andExpectAll(
                        status().isCreated(),
                        content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON),
                        content().json(om.writeValueAsString(painting))
                );

    }

    @Disabled("Invalid status code if not use jwt builder")
    @Test
    @DisplayName("Add: throws UNAUTHORIZED when request doesn't have token")
    void add_ThrowsUnauthorized_IfSendRequestWithoutToken() throws Exception {

        // Steps
        mockMvc.perform(MockMvcRequestBuilders.post("/api/painting")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(addPaintingDTO)))
                .andDo(print())

                // Assertions
                .andExpect(status().isUnauthorized());

    }

    @Test
    @DisplayName("Add: returns created painting when request is valid")
    void add_ThrowsBadRequest_IfRequestHasValidationErrors() throws Exception {

        // Data
        final var requestDTO = AddPaintingRequestDTO.builder()
                .title("Jo")
                .description("B")
                .artist(new ArtistIdDTO(null))
                .museum(new MuseumIdDTO(null))
                .photo("image")
                .build();

        // Steps
        mockMvc.perform(MockMvcRequestBuilders.post("/api/painting")
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
                                          "domain": "/api/painting",
                                          "reason": "photo.Image",
                                          "message": "[Image] Image base64 has invalid regex pattern"
                                        },
                                        {
                                          "domain": "/api/painting",
                                          "reason": "museum.id",
                                          "message": "[NotNull] Painting country_id must not equal null"
                                        },
                                        {
                                          "domain": "/api/painting",
                                          "reason": "description",
                                          "message": "[Size] Painting description must have length between [10; 2000]"
                                        },
                                        {
                                          "domain": "/api/painting",
                                          "reason": "title",
                                          "message": "[Size] Painting title must have length between [3; 255]"
                                        },
                                        {
                                          "domain": "/api/painting",
                                          "reason": "artist.id",
                                          "message": "[NotNull] Painting artist_id must not equal null"
                                        }
                                      ]
                                    }
                                  }""".formatted(apiVersion))
                );

    }

    @Test
    @DisplayName("Add: throws CONFLICT when painting already exists")
    void add_ThrowsConflict_IfPaintingNameAlreadyTaken() throws Exception {

        // Stubs
        when(paintingsClient.add(addPaintingDTO))
                .thenThrow(new PaintingAlreadyExistsException(addPaintingDTO.title()));

        // Steps
        mockMvc.perform(MockMvcRequestBuilders.post("/api/painting")
                        .with(jwt().jwt(builder -> builder.claim("roles", "ROLE_USER")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(addPaintingDTO)))
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
                                     "message": "Painting title = [%2$s] is already taken",
                                     "errors": [
                                       {
                                         "domain": "/api/painting",
                                         "reason": "Painting already exists",
                                         "message": "Painting title = [%2$s] is already taken"
                                       }
                                     ]
                                   }
                                 }""".formatted(apiVersion, addPaintingDTO.title()))
                );

    }

    @Test
    @DisplayName("Add: throws NOT_FOUND when painting client throws ArtistNotFoundException")
    void add_ThrowsNotFound_IfPaintingClientThrowsArtistNotFoundException() throws Exception {

        // Stubs
        when(paintingsClient.add(addPaintingDTO))
                .thenThrow(new ArtistNotFoundException(artistId));

        // Steps
        mockMvc.perform(MockMvcRequestBuilders.post("/api/painting")
                        .with(jwt().jwt(builder -> builder.claim("roles", "ROLE_USER")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(addPaintingDTO)))
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
                                          "domain": "/api/painting",
                                          "reason": "Artist not found",
                                          "message": "Artist with id = [%2$s] not found"
                                        }
                                      ]
                                    }
                                  }""".formatted(apiVersion, artistId))
                );

    }

    @Test
    @DisplayName("Add: throws NOT_FOUND when painting client throws MuseumNotFoundException")
    void add_ThrowsNotFound_IfPaintingClientThrowsMuseumNotFoundException() throws Exception {

        // Stubs
        when(paintingsClient.add(addPaintingDTO))
                .thenThrow(new MuseumNotFoundException(museumId));

        // Steps
        mockMvc.perform(MockMvcRequestBuilders.post("/api/painting")
                        .with(jwt().jwt(builder -> builder.claim("roles", "ROLE_USER")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(addPaintingDTO)))
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
                                          "domain": "/api/painting",
                                          "reason": "Museum not found",
                                          "message": "Museum with id = [%2$s] not found"
                                        }
                                      ]
                                    }
                                  }""".formatted(apiVersion, museumId))
                );

    }

    @Test
    @DisplayName("FindAll: returns paginated paintings")
    void findAll_ValidRequest_ReturnsPaginatedPaintings() throws Exception {

        final Page<PaintingDTO> page = new PageImpl<>(List.of(painting, updatedPainting), PageRequest.of(0, 20), 2);

        // Stubs
        when(paintingsClient.findAll(any(), any(), anyBoolean(), any(Pageable.class)))
                .thenReturn(page);

        // Steps
        mockMvc.perform(get(PAINTING_URL)
                        .param("size", "20")
                        .param("sort", "title,asc"))
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
                                      "title": "Impression, Sunrise",
                                      "description": "Description of Impression, Sunrise",
                                      "artist": {
                                        "id": "%s",
                                        "name": "Claude Monet",
                                        "biography": "Biography of Claude Monet"
                                      },
                                      "museum": {
                                        "id": "%s",
                                        "title": "Orsay Painting",
                                        "description": "Description of Orsay Painting",
                                        "geo": {
                                          "city": "Paris",
                                          "country": {
                                            "id": "%s",
                                            "name": "France",
                                            "code": "FR"
                                          }
                                        }
                                      },
                                      "content": "%s"
                                    },
                                    {
                                      "id": "%s",
                                      "title": "The Starry Night",
                                      "description": "Description of The Starry Night",
                                      "artist": {
                                        "id": "%s",
                                        "name": "Vincent Van Gogh",
                                        "biography": "Biography of Vincent Van Gogh"
                                      },
                                      "museum": {
                                        "id": "%s",
                                        "title": "National Gallery of Art",
                                        "description": "Description of National Gallery of Art",
                                        "geo": {
                                          "city": "New York City",
                                          "country": {
                                            "id": "%s",
                                            "name": "United States",
                                            "code": "US"
                                          }
                                        }
                                      },
                                      "content": "%s"
                                    }
                                  ],
                                  "page": {
                                    "size": 20,
                                    "number": 0,
                                    "totalElements": 2,
                                    "totalPages": 1
                                  }
                                }""".formatted(
                                paintingId, artistId, museumId, countryId, NEW_IMAGE,
                                paintingId, updatedArtistId, updatedMuseumId, updatedCountryId, UPDATED_IMAGE))
                );

    }

    @Test
    @DisplayName("FindAll: throws BAD_REQUEST when request has validation errors")
    void findAll_ThrowsBadRequest_IfRequestHasValidationErrors() throws Exception {

        // Steps
        mockMvc.perform(get(PAINTING_URL)
                        .param("title", "as")
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
                                        "domain": "/api/painting",
                                        "reason": "pageable.Columns",
                                        "message": "[Columns] Request contains invalid columns. Available columns: [id, title, createdDate]"
                                      },
                                      {
                                        "domain": "/api/painting",
                                        "reason": "title",
                                        "message": "[Size] Param [title] must have length [3; 255]"
                                      }
                                    ]
                                  }
                                }""".formatted(apiVersion))
                );

    }

    @Test
    @DisplayName("Update: successfully updates existing painting")
    void update_ValidRequest_Success() throws Exception {

        // Stubs
        when(paintingsClient.update(any(UpdatePaintingRequestDTO.class)))
                .thenReturn(updatedPainting);

        // Steps
        mockMvc.perform(patch(PAINTING_URL)
                        .with(jwt().jwt(builder -> builder.claim("roles", "ROLE_USER")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(updatePaintingDTO)))
                .andDo(print())

                // Assertions
                .andExpectAll(
                        status().isOk(),
                        content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON),
                        content().json(om.writeValueAsString(updatedPainting))
                );

    }

    @Test
    @DisplayName("Update: throws NOT_FOUND when painting doesn't exist")
    void update_ThrowsNotFound_IfPaintingNotExists() throws Exception {

        // Stubs
        when(paintingsClient.update(updatePaintingDTO))
                .thenThrow(new PaintingNotFoundException(paintingId));

        // Steps
        mockMvc.perform(patch(PAINTING_URL)
                        .with(jwt().jwt(builder -> builder.claim("roles", "ROLE_USER")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(updatePaintingDTO)))
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
                                         "domain": "/api/painting",
                                         "reason": "Painting not found",
                                         "message": "Painting with id = [%2$s] not found"
                                       }
                                     ]
                                   }
                                 }""".formatted(apiVersion, paintingId.toString()))
                );

    }

    @Test
    @DisplayName("Update: throws NOT_FOUND when artist doesn't exist")
    void update_ThrowsNotFound_IfArtistNotExists() throws Exception {

        // Stubs
        when(paintingsClient.update(updatePaintingDTO))
                .thenThrow(new ArtistNotFoundException(updatedArtistId));

        // Steps
        mockMvc.perform(patch(PAINTING_URL)
                        .with(jwt().jwt(builder -> builder.claim("roles", "ROLE_USER")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(updatePaintingDTO)))
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
                                          "domain": "/api/painting",
                                          "reason": "Artist not found",
                                          "message": "Artist with id = [%2$s] not found"
                                        }
                                      ]
                                    }
                                  }""".formatted(apiVersion, updatedArtistId))
                );

    }

    @Test
    @DisplayName("Update: throws NOT_FOUND when museum doesn't exist")
    void update_ThrowsNotFound_IfMuseumNotExists() throws Exception {

        // Stubs
        when(paintingsClient.update(updatePaintingDTO))
                .thenThrow(new MuseumNotFoundException(updatedMuseumId));

        // Steps
        mockMvc.perform(patch(PAINTING_URL)
                        .with(jwt().jwt(builder -> builder.claim("roles", "ROLE_USER")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(updatePaintingDTO)))
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
                                          "domain": "/api/painting",
                                          "reason": "Museum not found",
                                          "message": "Museum with id = [%2$s] not found"
                                        }
                                      ]
                                    }
                                  }""".formatted(apiVersion, updatedMuseumId))
                );

    }

    @Disabled("Invalid status code if not use jwt builder")
    @Test
    @DisplayName("Update: throws UNAUTHORIZED when request doesn't have token")
    void update_ThrowsUnauthorized_IfSendRequestWithoutToken() throws Exception {

        // Stubs
        when(paintingsClient.update(any(UpdatePaintingRequestDTO.class)))
                .thenReturn(updatedPainting);

        // Steps
        mockMvc.perform(patch(PAINTING_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(updatePaintingDTO)))

                // Assertions
                .andExpect(status().isUnauthorized());

    }

    @Test
    @DisplayName("Update: throws BAD_REQUEST when request has validation errors")
    void update_ThrowsBadRequest_IfRequestHasValidationErrors() throws Exception {

        // Data
        final var requestDTO = UpdatePaintingRequestDTO.builder()
                .id(null)
                .title("Jo")
                .description("B")
                .artist(new ArtistIdDTO(null))
                .museum(new MuseumIdDTO(null))
                .photo("image")
                .build();

        // Steps
        mockMvc.perform(MockMvcRequestBuilders.post("/api/painting")
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
                                        "domain": "/api/painting",
                                        "reason": "photo.Image",
                                        "message": "[Image] Image base64 has invalid regex pattern"
                                      },
                                      {
                                        "domain": "/api/painting",
                                        "reason": "title",
                                        "message": "[Size] Painting title must have length between [3; 255]"
                                      },
                                      {
                                        "domain": "/api/painting",
                                        "reason": "description",
                                        "message": "[Size] Painting description must have length between [10; 2000]"
                                      },
                                      {
                                        "domain": "/api/painting",
                                        "reason": "artist.id",
                                        "message": "[NotNull] Painting artist_id must not equal null"
                                      },
                                      {
                                        "domain": "/api/painting",
                                        "reason": "museum.id",
                                        "message": "[NotNull] Painting country_id must not equal null"
                                      }
                                    ]
                                  }
                                }""".formatted(apiVersion))
                );

    }

    @Test
    @DisplayName("Update: throws CONFLICT when painting already exists")
    void update_ThrowsConflict_IfPaintingNameAlreadyTaken() throws Exception {

        // Stubs
        when(paintingsClient.update(updatePaintingDTO))
                .thenThrow(new PaintingAlreadyExistsException(updatePaintingDTO.title()));

        // Steps
        mockMvc.perform(MockMvcRequestBuilders.patch("/api/painting")
                        .with(jwt().jwt(builder -> builder.claim("roles", "ROLE_USER")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(updatePaintingDTO)))
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
                                    "message": "Painting title = [%2$s] is already taken",
                                    "errors": [
                                      {
                                        "domain": "/api/painting",
                                        "reason": "Painting already exists",
                                        "message": "Painting title = [%2$s] is already taken"
                                      }
                                    ]
                                  }
                                }""".formatted(apiVersion, updatePaintingDTO.title()))
                );

    }

}
