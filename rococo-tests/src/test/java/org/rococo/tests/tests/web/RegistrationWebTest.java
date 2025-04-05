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

import static com.codeborne.selenide.Selenide.open;
import static org.rococo.tests.util.DataGenerator.generatePassword;
import static org.rococo.tests.util.DataGenerator.generateUsername;

@WebTest
@Feature("WEB")
@Story("[WEB] Registration tests")
@DisplayName("[WEB] Registration tests")
@ParametersAreNonnullByDefault
class RegistrationWebTest {

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
        open(RegisterPage.URL, RegisterPage.class)
                .register(user.getUsername(), user.getTestData().getPassword());

        // Assertions
        mainPage.shouldVisiblePage();
    }

    @Test
    @DisplayName("Should not register if password and confirmation password not equals")
    void shouldNotRegisterIfPasswordAndConfirmationPasswordNotEqualsTest() {
        // Steps
        open(RegisterPage.URL, RegisterPage.class)
                .registerWithError(generateUsername(), generatePassword(), generatePassword());

        // Assertions
        registerPage.shouldHaveConfirmationPasswordError(PASSWORDS_SHOULD_BE_EQUAL_MESSAGE);
    }


    @ParameterizedTest(name = "Case: [{0}]")
    @MethodSource("org.rococo.tests.tests.web.data.DataProvider#validRegistrationData")
    @DisplayName("Check user creates if username length is valid")
    void shouldRegisterUserIfRegistrationDataIsValidTest(String caseName, String username, String password) {
        // Steps
        open(RegisterPage.URL, RegisterPage.class)
                .register(username, password);

        // Assertions
        mainPage.shouldVisiblePage();
    }

    @ApiLogin(@User)
    @ParameterizedTest(name = "Case: [{0}]")
    @MethodSource("org.rococo.tests.tests.web.data.DataProvider#invalidRegistrationData")
    @DisplayName("Check errors displayed if registration data is invalid")
    void shouldDisplayErrorsIfRegistrationDataIsInvalidTest(String caseName, String username, String password, String[] errors) {
        // Steps
        open(RegisterPage.URL, RegisterPage.class)
                .registerWithError(username, password);

        // Assertions
        registerPage.shouldHaveErrors(errors);
    }

    @User
    @Test
    @DisplayName("Check displayed error if register by exists username")
    void shouldNotRegisterIfUsernameExistsTest(UserDTO user) {
        open(RegisterPage.URL, RegisterPage.class)
                .registerWithError(user.getUsername(), generatePassword())
                .shouldHaveUsernameError(USERNAME_ALREADY_EXISTS_ERROR_MESSAGE_TPL.formatted(user.getUsername()));
    }

}
