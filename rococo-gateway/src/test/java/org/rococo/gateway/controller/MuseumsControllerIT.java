package org.rococo.gateway.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.rococo.gateway.client.MuseumsGrpcClient;
import org.rococo.gateway.ex.CountryNotFoundException;
import org.rococo.gateway.ex.MuseumAlreadyExistsException;
import org.rococo.gateway.ex.MuseumNotFoundException;
import org.rococo.gateway.model.countries.CountryDTO;
import org.rococo.gateway.model.countries.CountryIdDTO;
import org.rococo.gateway.model.countries.LocationRequestDTO;
import org.rococo.gateway.model.countries.LocationResponseDTO;
import org.rococo.gateway.model.museums.AddMuseumRequestDTO;
import org.rococo.gateway.model.museums.MuseumDTO;
import org.rococo.gateway.model.museums.UpdateMuseumRequestDTO;
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
@DisplayName("MuseumsController: Integration tests")
class MuseumsControllerIT {

    private static final String NEW_IMAGE = "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAQAAAC1HAwCAAAAC0lEQVR42mNk+A8AAQUBAScY42YAAAAASUVORK5CYII=";
    private static final String UPDATED_IMAGE = "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAYAAAAfFcSJAAAADUlEQVR42mNMefj/PwAHOgNF1x8QkwAAAABJRU5ErkJggg==";

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
    private MuseumDTO updatedMuseum;
    private AddMuseumRequestDTO addMuseumDTO;
    private UpdateMuseumRequestDTO updateMuseumDTO;
    private UUID museumId;
    private UUID countryId;
    private UUID newCountryId;

    @BeforeEach
    void setUp() {

        museumId = UUID.randomUUID();
        countryId = UUID.randomUUID();
        newCountryId = UUID.randomUUID();
        museum = MuseumDTO.builder()
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
                .photo(NEW_IMAGE)
                .build();

        addMuseumDTO = AddMuseumRequestDTO.builder()
                .title("Orsay Museum")
                .description("Description of Orsay Museum")
                .location(LocationRequestDTO.builder()
                        .city("Paris")
                        .country(new CountryIdDTO(countryId))
                        .build())
                .photo(NEW_IMAGE)
                .build();

        updateMuseumDTO = UpdateMuseumRequestDTO.builder()
                .id(museumId)
                .title("National Gallery of Art")
                .description("Description of National Gallery of Art")
                .location(LocationRequestDTO.builder()
                        .city("New York City")
                        .country(new CountryIdDTO(newCountryId))
                        .build())
                .photo(UPDATED_IMAGE)
                .build();

        updatedMuseum = MuseumDTO.builder()
                .id(museumId)
                .title("National Gallery of Art")
                .description("Description of National Gallery of Art")
                .location(LocationResponseDTO.builder()
                        .city("New York City")
                        .country(CountryDTO.builder()
                                .id(newCountryId)
                                .name("United States")
                                .code("US")
                                .build())
                        .build())
                .photo(UPDATED_IMAGE)
                .build();

    }

    @Test
    @DisplayName("Add: returns created museum when request is valid")
    void add_ValidRequest_ReturnsCreatedMuseum() throws Exception {

        // Stub
        when(museumsClient.add(addMuseumDTO))
                .thenReturn(museum);

        // Steps
        mockMvc.perform(MockMvcRequestBuilders.post("/api/museum")
                        .with(jwt().jwt(builder -> builder.claim("roles", "ROLE_USER")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(addMuseumDTO)))
                .andDo(print())

                // Assertions
                .andExpectAll(
                        status().isCreated(),
                        content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON),
                        content().json(om.writeValueAsString(museum))
                );

    }

    @Disabled("Invalid status code if not use jwt builder")
    @Test
    @DisplayName("Add: throws UNAUTHORIZED when request doesn't have token")
    void add_ThrowsUnauthorized_IfSendRequestWithoutToken() throws Exception {

        // Steps
        mockMvc.perform(MockMvcRequestBuilders.post("/api/museum")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(addMuseumDTO)))
                .andDo(print())

