package org.rococo.gateway.ex.handler;

import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.rococo.gateway.ex.CountryNotFoundException;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Locale;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("CountriesExceptionHandlerTests: Module test")
class CountriesExceptionHandlerTests {

    private static final String COUNTRY_NOT_FOUND_REASON = "errors.api.countries.404.reason";
    private static final String COUNTRY_NOT_FOUND_BY_ID_ITEM_MESSAGE = "errors.api.countries.404.by_id.item_message";
    private static final String COUNTRY_NOT_FOUND_BY_CODE_ITEM_MESSAGE = "errors.api.countries.404.by_code.item_message";

    private static final String API_VERSION = "0.0.1";
    private static final Locale LOCALE = Locale.ENGLISH;

    @Mock
    private HttpServletRequest request;

    @Mock
    private MessageSource messageSource;

    @InjectMocks
    private CountriesExceptionHandler handler;

    @Test
    @DisplayName("HandleCountryNotFoundException: returns NOT_FOUND and if country not found by id")
    void handleCountryNotFoundException_IfReturnsNotFound_Test() {

        // Data
        final var countryId = UUID.randomUUID();
        final var requestUrl = "/api/country/" + countryId;
        final var message = "Country not found";
        final var reason = "Country with id = [%s] not found".formatted(countryId);
        final var exception = new CountryNotFoundException(countryId);

        // Mock
        ReflectionTestUtils.setField(handler, "apiVersion", API_VERSION);
        when(request.getRequestURI())
                .thenReturn(requestUrl);
        when(messageSource.getMessage(COUNTRY_NOT_FOUND_REASON, new Object[0], "Country not found(default)", LOCALE))
                .thenReturn(reason);
        when(messageSource.getMessage(COUNTRY_NOT_FOUND_BY_ID_ITEM_MESSAGE, new String[]{countryId.toString()}, "Country with id = [%s] not found(default)".formatted(countryId), LOCALE))
                .thenReturn(message);

        // Steps
        final var result = handler.handleCountryNotFoundException(exception, request, LOCALE);

        // Assertions
        assertAll(
                () -> assertNotNull(result),
                () -> assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode()),
                () -> assertEquals(API_VERSION, result.getBody().getApiVersion()),
                () -> assertEquals(message, result.getBody().getError().errors().getFirst().itemMessage()),
                () -> assertEquals(requestUrl, result.getBody().getError().errors().getFirst().domain()),
                () -> assertEquals(reason, result.getBody().getError().errors().getFirst().reason())
        );

        verifyNoMoreInteractions(request);

    }

    @Test
    @DisplayName("HandleCountryNotFoundException: returns NOT_FOUND")
    void handleCountryNotFoundException_ReturnsNotFound_Test() {

        // Data
        final var countryCode = "JP";
        final var requestUrl = "/api/country/code/" + countryCode;
        final var message = "Country not found";
        final var reason = "Country with code = [%s] not found".formatted(countryCode);
        final var exception = new CountryNotFoundException(countryCode);

        // Mock
        ReflectionTestUtils.setField(handler, "apiVersion", API_VERSION);
        when(request.getRequestURI())
                .thenReturn(requestUrl);
        when(messageSource.getMessage(COUNTRY_NOT_FOUND_REASON, new Object[0], "Country not found(default)", LOCALE))
                .thenReturn(reason);
        when(messageSource.getMessage(COUNTRY_NOT_FOUND_BY_CODE_ITEM_MESSAGE, new String[]{countryCode}, "Country with code = [%s] not found(default)".formatted(countryCode), LOCALE))
                .thenReturn(message);

        // Steps
        final var result = handler.handleCountryNotFoundException(exception, request, LOCALE);

        // Assertions
        assertAll(
                () -> assertNotNull(result),
                () -> assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode()),
                () -> assertEquals(API_VERSION, result.getBody().getApiVersion()),
                () -> assertEquals(message, result.getBody().getError().errors().getFirst().itemMessage()),
                () -> assertEquals(requestUrl, result.getBody().getError().errors().getFirst().domain()),
                () -> assertEquals(reason, result.getBody().getError().errors().getFirst().reason())
        );

        verifyNoMoreInteractions(request);

    }

}
