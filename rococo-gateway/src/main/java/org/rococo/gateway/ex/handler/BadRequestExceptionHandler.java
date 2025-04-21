package org.rococo.gateway.ex.handler;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.rococo.gateway.ex.BadRequestException;
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
public class BadRequestExceptionHandler {

    private static final String SINGLE_ERROR = "errors.api.common.400.message.single_error";
    private static final String MULTIPLE_ERRORS = "errors.api.common.400.message.multiple_errors";

    private final MessageSource messageSource;

    @Value("${app.api.version}")
    private String apiVersion;

    @Nonnull
    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ApiError> handleBadRequestException(final BadRequestException ex,
                                                              final HttpServletRequest request,
                                                              final Locale locale
    ) {

        log.info("Bad request. uri: {}, message: {}", request.getRequestURI(), ex.getMessage());

        var errorItems = ex.getErrors().stream()
                .map(error -> new ApiError.ErrorItem(
                        request.getRequestURI(),
                        error.getField(),
                        "[%s] %s".formatted(error.getCode(), error.getDefaultMessage())))
                .toList();

        boolean isMultipleErrors = ex.getErrors().size() > 1;
        final String message = isMultipleErrors
                ? messageSource.getMessage(MULTIPLE_ERRORS, new Object[0], "Bad request. Multiple validation errors(default)", locale)
                : messageSource.getMessage(SINGLE_ERROR, new Object[0], "Bad request(default)", locale);

        ApiError apiError = ApiError.builderErrors()
                .apiVersion(apiVersion)
                .code(HttpStatus.BAD_REQUEST.toString())
                .message(message)
                .errorItems(errorItems)
                .buildErrors();

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_PROBLEM_JSON_VALUE);

        return new ResponseEntity<>(apiError, headers, HttpStatus.BAD_REQUEST);

    }

}
