package org.rococo.gateway.ex.handler;

import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.rococo.gateway.ex.CurrentUserNotFoundException;
import org.rococo.gateway.ex.UserAlreadyExistsException;
import org.rococo.gateway.ex.UserNotFoundException;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Locale;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("UsersExceptionHandlerTests: Module test")
class UsersExceptionHandlerTests {

    private static final String USER_NOT_FOUND_REASON = "errors.api.users.404.reason";
    private static final String USER_NOT_FOUND_BY_ID_ITEM_MESSAGE = "errors.api.users.404.by_id.item_message";
    private static final String CURRENT_USER_NOT_FOUND_ITEM_MESSAGE = "errors.api.users.404.by_username.item_message";

    private static final String USER_ALREADY_EXISTS_REASON = "errors.api.users.409.reason";
    private static final String USER_ALREADY_EXISTS_ITEM_MESSAGE = "errors.api.users.409.item_message";


    private static final String API_VERSION = "0.0.1";
    private static final Locale LOCALE = Locale.ENGLISH;

    @Mock
    private HttpServletRequest request;

    @Mock
    private MessageSource messageSource;

    @InjectMocks
    private UsersExceptionHandler handler;

    @Test
    @DisplayName("HandleUserNotFoundException: returns NOT_FOUND")
    void handleUserNotFoundException_ReturnsNotFound_Test() {

        // Data
        final var userId = UUID.randomUUID();
        final var requestUrl = "/api/user/" + userId;
        final var message = "User not found";
        final var reason = "User with id = [%s] not found".formatted(userId);
        final var exception = new UserNotFoundException(userId);

        // Mock
        ReflectionTestUtils.setField(handler, "apiVersion", API_VERSION);
        when(request.getRequestURI())
                .thenReturn(requestUrl);
        when(messageSource.getMessage(USER_NOT_FOUND_REASON, new Object[0], "User not found(default)", LOCALE))
                .thenReturn(reason);
        when(messageSource.getMessage(USER_NOT_FOUND_BY_ID_ITEM_MESSAGE, new String[]{userId.toString()}, "User with id = [%s] not found(default)".formatted(userId), LOCALE))
                .thenReturn(message);

        // Steps
        final var result = handler.handleUserNotFoundException(exception, request, LOCALE);

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
    @DisplayName("HandleCurrentUserNotFoundException: returns NOT_FOUND")
    void handleCurrentUserNotFoundException_ReturnsNotFound_Test() {

        // Data
        final var username = "test_user";
        final var requestUrl = "/api/user/";
        final var message = "User not found";
        final var reason = "User with username = [%s] not found".formatted(username);
        final var exception = new CurrentUserNotFoundException(username);

        // Mock
        ReflectionTestUtils.setField(handler, "apiVersion", API_VERSION);
        when(request.getRequestURI())
                .thenReturn(requestUrl);
        when(messageSource.getMessage(USER_NOT_FOUND_REASON, new Object[0], "User not found(default)", LOCALE))
                .thenReturn(reason);
        when(messageSource.getMessage(CURRENT_USER_NOT_FOUND_ITEM_MESSAGE, new String[]{username}, "User with username = [%s] not found(default)".formatted(username), LOCALE))
                .thenReturn(message);

        // Steps
        final var result = handler.handleCurrentUserNotFoundException(exception, request, LOCALE);

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
    @DisplayName("HandleUserAlreadyExistsException: returns CONFLICT")
    void handleUserAlreadyExistsException_ReturnsConflict_Test() {

        // Data
        final var userTitle = "test_user";
        final var requestUrl = "/api/user";
        final var message = "User already exists";
        final var reason = "User with username = [%s] already exists".formatted(userTitle);
        final var exception = new UserAlreadyExistsException(userTitle);

        // Mock
        ReflectionTestUtils.setField(handler, "apiVersion", API_VERSION);
        when(request.getRequestURI())
                .thenReturn(requestUrl);
        when(messageSource.getMessage(USER_ALREADY_EXISTS_REASON, new Object[0], "User already exists(default)", LOCALE))
                .thenReturn(reason);
        when(messageSource.getMessage(USER_ALREADY_EXISTS_ITEM_MESSAGE, new String[]{userTitle}, "User with username = [%s] already exists(default)".formatted(userTitle), LOCALE))
                .thenReturn(message);

        // Steps
        final var result = handler.handleUserAlreadyExistsException(exception, request, LOCALE);

        // Assertions
        assertAll(
                () -> assertNotNull(result),
                () -> assertEquals(HttpStatus.CONFLICT, result.getStatusCode()),
                () -> assertEquals(API_VERSION, result.getBody().getApiVersion()),
                () -> assertEquals(message, result.getBody().getError().errors().getFirst().itemMessage()),
                () -> assertEquals(requestUrl, result.getBody().getError().errors().getFirst().domain()),
                () -> assertEquals(reason, result.getBody().getError().errors().getFirst().reason())
        );

        verifyNoMoreInteractions(request);

    }

}
