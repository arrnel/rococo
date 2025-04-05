package org.rococo.gateway.ex.handler;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.rococo.gateway.ex.CurrentUserNotFoundException;
import org.rococo.gateway.ex.UserAlreadyExistException;
import org.rococo.gateway.ex.UserNotFoundException;
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
public class UsersExceptionHandler {

    private static final String USER_NOT_FOUND_REASON = "errors.api.users.404.reason";
    private static final String USER_NOT_FOUND_ITEM_MESSAGE = "errors.api.users.404.by_id.item_message";
    private static final String CURRENT_USER_NOT_FOUND_ITEM_MESSAGE = "errors.api.users.404.by_username.item_message";

    private static final String USER_ALREADY_EXISTS_REASON = "errors.api.users.409.reason";
    private static final String USER_ALREADY_EXISTS_ITEM_MESSAGE = "errors.api.users.409.item_message";

    private final MessageSource messageSource;

    @Value("${app.api.version}")
    private String apiVersion;

    @Nonnull
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ApiError> handleUserNotFoundException(final UserNotFoundException ex,
                                                                final HttpServletRequest request,
                                                                final Locale locale
    ) {

        log.error("User with id = [{}] not found. uri: {}, message: {}", ex.getId(), request.getRequestURI(), ex.getMessage());

        final String reason = messageSource.getMessage(
                USER_NOT_FOUND_REASON,
                new Object[0],
                "User not found(default)",
                locale);

        final String message = messageSource.getMessage(
                USER_NOT_FOUND_ITEM_MESSAGE,
                new String[]{ex.getId().toString()},
                "User with id = [%s] not found(default)".formatted(ex.getId().toString()),
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
    @ExceptionHandler(CurrentUserNotFoundException.class)
    public ResponseEntity<ApiError> handleCurrentUserNotFoundException(final CurrentUserNotFoundException ex,
                                                                       final HttpServletRequest request,
                                                                       final Locale locale
    ) {

        log.error("User with username = [{}] not found. uri: {}, message: {}", ex.getUsername(), request.getRequestURI(), ex.getMessage());

        final String reason = messageSource.getMessage(
                USER_NOT_FOUND_REASON,
                new Object[0],
                "User not found(default)",
                locale);

        final String message = messageSource.getMessage(
                CURRENT_USER_NOT_FOUND_ITEM_MESSAGE,
                new String[]{ex.getUsername()},
                "User with username = [%s] not found(default)".formatted(ex.getUsername()),
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
    @ExceptionHandler(UserAlreadyExistException.class)
    public ResponseEntity<ApiError> handleUserAlreadyExistsException(final UserAlreadyExistException ex,
                                                                     final HttpServletRequest request,
                                                                     final Locale locale
    ) {

        log.error("User with name = [{}] already exists. uri: {}, itemMessage: {}", ex.getUsername(), request.getRequestURI(), ex.getMessage());

        final var reason = messageSource.getMessage(
                USER_ALREADY_EXISTS_REASON,
                new Object[0],
                "User already exists(default)",
                locale);

        final var itemMessage = messageSource.getMessage(
                USER_ALREADY_EXISTS_ITEM_MESSAGE,
                new String[]{ex.getUsername()},
                "User with username = [%s] already exists(default)".formatted(ex.getUsername()),
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
