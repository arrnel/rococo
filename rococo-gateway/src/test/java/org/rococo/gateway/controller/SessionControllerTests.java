package org.rococo.gateway.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.oauth2.jwt.Jwt;

import java.time.Instant;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("SessionController: Module tests")
class SessionControllerTests {

    @Mock
    private Jwt jwt;

    @InjectMocks
    private SessionController sessionController;

    @Test
    @DisplayName("Session: returns empty session when principal is null")
    void session_ReturnsEmpty_WhenPrincipalIsNull() {

        // Data
        final var result = sessionController.session(null);

        // Assertions
        assertNull(result.username());
        assertNull(result.issuedAt());
        assertNull(result.expiresAt());

    }

    @Test
    @DisplayName("Session: returns filled session when principal is present")
    void session_ReturnsFilled_WhenPrincipalIsPresent() {

        // Data
        final var issuedAt = Instant.now();
        final var expiresAt = issuedAt.plusSeconds(3600);
        final var username = "test-user";

        when(jwt.getClaimAsString("sub"))
                .thenReturn(username);
        when(jwt.getIssuedAt())
                .thenReturn(issuedAt);
        when(jwt.getExpiresAt())
                .thenReturn(expiresAt);

        // Act
        final var result = sessionController.session(jwt);

        // Assert
        assertEquals(username, result.username());
        assertEquals(Date.from(issuedAt), result.issuedAt());
        assertEquals(Date.from(expiresAt), result.expiresAt());

    }

}
