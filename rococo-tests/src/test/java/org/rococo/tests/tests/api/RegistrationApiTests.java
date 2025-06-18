package org.rococo.tests.tests.api;

import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import net.datafaker.Faker;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.rococo.tests.client.gateway.AuthApiClient;
import org.rococo.tests.client.gateway.core.store.ThreadSafeCookieStore;
import org.rococo.tests.enums.CookieType;
import org.rococo.tests.enums.HttpStatus;
import org.rococo.tests.jupiter.annotation.User;
import org.rococo.tests.jupiter.annotation.meta.ApiTest;
import org.rococo.tests.model.UserDTO;
import org.rococo.tests.util.DataGenerator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ApiTest
@Feature("API")
@Story("[API] Registration tests")
@DisplayName("[API] Registration tests")
class RegistrationApiTests {

    private final AuthApiClient authApi = new AuthApiClient();

    @Test
    @DisplayName("Can register user with valid data")
    void canRegisterUserWithValidDataTest() {

        // Data
        var user = DataGenerator.generateUser();

        // Steps
        var result = authApi.register(user.getUsername(), user.password());

        // Assertions
        assertNotNull(result.getId());

    }

    @Test
    @DisplayName("Should returns BAD_REQUEST if password and confirmPassword mismatch")
    void shouldReturnBadRequestIfPasswordAndConfirmPasswordMismatchTest() {

        // Data
        var user = DataGenerator.generateUser();

        // Steps
        authApi.sendGetCookiesRequest();
        var result = authApi.sendRegisterUserRequest(
                user.getUsername(), user.password(), DataGenerator.generatePassword(), ThreadSafeCookieStore.INSTANCE.cookieValue(CookieType.CSRF.getCookieName())
        );

        // Assertions
        assertEquals(HttpStatus.BAD_REQUEST, result.code());

    }

    @ParameterizedTest(name = "Case: username length = {0}")
    @ValueSource(ints = {0, 5, 31})
    @DisplayName("Should returns BAD_REQUEST if register with invalid length username")
    void shouldReturnBadRequestIfUsernameLengthInvalidTest(int usernameLength) {

        // Data
        var user = DataGenerator.generateUser();
        user.setUsername(DataGenerator.generateUsername(usernameLength));

        // Steps
        authApi.sendGetCookiesRequest();
        var result = authApi.sendRegisterUserRequest(
                user.getUsername(), user.password(), user.password(), ThreadSafeCookieStore.INSTANCE.cookieValue(CookieType.CSRF.getCookieName())
        );

        // Assertions
        assertEquals(HttpStatus.BAD_REQUEST, result.code());

    }

    @ParameterizedTest(name = "Case: password length = {0}")
    @ValueSource(ints = {0, 4, 21})
    @DisplayName("Should returns BAD_REQUEST if register with invalid length password")
    void shouldReturnBadRequestIfPasswordLengthInvalidTest(int passwordLength) {

        // Data
        var user = DataGenerator.generateUser();
        user.getTestData().setPassword(DataGenerator.generatePassword(passwordLength));

        // Steps
        authApi.sendGetCookiesRequest();
        var result = authApi.sendRegisterUserRequest(
                user.getUsername(), user.password(), user.password(), ThreadSafeCookieStore.INSTANCE.cookieValue(CookieType.CSRF.getCookieName())
        );

        // Assertions
        assertEquals(HttpStatus.BAD_REQUEST, result.code());

    }

    @Test
    @DisplayName("Should returns BAD_REQUEST if register with weak password")
    void shouldReturnBadRequestIfPasswordIsWeakTest() {

        // Data
        var user = DataGenerator.generateUser();
        user.getTestData().setPassword(new Faker().lorem().characters(6, 30, false, false, true).toLowerCase());

        // Steps
        authApi.sendGetCookiesRequest();
        var result = authApi.sendRegisterUserRequest(
                user.getUsername(), user.password(), user.password(), ThreadSafeCookieStore.INSTANCE.cookieValue(CookieType.CSRF.getCookieName())
        );

        // Assertions
        assertEquals(HttpStatus.BAD_REQUEST, result.code());

    }

    @User
    @Test
    @DisplayName("Should returns BAD_REQUEST if register with exists username")
    void shouldReturnBadRequestIfRegisterWithExistsUsernameTest(UserDTO user) {

        // Clear all cookies before test
        ThreadSafeCookieStore.INSTANCE.removeAll();

        // Steps
        authApi.sendGetCookiesRequest();
        var result = authApi.sendRegisterUserRequest(
                user.getUsername(), user.password(), DataGenerator.generatePassword(), ThreadSafeCookieStore.INSTANCE.cookieValue(CookieType.CSRF.getCookieName())
        );

        // Assertions
        assertEquals(HttpStatus.BAD_REQUEST, result.code());

    }

}
