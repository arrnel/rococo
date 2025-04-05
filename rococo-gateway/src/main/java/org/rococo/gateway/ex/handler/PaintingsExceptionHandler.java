package org.rococo.gateway.ex.handler;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.rococo.gateway.ex.PaintingAlreadyExistException;
import org.rococo.gateway.ex.PaintingNotFoundException;
import org.rococo.gateway.model.ApiError;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Locale;

@Slf4j
@ControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
@RequiredArgsConstructor
@ParametersAreNonnullByDefault
public class PaintingsExceptionHandler {

    private static final String PAINTING_NOT_FOUND_REASON = "errors.api.paintings.404.reason";
    private static final String PAINTING_NOT_FOUND_ITEM_MESSAGE = "errors.api.paintings.404.item_message";

    private static final String PAINTING_ALREADY_EXISTS_REASON = "errors.api.paintings.409.reason";
    private static final String PAINTING_ALREADY_EXISTS_ITEM_MESSAGE = "errors.api.paintings.409.item_message";

    private final MessageSource messageSource;

    @Value("${app.api.version}")
    private String apiVersion;

    @Nonnull
    @ExceptionHandler(PaintingNotFoundException.class)
    public ResponseEntity<ApiError> handlePaintingNotFoundException(final PaintingNotFoundException ex,
                                                                    final HttpServletRequest request,
                                                                    final Locale locale
    ) {

        log.error("Painting with id = [{}] not found. uri: {}, message: {}", ex.getId(), request.getRequestURI(), ex.getMessage());

        final String reason = messageSource.getMessage(
                PAINTING_NOT_FOUND_REASON,
                new Object[0],
                "Painting not found(default)",
                locale);

        final String message = messageSource.getMessage(
                PAINTING_NOT_FOUND_ITEM_MESSAGE,
                new String[]{ex.getId().toString()},
                "Painting with id = [%s] not found(default)".formatted(ex.getId().toString()),
                locale);

        final var apiError = ApiError.builder()
                .apiVersion(apiVersion)
                .code(HttpStatus.NOT_FOUND.toString())
                .domain(request.getRequestURI())
                .reason(reason)
                .message(message)
                .build();

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_PROBLEM_JSON_VALUE);

        return new ResponseEntity<>(apiError, headers, HttpStatus.NOT_FOUND);

    }

    @Nonnull
    @ExceptionHandler(PaintingAlreadyExistException.class)
    public ResponseEntity<ApiError> handlePaintingAlreadyExistsException(final PaintingAlreadyExistException ex,
                                                                         final HttpServletRequest request,
                                                                         final Locale locale
    ) {

        log.error("Painting with name = [{}] already exists. uri: {}, itemMessage: {}", ex.getTitle(), request.getRequestURI(), ex.getMessage());

        final var reason = messageSource.getMessage(
                PAINTING_ALREADY_EXISTS_REASON,
                new Object[0],
                "Painting already exists(default)",
                locale);

        final var itemMessage = messageSource.getMessage(
                PAINTING_ALREADY_EXISTS_ITEM_MESSAGE,
                new String[]{ex.getTitle()},
                "Painting with title = [%s] already exists(default)".formatted(ex.getTitle()),
                locale);

        final var apiError = ApiError.builder()
                .apiVersion(apiVersion)
                .code(HttpStatus.CONFLICT.toString())
                .domain(request.getRequestURI())
                .reason(reason)
                .message(itemMessage)
                .build();

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_PROBLEM_JSON_VALUE);

        return new ResponseEntity<>(apiError, headers, HttpStatus.CONFLICT);

    }

}
