package org.rococo.gateway.ex.handler;

import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.rococo.gateway.ex.PaintingAlreadyExistsException;
import org.rococo.gateway.ex.PaintingNotFoundException;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Locale;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("PaintingsExceptionHandlerTests: Module test")
class PaintingsExceptionHandlerTests {

    private static final String PAINTING_NOT_FOUND_REASON = "errors.api.paintings.404.reason";
    private static final String PAINTING_NOT_FOUND_ITEM_MESSAGE = "errors.api.paintings.404.item_message";

    private static final String PAINTING_ALREADY_EXISTS_REASON = "errors.api.paintings.409.reason";
    private static final String PAINTING_ALREADY_EXISTS_ITEM_MESSAGE = "errors.api.paintings.409.item_message";

    private static final String API_VERSION = "0.0.1";
    private static final Locale LOCALE = Locale.ENGLISH;

    @Mock
    private HttpServletRequest request;

    @Mock
    private MessageSource messageSource;

    @InjectMocks
    private PaintingsExceptionHandler handler;

    @Test
    @DisplayName("HandlePaintingNotFoundException: returns NOT_FOUND")
    void handlePaintingNotFoundException_ReturnsNotFound_Test() {

        // Data
        final var paintingId = UUID.randomUUID();
        final var requestUrl = "/api/painting/" + paintingId;
        final var message = "Painting not found";
        final var reason = "Painting with id = [%s] not found".formatted(paintingId);
        final var exception = new PaintingNotFoundException(paintingId);

        // Mock
        ReflectionTestUtils.setField(handler, "apiVersion", API_VERSION);
        when(request.getRequestURI())
                .thenReturn(requestUrl);
        when(messageSource.getMessage(PAINTING_NOT_FOUND_REASON, new Object[0], "Painting not found(default)", LOCALE))
                .thenReturn(reason);
        when(messageSource.getMessage(PAINTING_NOT_FOUND_ITEM_MESSAGE, new String[]{paintingId.toString()}, "Painting with id = [%s] not found(default)".formatted(paintingId), LOCALE))
                .thenReturn(message);

        // Steps
        final var result = handler.handlePaintingNotFoundException(exception, request, LOCALE);

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
    @DisplayName("HandlePaintingAlreadyExistsException: returns CONFLICT")
    void handlePaintingAlreadyExistsException_ReturnsConflict_Test() {

        // Data
        final var paintingTitle = "Random title";
        final var requestUrl = "/api/painting";
        final var message = "Painting already exists";
        final var reason = "Painting with title = [%s] already exists".formatted(paintingTitle);
        final var exception = new PaintingAlreadyExistsException(paintingTitle);

        // Mock
        ReflectionTestUtils.setField(handler, "apiVersion", API_VERSION);
        when(request.getRequestURI())
                .thenReturn(requestUrl);
        when(messageSource.getMessage(PAINTING_ALREADY_EXISTS_REASON, new Object[0], "Painting already exists(default)", LOCALE))
                .thenReturn(reason);
        when(messageSource.getMessage(PAINTING_ALREADY_EXISTS_ITEM_MESSAGE, new String[]{paintingTitle}, "Painting with title = [%s] already exists(default)".formatted(paintingTitle), LOCALE))
                .thenReturn(message);

        // Steps
        final var result = handler.handlePaintingAlreadyExistsException(exception, request, LOCALE);

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
