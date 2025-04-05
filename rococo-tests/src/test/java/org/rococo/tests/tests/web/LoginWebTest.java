package org.rococo.tests.tests.web;

import com.codeborne.selenide.Selenide;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import net.datafaker.Faker;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.rococo.tests.jupiter.annotation.User;
import org.rococo.tests.jupiter.annotation.ApiLogin;
import org.rococo.tests.jupiter.annotation.meta.WebTest;
import org.rococo.tests.model.UserDTO;
import org.rococo.tests.page.MainPage;
import org.rococo.tests.page.component.NotificationComponent.NotificationStatus;
import org.rococo.tests.util.DataGenerator;

import javax.annotation.ParametersAreNonnullByDefault;

import static com.codeborne.selenide.Selenide.open;

@WebTest
@Feature("WEB")
@Story("[Web] Login tests")
@DisplayName("[WEB] Login tests")
@ParametersAreNonnullByDefault
class LoginWebTest {

    private static final String BAD_CREDENTIALS_ERROR_MESSAGE = "Неверные учетные данные пользователя";
    private static final String SESSION_TIMEOUT_MESSAGE = "Сессия завершена";

    private static final Faker FAKE = new Faker();

    @User
    @Test
    @DisplayName("[WEB] Should login by correct credentials")
    void shouldLoginWithCorrectCredentialsTest(UserDTO user) {
        open(MainPage.URL, MainPage.class)
                .getHeader()
                .goToSignInPage()
                .login(user.getUsername(), user.getTestData().getPassword())
                .shouldVisiblePage();
    }

    @User
    @Test
    @DisplayName("[WEB] Should not login by incorrect credentials")
    void shouldDisplayBadCredentialsErrorIfPasswordIsIncorrectTest(UserDTO user) {
        open(MainPage.URL, MainPage.class)
                .getHeader()
                .goToSignInPage()
                .loginWithError(user.getUsername(), DataGenerator.generatePassword())
                .shouldHaveErrorMessage(BAD_CREDENTIALS_ERROR_MESSAGE);
    }

    @Test
    @DisplayName("[WEB] Should display bad credentials error, if sign in with unknown username")
    void shouldDisplayBadCredentialsErrorIfUsernameUnknownTest() {
        open(MainPage.URL, MainPage.class)
                .getHeader()
                .goToSignInPage()
                .loginWithError(FAKE.internet().username(), DataGenerator.generatePassword())
                .shouldHaveErrorMessage(BAD_CREDENTIALS_ERROR_MESSAGE);
    }

    @ApiLogin(@User)
    @Test
    @DisplayName("[WEB] Should log out")
    void canLogOutTest() {
        open(MainPage.URL, MainPage.class)
                .getHeader()
                .signOut()
                .shouldVisibleNotification(NotificationStatus.INFO, SESSION_TIMEOUT_MESSAGE);

        Selenide.refresh();

        new MainPage().getHeader()
                .shouldVisibleLoginButton();
    }

}
