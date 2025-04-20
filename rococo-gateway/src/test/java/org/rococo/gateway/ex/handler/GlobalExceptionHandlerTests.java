package org.rococo.gateway.ex.handler;

import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Locale;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("GlobalExceptionHandlerTests: Module test")
class GlobalExceptionHandlerTests {

    private static final String INTERNAL_ERROR_REASON = "errors.api.services.500.reason";
    private static final String API_VERSION = "0.0.1";
    private static final Locale LOCALE = Locale.ENGLISH;

    @Mock
    private HttpServletRequest request;

    @Mock
    private MessageSource messageSource;

    @InjectMocks
    private GlobalExceptionHandler handler;

    @Test
    @DisplayName("HandleGlobalException: returns INTERNAL_SERVER_ERROR")
    void handleGlobalException_ReturnsInternalServerError_Test() {

        // Data
        final var requestUrl = "/api/test";
        final var exceptionMessage = "Test error";
        final var reason = "Internal server error";
        final var exception = new Exception(exceptionMessage);

        // Mock
        ReflectionTestUtils.setField(handler, "apiVersion", API_VERSION);
        when(request.getRequestURI())
                .thenReturn(requestUrl);
        when(request.getMethod())
                .thenReturn("GET");
        when(messageSource.getMessage(INTERNAL_ERROR_REASON, new Object[0], "INTERNAL SERVER ERROR(default)", LOCALE))
                .thenReturn(reason);

        // Steps
        final var result = handler.handleGlobalException(exception, request, LOCALE);

        // Assertions
        assertAll(
                () -> assertNotNull(result),
                () -> assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, result.getStatusCode()),
                () -> assertEquals(API_VERSION, result.getBody().getApiVersion()),
                () -> assertEquals(exceptionMessage, result.getBody().getError().errors().getFirst().itemMessage()),
                () -> assertEquals(requestUrl, result.getBody().getError().errors().getFirst().domain()),
                () -> assertEquals(reason, result.getBody().getError().errors().getFirst().reason())
        );

        verifyNoMoreInteractions(request, messageSource);

    }

}
