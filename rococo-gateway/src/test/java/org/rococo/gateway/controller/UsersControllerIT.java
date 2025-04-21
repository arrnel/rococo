package org.rococo.gateway.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.rococo.gateway.client.UsersGrpcClient;
import org.rococo.gateway.model.users.UserDTO;
import org.springframework.beans.factory.annotation.Autowired;
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

import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("UsersController: Integration tests")
class UsersControllerIT {

    private static final String NEW_IMAGE = "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAQAAAC1HAwCAAAAC0lEQVR42mNk+A8AAQUBAScY42YAAAAASUVORK5CYII=";

    private static final String USER_URL = "/api/user";

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private JwtDecoder jwtDecoder;

    @MockBean
    private UsersGrpcClient usersClient;

    private UserDTO user1;
    private UserDTO user2;

    @BeforeEach
    void setUp() {

        user1 = UserDTO.builder()
                .id(UUID.randomUUID())
                .username("jonny.doe.777")
                .firstName("John")
                .lastName("Doe")
                .photo(NEW_IMAGE)
                .build();

        user2 = UserDTO.builder()
                .id(UUID.randomUUID())
                .username("test.user")
                .firstName("Jonny")
                .lastName("Dew")
                .photo(NEW_IMAGE)
                .build();

    }

    @Test
    @DisplayName("GetAllUsers: returns users")
    void getAllUsers_Success() throws Exception {
        // Data
        final var pageable = PageRequest.of(0, 20, Sort.by("username"));
        final Page<UserDTO> page = new PageImpl<>(
                List.of(user1, user2),
                pageable,
                2
        );

        // Stubs
        when(usersClient.findAll(pageable))
                .thenReturn(page);

        // Steps
        mockMvc.perform(get(USER_URL + "/all")
                        .param("page", "0")
                        .param("size", "20")
                        .param("sort", "username,asc")
                        .with(jwt().jwt(builder -> builder
                                .claim("sub", "test_user")
                                .claim("role", "ROLE_USER"))))
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
                                      "username": "%s"
                                    },
                                    {
                                      "id": "%s",
                                      "username": "%s"
                                    }
                                  ],
                                  "page": {
                                    "size": 20,
                                    "number": 0,
                                    "totalElements": 2,
                                    "totalPages": 1
                                  }
                                }""".formatted(user1.getId(), user1.getUsername(), user2.getId(), user2.getUsername()))
                );
    }

}
