package org.rococo.gateway.ex.handler;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.rococo.gateway.ex.ServiceUnavailableException;
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
import java.util.List;
import java.util.Locale;

@Slf4j
@ControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
@RequiredArgsConstructor
@ParametersAreNonnullByDefault
public class ServiceUnavailableExceptionHandler {

    private static final String SERVICE_UNAVAILABLE_REASON = "errors.api.services.503.reason";
    private static final String SERVICE_UNAVAILABLE_ITEM_MESSAGE = "errors.api.services.503.item_message";

    private final MessageSource messageSource;

    @Value("${app.api.version}")
    private String apiVersion;

    @Nonnull
    @ExceptionHandler(ServiceUnavailableException.class)
    public ResponseEntity<ApiError> handleServiceUnavailableException(final ServiceUnavailableException ex,
                                                                      final HttpServletRequest request,
                                                                      final Locale locale
    ) {

        log.error("Service [{}] not available. Details: {}", ex.getServiceName(), ex.getMessage());

        final String message = messageSource.getMessage(
                SERVICE_UNAVAILABLE_REASON,
                new Object[0],
                "Service unavailable(default)",
                locale);

        final String reason = messageSource.getMessage(
                SERVICE_UNAVAILABLE_ITEM_MESSAGE,
                new String[]{ex.getServiceName()},
                "Service [%s] not available(default)".formatted(ex.getServiceName()),
                locale);

        final var apiError = ApiError.builderErrors()
                .apiVersion(apiVersion)
                .code(HttpStatus.SERVICE_UNAVAILABLE.toString())
                .message(message)
                .errorItems(
                        List.of(
                                new ApiError.ErrorItem(
                                        request.getRequestURI(),
                                        reason,
                                        (ex.getCause() == null)
                                                ? null
                                                : ex.getCause().getMessage())))
                .buildErrors();

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_PROBLEM_JSON_VALUE);

        return new ResponseEntity<>(apiError, headers, HttpStatus.SERVICE_UNAVAILABLE);

    }

}
