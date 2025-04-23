package org.rococo.gateway.ex.handler;

import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.rococo.gateway.ex.MuseumAlreadyExistsException;
import org.rococo.gateway.ex.MuseumNotFoundException;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Locale;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("MuseumsExceptionHandlerTests: Module test")
class MuseumsExceptionHandlerTests {

    private static final String MUSEUM_NOT_FOUND_REASON = "errors.api.museums.404.reason";
    private static final String MUSEUM_NOT_FOUND_ITEM_MESSAGE = "errors.api.museums.404.item_message";

    private static final String MUSEUM_ALREADY_EXISTS_REASON = "errors.api.museums.409.reason";
    private static final String MUSEUM_ALREADY_EXISTS_ITEM_MESSAGE = "errors.api.museums.409.item_message";

    private static final String API_VERSION = "0.0.1";
    private static final Locale LOCALE = Locale.ENGLISH;

    @Mock
    private HttpServletRequest request;

    @Mock
    private MessageSource messageSource;

    @InjectMocks
    private MuseumsExceptionHandler handler;

    @Test
    @DisplayName("HandleMuseumNotFoundException: returns NOT_FOUND")
    void handleMuseumNotFoundException_ReturnsNotFound_Test() {

        // Data
        final var museumId = UUID.randomUUID();
        final var requestUrl = "/api/museum/" + museumId;
        final var message = "Museum not found";
        final var reason = "Museum with id = [%s] not found".formatted(museumId);
        final var exception = new MuseumNotFoundException(museumId);

        // Mock
        ReflectionTestUtils.setField(handler, "apiVersion", API_VERSION);
        when(request.getRequestURI())
                .thenReturn(requestUrl);
        when(messageSource.getMessage(MUSEUM_NOT_FOUND_REASON, new Object[0], "Museum not found(default)", LOCALE))
                .thenReturn(reason);
        when(messageSource.getMessage(MUSEUM_NOT_FOUND_ITEM_MESSAGE, new String[]{museumId.toString()}, "Museum with id = [%s] not found(default)".formatted(museumId), LOCALE))
                .thenReturn(message);

        // Steps
        final var result = handler.handleMuseumNotFoundException(exception, request, LOCALE);

        // Assertions
        assertAll(
                () -> assertNotNull(result),
                () -> assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode()),
                () -> assertEquals(API_VERSION, result.getBody().getApiVersion()),
                () -> assertEquals(message, result.getBody().getError().errors().getFirst().itemMessage()),
                () -> assertEquals(requestUrl, result.getBody().getError().errors().getFirst().domain()),
                () -> assertEquals(reason, result.getBody().getError().errors().getFirst().reason())
        );

        verifyNoMoreInteractions(request);

    }

    @Test
    @DisplayName("HandleMuseumAlreadyExistsException: returns CONFLICT")
    void handleMuseumAlreadyExistsException_ReturnsConflict_Test() {

        // Data
        final var museumTitle = "Random title";
        final var requestUrl = "/api/museum";
        final var message = "Museum already exists";
        final var reason = "Museum with title = [%s] already exists".formatted(museumTitle);
        final var exception = new MuseumAlreadyExistsException(museumTitle);

        // Mock
        ReflectionTestUtils.setField(handler, "apiVersion", API_VERSION);
        when(request.getRequestURI())
                .thenReturn(requestUrl);
        when(messageSource.getMessage(MUSEUM_ALREADY_EXISTS_REASON, new Object[0], "Museum already exists(default)", LOCALE))
                .thenReturn(reason);
        when(messageSource.getMessage(MUSEUM_ALREADY_EXISTS_ITEM_MESSAGE, new String[]{museumTitle}, "Museum with title = [%s] already exists(default)".formatted(museumTitle), LOCALE))
                .thenReturn(message);

        // Steps
        final var result = handler.handleMuseumAlreadyExistsException(exception, request, LOCALE);

        // Assertions
        assertAll(
                () -> assertNotNull(result),
                () -> assertEquals(HttpStatus.CONFLICT, result.getStatusCode()),
                () -> assertEquals(API_VERSION, result.getBody().getApiVersion()),
                () -> assertEquals(message, result.getBody().getError().errors().getFirst().itemMessage()),
                () -> assertEquals(requestUrl, result.getBody().getError().errors().getFirst().domain()),
                () -> assertEquals(reason, result.getBody().getError().errors().getFirst().reason())
        );

        verifyNoMoreInteractions(request);

    }

}
