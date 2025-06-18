package org.rococo.tests.tests.web;

import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.rococo.tests.jupiter.annotation.ApiLogin;
import org.rococo.tests.jupiter.annotation.User;
import org.rococo.tests.jupiter.annotation.meta.WebTest;
import org.rococo.tests.model.UserDTO;
import org.rococo.tests.page.MainPage;
import org.rococo.tests.page.RegisterPage;
import org.rococo.tests.util.DataGenerator;

import javax.annotation.ParametersAreNonnullByDefault;

import static org.rococo.tests.util.DataGenerator.generatePassword;
import static org.rococo.tests.util.DataGenerator.generateUsername;

@WebTest
@Feature("WEB")
@Story("[WEB] Registration tests")
@DisplayName("[WEB] Registration tests")
@ParametersAreNonnullByDefault
class RegistrationWebTests {

    private static final String USERNAME_ALREADY_EXISTS_ERROR_MESSAGE_TPL = "Username = [%s] already exists",
            PASSWORDS_SHOULD_BE_EQUAL_MESSAGE = "Passwords should be equal";

    private final RegisterPage registerPage = new RegisterPage();
    private final MainPage mainPage = new MainPage();

    @Test
    @DisplayName("Should register if user credentials is valid")
    void shouldRegisterUserWithCorrectCredentialsTest() {
        // Data
        var user = DataGenerator.generateUser();

        // Steps
        registerPage.open()
                .register(user.getUsername(), user.getTestData().getPassword());

        // Assertions
        mainPage.shouldVisiblePage();
    }

    @Test
    @DisplayName("Should not register if password and confirmation password not equals")
    void shouldNotRegisterIfPasswordAndConfirmationPasswordNotEqualsTest() {
        // Steps
        registerPage.open()
                .registerWithError(generateUsername(), generatePassword(), generatePassword())

                // Assertions
                .shouldHaveConfirmationPasswordError(PASSWORDS_SHOULD_BE_EQUAL_MESSAGE);
    }


    @ParameterizedTest(name = "Case: [{0}]")
    @MethodSource("org.rococo.tests.tests.web.data.DataProvider#validRegistrationDataLength")
    @DisplayName("Check user creates if username length is valid")
    void shouldRegisterUserIfRegistrationDataIsValidTest(String caseName, int usernameLength, int passwordLength) {
        // Data
        var username = DataGenerator.generateUsername(usernameLength);
        var password = DataGenerator.generatePassword(passwordLength);

        // Steps
        registerPage.open()
                .register(username, password);

        // Assertions
        mainPage.shouldVisiblePage();
    }

    @ApiLogin(@User)
    @ParameterizedTest(name = "Case: [{0}]")
    @MethodSource("org.rococo.tests.tests.web.data.DataProvider#invalidRegistrationDataLength")
    @DisplayName("Check errors displayed if registration data has invalid length")
    void shouldDisplayErrorsIfRegistrationDataLengthIsInvalidTest(String caseName,
                                                                  int usernameLength,
                                                                  int passwordLength,
                                                                  String[] errors
    ) {
        var username = DataGenerator.generateUsername(usernameLength);
        var password = DataGenerator.generatePassword(passwordLength);

        // Steps
        registerPage.open()
                .registerWithError(username, password)

                // Assertions
                .shouldHaveErrors(errors);
    }

    @ApiLogin(@User)
    @ParameterizedTest(name = "Case: [{0}]")
    @MethodSource("org.rococo.tests.tests.web.data.DataProvider#invalidUsernamePattern")
    @DisplayName("Check errors displayed if username has invalid pattern")
    void shouldDisplayErrorsIfUsernameHasInvalidPatternTest(String caseName,
                                                            String username,
                                                            String usernameError
    ) {
        // Steps
        registerPage.open()
                .registerWithError(username, DataGenerator.generatePassword())

                // Assertions
                .shouldHaveUsernameError(usernameError);
    }

    @ApiLogin(@User)
    @ParameterizedTest(name = "Case: [{0}]")
    @MethodSource("org.rococo.tests.tests.web.data.DataProvider#invalidPasswordPattern")
    @DisplayName("Check errors displayed if password has invalid pattern")
    void shouldDisplayErrorsIfPasswordHasInvalidPatternTest(String caseName,
                                                            String password,
                                                            String passwordError
    ) {
        // Steps
        registerPage.open()
                .registerWithError(DataGenerator.generateUsername(), password)

                // Assertions
                .shouldHavePasswordError(passwordError);
    }

    @User
    @Test
    @DisplayName("Check displayed error if register by exists username")
    void shouldNotRegisterIfUsernameExistsTest(UserDTO user) {
        // Steps
        registerPage.open()
                .registerWithError(user.getUsername(), generatePassword())

                // Assertions
                .shouldHaveUsernameError(USERNAME_ALREADY_EXISTS_ERROR_MESSAGE_TPL.formatted(user.getUsername()));
    }

}
