package org.rococo.gateway.ex.handler;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.rococo.gateway.ex.CountryNotFoundException;
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
public class CountriesExceptionHandler {

    private static final String COUNTRY_NOT_FOUND_REASON = "errors.api.countries.404.reason";
    private static final String COUNTRY_NOT_FOUND_ITEM_MESSAGE = "errors.api.countries.404.item_message";

    private final MessageSource messageSource;

    @Value("${app.api.version}")
    private String apiVersion;

    @Nonnull
    @ExceptionHandler(CountryNotFoundException.class)
    public ResponseEntity<ApiError> handleCountryNotFoundException(final CountryNotFoundException ex,
                                                                   final HttpServletRequest request,
                                                                   final Locale locale
    ) {

        log.error("Country with id = [{}] not found. uri: {}, message: {}", ex.getId(), request.getRequestURI(), ex.getMessage());

        final String reason = messageSource.getMessage(
                COUNTRY_NOT_FOUND_REASON,
                new Object[0],
                "Country not found(default)",
                locale);

        final String message = messageSource.getMessage(
                COUNTRY_NOT_FOUND_ITEM_MESSAGE,
                new String[]{ex.getId().toString()},
                "Country with id = [%s] not found(default)".formatted(ex.getId().toString()),
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

}
