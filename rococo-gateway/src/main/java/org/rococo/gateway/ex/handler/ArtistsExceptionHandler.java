package org.rococo.gateway.ex.handler;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.rococo.gateway.ex.ArtistAlreadyExistException;
import org.rococo.gateway.ex.ArtistNotFoundException;
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
public class ArtistsExceptionHandler {

    private static final String ARTIST_NOT_FOUND_REASON = "errors.api.artists.404.reason";
    private static final String ARTIST_NOT_FOUND_ITEM_MESSAGE = "errors.api.artists.404.item_message";

    private static final String ARTIST_ALREADY_EXISTS_REASON = "errors.api.artists.409.reason";
    private static final String ARTIST_ALREADY_EXISTS_ITEM_MESSAGE = "errors.api.artists.409.item_message";

    private final MessageSource messageSource;

    @Value("${app.api.version}")
    private String apiVersion;

    @Nonnull
    @ExceptionHandler(ArtistNotFoundException.class)
    public ResponseEntity<ApiError> handleArtistNotFoundException(final ArtistNotFoundException ex,
                                                                  final HttpServletRequest request,
                                                                  final Locale locale
    ) {

        log.error("Artist with id = [{}] not found. uri: {}, message: {}", ex.getId(), request.getRequestURI(), ex.getMessage());

        final String reason = messageSource.getMessage(
                ARTIST_NOT_FOUND_REASON,
                new Object[0],
                "Artist not found(default)",
                locale);

        final String message = messageSource.getMessage(
                ARTIST_NOT_FOUND_ITEM_MESSAGE,
                new String[]{ex.getId().toString()},
                "Artist with id = [%s] not found(default)".formatted(ex.getId().toString()),
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
    @ExceptionHandler(ArtistAlreadyExistException.class)
    public ResponseEntity<ApiError> handleArtistAlreadyExistsException(final ArtistAlreadyExistException ex,
                                                                       final HttpServletRequest request,
                                                                       final Locale locale
    ) {

        log.error("Artist with name = [{}] already exists. uri: {}, itemMessage: {}", ex.getName(), request.getRequestURI(), ex.getMessage());

        final var reason = messageSource.getMessage(
                ARTIST_ALREADY_EXISTS_REASON,
                new Object[0],
                "Artist already exists(default)",
                locale);

        final var itemMessage = messageSource.getMessage(
                ARTIST_ALREADY_EXISTS_ITEM_MESSAGE,
                new String[]{ex.getName()},
                "Artist with name = [%s] already exists(default)".formatted(ex.getName()),
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