                // Assertions
                .andExpect(status().isUnauthorized());

    }

    @Test
    @DisplayName("Add: returns created museum when request is valid")
    void add_ThrowsBadRequest_IfRequestHasValidationErrors() throws Exception {

        // Data
        final var requestDTO = AddMuseumRequestDTO.builder()
                .title("Jo")
                .description("B")
                .location(LocationRequestDTO.builder()
                        .city("")
                        .country(new CountryIdDTO(null))
                        .build())
                .photo("image")
                .build();

        // Steps
        mockMvc.perform(MockMvcRequestBuilders.post("/api/museum")
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
                                          "domain": "/api/museum",
                                          "reason": "location.city",
                                          "message": "[NotBlank] Museum city must not be blank"
                                        },
                                        {
                                          "domain": "/api/museum",
                                          "reason": "location.city",
                                          "message": "[Size] Museum city must have length between [3; 255]"
                                        },
                                        {
                                          "domain": "/api/museum",
                                          "reason": "description",
                                          "message": "[Size] Museum description must have length between [10; 2000]"
                                        },
                                        {
                                          "domain": "/api/museum",
                                          "reason": "photo.Image",
                                          "message": "[Image] Image base64 has invalid regex pattern"
                                        },
                                        {
                                          "domain": "/api/museum",
                                          "reason": "title",
                                          "message": "[Size] Museum title must have length between [3; 255]"
                                        },
                                        {
                                          "domain": "/api/museum",
                                          "reason": "location.country.id",
                                          "message": "[NotNull] Museum country_id must not be empty"
                                        }
                                      ]
                                    }
                                  }""".formatted(apiVersion))
                );

    }

    @Test
    @DisplayName("Add: throws CONFLICT when museum already exists")
    void add_ThrowsConflict_IfMuseumNameAlreadyTaken() throws Exception {

        // Stubs
        when(museumsClient.add(addMuseumDTO))
                .thenThrow(new MuseumAlreadyExistsException(addMuseumDTO.title()));

        // Steps
        mockMvc.perform(MockMvcRequestBuilders.post("/api/museum")
                        .with(jwt().jwt(builder -> builder.claim("roles", "ROLE_USER")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(addMuseumDTO)))
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
                                     "message": "Museum title = [%2$s] is already taken",
                                     "errors": [
                                       {
                                         "domain": "/api/museum",
                                         "reason": "Museum already exists",
                                         "message": "Museum title = [%2$s] is already taken"
                                       }
                                     ]
                                   }
                                 }""".formatted(apiVersion, addMuseumDTO.title()))
                );

    }

    @Test
    @DisplayName("Add: throws NOT_FOUND when museum client throws CountryNotFoundException")
    void add_ThrowsNotFound_IfMuseumClientThrowsCountryNotFoundException() throws Exception {

        // Stubs
        when(museumsClient.add(addMuseumDTO))
                .thenThrow(new CountryNotFoundException(countryId));

        // Steps
        mockMvc.perform(MockMvcRequestBuilders.post("/api/museum")
                        .with(jwt().jwt(builder -> builder.claim("roles", "ROLE_USER")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(addMuseumDTO)))
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
                                          "domain": "/api/museum",
                                          "reason": "Country not found",
                                          "message": "Country with id = [%2$s] not found"
                                        }
                                      ]
                                    }
                                  }""".formatted(apiVersion, countryId))
                );

    }

    @Test
    @DisplayName("FindAll: returns paginated museums")
    void findAll_ValidRequest_ReturnsPaginatedMuseums() throws Exception {

        // Data
        final var museum2 = MuseumDTO.builder()
                .id(UUID.randomUUID())
                .title("Louvre")
                .description("Description of Louvre")
                .location(LocationResponseDTO.builder()
                        .city("Paris")
                        .country(CountryDTO.builder()
                                .id(museum.getLocation().country().id())
                                .name("France")
                                .code("FR")
                                .build())
                        .build())
                .photo(NEW_IMAGE)
                .build();

        final Page<MuseumDTO> page = new PageImpl<>(
                List.of(museum, museum2),
                PageRequest.of(0, 20, Sort.by("title").ascending()),
                2
        );

        // Stubs
        when(museumsClient.findAll(any(), anyBoolean(), any()))
                .thenReturn(page);

        // Steps
        mockMvc.perform(get(MUSEUM_URL)
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
                                       "id": "%1$s",
                                       "title": "Orsay Museum",
                                       "description": "Description of Orsay Museum",
                                       "geo": {
                                         "city": "Paris",
                                         "country": {
                                           "id": "%3$s",
                                           "name": "France",
                                           "code": "FR"
                                         }
                                       },
                                       "photo": "%4$s"
                                     },
                                     {
                                       "id": "%2$s",
                                       "title": "Louvre",
                                       "description": "Description of Louvre",
                                       "geo": {
                                         "city": "Paris",
                                         "country": {
                                           "id": "%3$s",
                                           "name": "France",
                                           "code": "FR"
                                         }
                                       },
                                       "photo": "%4$s"
                                     }
                                   ],
                                   "page": {
                                     "size": 20,
                                     "number": 0,
                                     "totalElements": 2,
                                     "totalPages": 1
                                   }
                                 }""".formatted(museumId, museum2.getId().toString(), museum.getLocation().country().id(), NEW_IMAGE))
                );

    }

    @Test
    @DisplayName("FindAll: throws BAD_REQUEST when request has validation errors")
    void findAll_ThrowsBadRequest_IfRequestHasValidationErrors() throws Exception {

        // Steps
        mockMvc.perform(get(MUSEUM_URL)
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
                                        "domain": "/api/museum",
                                        "reason": "title",
                                        "message": "[Size] Param [title] must have length [3; 255]"
                                      },
                                      {
                                        "domain": "/api/museum",
                                        "reason": "pageable.Columns",
                                        "message": "[Columns] Request contains invalid columns. Available columns: [id, title, createdDate]"
                                      }
                                    ]
                                  }
                                }""".formatted(apiVersion))
                );

    }

    @Test
    @DisplayName("Update: successfully updates existing museum")
    void update_ValidRequest_Success() throws Exception {

        // Stubs
        when(museumsClient.update(updateMuseumDTO))
                .thenReturn(updatedMuseum);

        // Steps
        mockMvc.perform(patch(MUSEUM_URL)
                        .with(jwt().jwt(builder -> builder.claim("roles", "ROLE_USER")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(updateMuseumDTO)))
                .andDo(print())

                // Assertions
                .andExpectAll(
                        status().isOk(),
                        content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON),
                        content().json(om.writeValueAsString(updatedMuseum))
                );

    }

    @Test
    @DisplayName("Update: throws NOT_FOUND when museum doesn't exist")
    void update_ThrowsNotFound_IfMuseumNotExists() throws Exception {

        // Stubs
        when(museumsClient.update(updateMuseumDTO))
                .thenThrow(new MuseumNotFoundException(museumId));

        // Steps
        mockMvc.perform(patch(MUSEUM_URL)
                        .with(jwt().jwt(builder -> builder.claim("roles", "ROLE_USER")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(updateMuseumDTO)))
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
                                         "domain": "/api/museum",
                                         "reason": "Museum not found",
                                         "message": "Museum with id = [%2$s] not found"
                                       }
                                     ]
                                   }
                                 }""".formatted(apiVersion, museumId.toString()))
                );

    }

    @Test
    @DisplayName("Update: throws NOT_FOUND when country doesn't exist")
    void update_ThrowsNotFound_IfCountryNotExists() throws Exception {

        // Stubs
        when(museumsClient.update(updateMuseumDTO))
                .thenThrow(new CountryNotFoundException(newCountryId));

        // Steps
        mockMvc.perform(patch(MUSEUM_URL)
                        .with(jwt().jwt(builder -> builder.claim("roles", "ROLE_USER")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(updateMuseumDTO)))
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
                                          "domain": "/api/museum",
                                          "reason": "Country not found",
                                          "message": "Country with id = [%2$s] not found"
                                        }
                                      ]
                                    }
                                  }""".formatted(apiVersion, newCountryId))
                );

    }

    @Disabled("Invalid status code if not use jwt builder")
    @Test
    @DisplayName("Update: throws UNAUTHORIZED when request doesn't have token")
    void update_ThrowsUnauthorized_IfSendRequestWithoutToken() throws Exception {

        // Steps
        mockMvc.perform(patch(MUSEUM_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(updateMuseumDTO)))

                // Assertions
                .andExpect(status().isUnauthorized());

    }

    @Test
    @DisplayName("Update: throws BAD_REQUEST when request has validation errors")
    void update_ThrowsBadRequest_IfRequestHasValidationErrors() throws Exception {

        // Data
        final var requestDTO = UpdateMuseumRequestDTO.builder()
                .id(null)
                .title("Jo")
                .description("B")
                .location(LocationRequestDTO.builder()
                        .city("P")
                        .country(new CountryIdDTO(null))
                        .build())
                .photo("image")
                .build();

        // Steps
        mockMvc.perform(MockMvcRequestBuilders.post("/api/museum")
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
                                        "domain": "/api/museum",
                                        "reason": "location.country.id",
                                        "message": "[NotNull] Museum country_id must not be empty"
                                      },
                                      {
                                        "domain": "/api/museum",
                                        "reason": "title",
                                        "message": "[Size] Museum title must have length between [3; 255]"
                                      },
                                      {
                                        "domain": "/api/museum",
                                        "reason": "photo.Image",
                                        "message": "[Image] Image base64 has invalid regex pattern"
                                      },
                                      {
                                        "domain": "/api/museum",
                                        "reason": "description",
                                        "message": "[Size] Museum description must have length between [10; 2000]"
                                      },
                                      {
                                        "domain": "/api/museum",
                                        "reason": "location.city",
                                        "message": "[Size] Museum city must have length between [3; 255]"
                                      }
                                    ]
                                  }
                                }""".formatted(apiVersion))
                );

    }

    @Test
    @DisplayName("Update: throws CONFLICT when museum already exists")
    void update_ThrowsConflict_IfMuseumNameAlreadyTaken() throws Exception {

        // Stubs
        when(museumsClient.update(updateMuseumDTO))
                .thenThrow(new MuseumAlreadyExistsException(updateMuseumDTO.title()));

        // Steps
        mockMvc.perform(MockMvcRequestBuilders.patch("/api/museum")
                        .with(jwt().jwt(builder -> builder.claim("roles", "ROLE_USER")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(updateMuseumDTO)))
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
                                    "message": "Museum title = [%2$s] is already taken",
                                    "errors": [
                                      {
                                        "domain": "/api/museum",
                                        "reason": "Museum already exists",
                                        "message": "Museum title = [%2$s] is already taken"
                                      }
                                    ]
                                  }
                                }""".formatted(apiVersion, updateMuseumDTO.title()))
                );

    }

}
