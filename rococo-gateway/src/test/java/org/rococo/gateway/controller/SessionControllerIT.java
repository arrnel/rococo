package org.rococo.gateway.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("UserController: Integration tests")
class SessionControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private JwtDecoder jwtDecoder;

    @Test
    @DisplayName("Session: should return empty session when unauthenticated")
    void session_shouldReturnEmptySession_WhenUnauthenticated() throws Exception {
        mockMvc.perform(get("/api/session"))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.username").doesNotExist(),
                        jsonPath("$.issuedAt").doesNotExist(),
                        jsonPath("$.expiresAt").doesNotExist()

                );
    }

    @Test
    @DisplayName("Session: should return session when authenticated")
    void session_shouldReturnSession_WhenAuthenticated() throws Exception {

        // Data
        final var username = "test_user";
        final var issuedAtText = "2025-04-21T10:00:00.000+00:00";
        final var expiresAtText = "2025-04-21T11:00:00.000+00:00";
        final var issuedAt = Instant.parse(issuedAtText);
        final var expiresAt = Instant.parse(expiresAtText);

        // Steps
        mockMvc.perform(get("/api/session")
                        .with(SecurityMockMvcRequestPostProcessors.jwt()
                                .jwt(Jwt.withTokenValue("token")
                                        .header("alg", "none")
                                        .claim("sub", username)
                                        .issuedAt(issuedAt)
                                        .expiresAt(expiresAt)
                                        .build())))

                // Assertion
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value(username))
                .andExpect(jsonPath("$.issuedAt").value(issuedAtText))
                .andExpect(jsonPath("$.expiresAt").value(expiresAtText));

    }

}
