package org.rococo.gateway.ex.handler;

import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.rococo.gateway.ex.BadRequestException;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.validation.FieldError;
import org.springframework.validation.MapBindingResult;

import java.util.Locale;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("BadRequestExceptionHandlerTests: Module test")
class BadRequestExceptionHandlerTests {

    private static final String API_VERSION = "0.0.1";
    private static final Locale LOCALE = Locale.ENGLISH;
    private static final String MULTIPLE_ERRORS = "errors.api.common.400.message.multiple_errors";
    private static final String INVALID_ERROR_1 = "invalid error 1";
    private static final String INVALID_ERROR_2 = "invalid error 2";

    @Mock
    private HttpServletRequest request;

    @Mock
    private MessageSource messageSource;

    @InjectMocks
    private BadRequestExceptionHandler handler;

    @Test
    @DisplayName("HandleBadRequestException: returns BAD_REQUEST")
    void handleBadRequestException_ReturnsBadRequest_Test() {

        // Data
        final var requestUrl = "/api/artist";
        final var bindingResult = new MapBindingResult(Map.of(), "request");
        bindingResult.addError(new FieldError("request", "name", null, true, new String[]{"TEST_VALIDATOR"}, null, INVALID_ERROR_1));
        bindingResult.addError(new FieldError("request", "code", null, true, new String[]{"TEST_VALIDATOR"}, null, INVALID_ERROR_2));
        final var exception = new BadRequestException(bindingResult.getFieldErrors());
        final var multipleErrorsText = "Bad request. Multiple validation errors(default)";

        // Mock
        ReflectionTestUtils.setField(handler, "apiVersion", API_VERSION);
        when(request.getRequestURI())
                .thenReturn(requestUrl);
        when(messageSource.getMessage(MULTIPLE_ERRORS, new Object[0], multipleErrorsText, LOCALE))
                .thenReturn(multipleErrorsText);

        // Steps
        final var result = handler.handleBadRequestException(exception, request, LOCALE);

        // Assertions
        final var responseBody = result.getBody();
        assertAll(
                () -> assertNotNull(result),
                () -> assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode()),
                () -> assertEquals(API_VERSION, responseBody.getApiVersion()),
                () -> assertEquals(multipleErrorsText, responseBody.getError().message()),
                () -> assertEquals(requestUrl, responseBody.getError().errors().getFirst().domain()),
                () -> assertEquals("[TEST_VALIDATOR] " + INVALID_ERROR_1, responseBody.getError().errors().getFirst().itemMessage()),
                () -> assertEquals("[TEST_VALIDATOR] " + INVALID_ERROR_2, responseBody.getError().errors().getLast().itemMessage())
        );

        verifyNoMoreInteractions(request);

    }

}
