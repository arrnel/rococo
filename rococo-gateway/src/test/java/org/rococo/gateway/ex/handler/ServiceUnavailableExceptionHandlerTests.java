package org.rococo.gateway.ex.handler;

import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.rococo.gateway.ex.ServiceUnavailableException;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Locale;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("ServiceUnavailableExceptionHandlerTests: Module test")
class ServiceUnavailableExceptionHandlerTests {

    private static final String SERVICE_UNAVAILABLE_REASON = "errors.api.services.503.reason";
    private static final String SERVICE_UNAVAILABLE_ITEM_MESSAGE = "errors.api.services.503.item_message";
    private static final String API_VERSION = "0.0.1";
    private static final Locale LOCALE = Locale.ENGLISH;

    @Mock
    private HttpServletRequest request;

    @Mock
    private MessageSource messageSource;

    @InjectMocks
    private ServiceUnavailableExceptionHandler handler;

    @Test
    @DisplayName("HandleServiceUnavailableException: returns SERVICE_UNAVAILABLE")
    void handleServiceUnavailableException_ReturnsServiceUnavailable_Test() {

        // Data
        final var serviceName = "test-service";
        final var requestUrl = "/api/test";
        final var message = "Service unavailable";
        final var reason = "Service [%s] not available".formatted(serviceName);
        final var exception = new ServiceUnavailableException(serviceName);

        // Mock
        ReflectionTestUtils.setField(handler, "apiVersion", API_VERSION);
        when(request.getRequestURI())
                .thenReturn(requestUrl);
        when(messageSource.getMessage(SERVICE_UNAVAILABLE_REASON, new Object[0], "Service unavailable(default)", LOCALE))
                .thenReturn(message);
        when(messageSource.getMessage(SERVICE_UNAVAILABLE_ITEM_MESSAGE, new String[]{serviceName},
                "Service [%s] not available(default)".formatted(serviceName), LOCALE))
                .thenReturn(reason);

        // Steps
        final var result = handler.handleServiceUnavailableException(exception, request, LOCALE);

        // Assertions
        assertAll(
                () -> assertNotNull(result),
                () -> assertEquals(HttpStatus.SERVICE_UNAVAILABLE, result.getStatusCode()),
                () -> assertEquals(MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                        result.getHeaders().getFirst(HttpHeaders.CONTENT_TYPE)),
                () -> assertEquals(API_VERSION, result.getBody().getApiVersion()),
                () -> assertEquals(message, result.getBody().getError().message()),
                () -> assertEquals(reason, result.getBody().getError().errors().getFirst().reason()),
                () -> assertEquals(requestUrl, result.getBody().getError().errors().getFirst().domain()),
                () -> assertNull(result.getBody().getError().errors().getFirst().itemMessage())
        );

        verifyNoMoreInteractions(request, messageSource);

    }

}
