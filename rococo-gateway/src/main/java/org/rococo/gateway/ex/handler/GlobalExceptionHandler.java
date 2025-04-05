package org.rococo.gateway.ex.handler;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.rococo.gateway.model.ApiError;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Locale;

@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    private static final String INTERNAL_ERROR_REASON = "errors.api.services.500.reason";

    private final MessageSource messageSource;

    @Value("${app.api.version}")
    private String apiVersion;

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleGlobalException(Exception ex,
                                                          HttpServletRequest request,
                                                          Locale locale
    ) {

        log.error("INTERNAL SERVER ERROR. {}: uri = {}, itemMessage = {}", request.getMethod(), request.getRequestURI(), ex.getMessage());

        final var reason = messageSource.getMessage(
                INTERNAL_ERROR_REASON,
                new Object[0],
                "INTERNAL SERVER ERROR(default)",
                locale
        );

        final var message = ex.getMessage() != null
                ? ex.getMessage()
                : ex.getCause().getMessage();

        final var apiError = ApiError.builder()
                .apiVersion(apiVersion)
                .code(HttpStatus.INTERNAL_SERVER_ERROR.toString())
                .domain(request.getRequestURI())
                .reason(reason)
                .message(message)
                .build();

        return new ResponseEntity<>(
                apiError,
                HttpStatus.INTERNAL_SERVER_ERROR
        );
    }
}