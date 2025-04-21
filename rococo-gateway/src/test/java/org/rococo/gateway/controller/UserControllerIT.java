package org.rococo.gateway.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.rococo.gateway.client.UsersGrpcClient;
import org.rococo.gateway.model.users.UpdateUserRequestDTO;
import org.rococo.gateway.model.users.UserDTO;
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

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("UserController: Integration tests")
class UserControllerIT {

    private static final String NEW_IMAGE = "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAQAAAC1HAwCAAAAC0lEQVR42mNk+A8AAQUBAScY42YAAAAASUVORK5CYII=";
    private static final String UPDATED_IMAGE = "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAYAAAAfFcSJAAAADUlEQVR42mNMefj/PwAHOgNF1x8QkwAAAABJRU5ErkJggg==";

    private static final String USER_URL = "/api/user";
    private static final ObjectMapper om = new ObjectMapper();

    @Value("${app.api.version}")
    private String apiVersion;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private JwtDecoder jwtDecoder;

    @MockBean
    private UsersGrpcClient usersClient;

    private UserDTO user;
    private UserDTO updatedUser;
    private UpdateUserRequestDTO updateUserDTO;
    private UUID userId;
    private String username;

    @BeforeEach
    void setUp() {

        userId = UUID.randomUUID();
        username = "jonny.doe.777";

        user = UserDTO.builder()
                .id(userId)
                .username("jonny.doe.777")
                .firstName("John")
                .lastName("Doe")
                .photo(NEW_IMAGE)
                .build();

        updatedUser = UserDTO.builder()
                .id(userId)
                .username("jonny.doe.777")
                .firstName("Jonny")
                .lastName("Dew")
                .photo(UPDATED_IMAGE)
                .build();

        updateUserDTO = UpdateUserRequestDTO.builder()
                .username("jonny.doe.777")
                .firstName("Jonny")
                .lastName("Dew")
                .photo(UPDATED_IMAGE)
                .build();

    }

    @Test
    @DisplayName("GetCurrentUser: returns current user by username")
    void getCurrentUser_ReturnsCurrentUser() throws Exception {

        // Stubs
        when(usersClient.findByUsername(username))
                .thenReturn(Optional.of(user));

        // Steps
        mockMvc.perform(get(USER_URL)
                        .with(jwt().jwt(builder -> builder
                                .claim("sub", username)
                                .claim("role", "ROLE_USER"))))
                .andDo(print())
                // Assertions
                .andExpectAll(
                        status().isOk(),
                        content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON),
                        content().json(om.writeValueAsString(user))
                );

    }

    @Test
    @DisplayName("GetUser: throws NOT_FOUND when current user not found by username")
    void getUser_ThrowsNotFound_IfCurrentUserNotFoundByUsername() throws Exception {

        // Stubs
        when(usersClient.findByUsername(username))
                .thenReturn(Optional.empty());

        // Steps
        mockMvc.perform(get(USER_URL)
                        .with(jwt().jwt(builder -> builder
                                .claim("sub", username)
                                .claim("role", "ROLE_USER"))))
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
                                    "message": "User with id = [%2$s] not found",
                                    "errors": [
                                      {
                                        "domain": "/api/user",
                                        "reason": "User not found",
                                        "message": "User with id = [%2$s] not found"
                                      }
                                    ]
                                  }
                                }""".formatted(apiVersion, username))
                );

    }

    @Test
    @DisplayName("UpdateCurrentUser: successfully updates current user")
    void updateCurrentUser_UpdateUser_Success() throws Exception {

        // Stubs
        when(usersClient.findByUsername(username))
                .thenReturn(Optional.of(user));
        when(usersClient.update(userId, updateUserDTO))
                .thenReturn(updatedUser);

        // Steps
        mockMvc.perform(patch(USER_URL)
                        .with(jwt().jwt(builder -> builder
                                .claim("sub", username)
                                .claim("role", "ROLE_USER")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(updateUserDTO)))
                .andDo(print())

                // Assertions
                .andExpectAll(
                        status().isOk(),
                        content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON),
                        content().json(om.writeValueAsString(updatedUser))
                );

    }

    @Test
    @DisplayName("UpdateCurrentUser: throws BAD_REQUEST when request has validation errors")
    void updateCurrentUser_ThrowsBadRequest_IfRequestHasValidationErrors() throws Exception {

        // Data
        final var request = UpdateUserRequestDTO.builder()
                .username("1")
                .firstName("f")
                .lastName("l")
                .photo("image")
                .build();

        // Stubs
        when(usersClient.findByUsername(username))
                .thenReturn(Optional.of(user));

        // Steps
        mockMvc.perform(patch(USER_URL)
                        .with(jwt().jwt(builder -> builder
                                .claim("sub", username)
                                .claim("role", "ROLE_USER")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(request)))
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
                                        "domain": "/api/user",
                                        "reason": "lastName",
                                        "message": "[Size] Last name must have length between [3; 255]"
                                      },
                                      {
                                        "domain": "/api/user",
                                        "reason": "firstName",
                                        "message": "[Size] First name must have length between [3; 255]"
                                      },
                                      {
                                        "domain": "/api/user",
                                        "reason": "photo.Image",
                                        "message": "[Image] Image base64 has invalid regex pattern"
                                      },
                                      {
                                        "domain": "/api/user",
                                        "reason": "username",
                                        "message": "[Pattern] Username must have pattern"
                                      }
                                    ]
                                  }
                                }""".formatted(apiVersion))
                );

    }

}
