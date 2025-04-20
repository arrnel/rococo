package org.rococo.gateway.ex.handler;

import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.rococo.gateway.ex.ArtistAlreadyExistsException;
import org.rococo.gateway.ex.ArtistNotFoundException;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Locale;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("ArtistsExceptionHandlerTests: Module test")
class ArtistsExceptionHandlerTests {

    private static final String ARTIST_NOT_FOUND_REASON = "errors.api.artists.404.reason";
    private static final String ARTIST_NOT_FOUND_ITEM_MESSAGE = "errors.api.artists.404.item_message";

    private static final String ARTIST_ALREADY_EXISTS_REASON = "errors.api.artists.409.reason";
    private static final String ARTIST_ALREADY_EXISTS_ITEM_MESSAGE = "errors.api.artists.409.item_message";

    private static final String API_VERSION = "0.0.1";
    private static final Locale LOCALE = Locale.ENGLISH;

    @Mock
    private HttpServletRequest request;

    @Mock
    private MessageSource messageSource;

    @InjectMocks
    private ArtistsExceptionHandler handler;

    @Test
    @DisplayName("HandleArtistNotFoundException: returns NOT_FOUND")
    void handleArtistNotFoundException_ReturnsNotFound_Test() {

        // Data
        final var artistId = UUID.randomUUID();
        final var requestUrl = "/api/artist/" + artistId;
        final var message = "Artist not found";
        final var reason = "Artist with id = [%s] not found".formatted(artistId);
        final var exception = new ArtistNotFoundException(artistId);

        // Mock
        ReflectionTestUtils.setField(handler, "apiVersion", API_VERSION);
        when(request.getRequestURI())
                .thenReturn(requestUrl);
        when(messageSource.getMessage(ARTIST_NOT_FOUND_REASON, new Object[0], "Artist not found(default)", LOCALE))
                .thenReturn(reason);
        when(messageSource.getMessage(ARTIST_NOT_FOUND_ITEM_MESSAGE, new String[]{artistId.toString()}, "Artist with id = [%s] not found(default)".formatted(artistId), LOCALE))
                .thenReturn(message);

        // Steps
        final var result = handler.handleArtistNotFoundException(exception, request, LOCALE);

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
    @DisplayName("HandleArtistAlreadyExistsException: returns CONFLICT")
    void handleArtistAlreadyExistsException_ReturnsConflict_Test() {

        // Data
        final var artistName = "Random name";
        final var requestUrl = "/api/artist";
        final var message = "Artist already exists";
        final var reason = "Artist with name = [%s] already exists".formatted(artistName);
        final var exception = new ArtistAlreadyExistsException(artistName);

        // Mock
        ReflectionTestUtils.setField(handler, "apiVersion", API_VERSION);
        when(request.getRequestURI())
                .thenReturn(requestUrl);
        when(messageSource.getMessage(ARTIST_ALREADY_EXISTS_REASON, new Object[0], "Artist already exists(default)", LOCALE))
                .thenReturn(reason);
        when(messageSource.getMessage(ARTIST_ALREADY_EXISTS_ITEM_MESSAGE, new String[]{artistName}, "Artist with name = [%s] already exists(default)".formatted(artistName), LOCALE))
                .thenReturn(message);

        // Steps
        final var result = handler.handleArtistAlreadyExistsException(exception, request, LOCALE);

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
