package org.rococo.gateway.ex.handler;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.rococo.gateway.ex.MuseumAlreadyExistsException;
import org.rococo.gateway.ex.MuseumNotFoundException;
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
public class MuseumsExceptionHandler {

    private static final String MUSEUM_NOT_FOUND_REASON = "errors.api.museums.404.reason";
    private static final String MUSEUM_NOT_FOUND_ITEM_MESSAGE = "errors.api.museums.404.item_message";

    private static final String MUSEUM_ALREADY_EXISTS_REASON = "errors.api.museums.409.reason";
    private static final String MUSEUM_ALREADY_EXISTS_ITEM_MESSAGE = "errors.api.museums.409.item_message";

    private final MessageSource messageSource;

    @Value("${app.api.version}")
    private String apiVersion;

    @Nonnull
    @ExceptionHandler(MuseumNotFoundException.class)
    public ResponseEntity<ApiError> handleMuseumNotFoundException(final MuseumNotFoundException ex,
                                                                  final HttpServletRequest request,
                                                                  final Locale locale
    ) {

        log.error("Museum with id = [{}] not found. uri: {}, message: {}", ex.getMessage(), request.getRequestURI(), ex.getMessage());

        final String reason = messageSource.getMessage(
                MUSEUM_NOT_FOUND_REASON,
                new Object[0],
                "Museum not found(default)",
                locale);

        final String message = messageSource.getMessage(
                MUSEUM_NOT_FOUND_ITEM_MESSAGE,
                new String[]{ex.getId().toString()},
                "Museum with id = [%s] not found(default)".formatted(ex.getId().toString()),
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
    @ExceptionHandler(MuseumAlreadyExistsException.class)
    public ResponseEntity<ApiError> handleMuseumAlreadyExistsException(final MuseumAlreadyExistsException ex,
                                                                       final HttpServletRequest request,
                                                                       final Locale locale
    ) {

        log.error("Museum with title = [{}] already exists. uri: {}, itemMessage: {}", ex.getTitle(), request.getRequestURI(), ex.getMessage());

        final var reason = messageSource.getMessage(
                MUSEUM_ALREADY_EXISTS_REASON,
                new Object[0],
                "Museum already exists(default)",
                locale);

        final var itemMessage = messageSource.getMessage(
                MUSEUM_ALREADY_EXISTS_ITEM_MESSAGE,
                new String[]{ex.getTitle()},
                "Museum with title = [%s] already exists(default)".formatted(ex.getTitle()),
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
