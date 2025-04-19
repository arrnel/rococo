package org.rococo.tests.page;

import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;
import lombok.extern.slf4j.Slf4j;
import org.rococo.tests.page.component.HeaderComponent;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selectors.*;
import static com.codeborne.selenide.Selenide.$;

@Slf4j
@ParametersAreNonnullByDefault
public class LoginPage extends BasePage<LoginPage> {

    public static final String URL = AUTH_URL + "/login";

    private final SelenideElement usernameInput = root.$(byName("username")).as("'Username' input"),
            passwordInput = root.$(byName("password")).as("'Password' input"),
            submitButton = root.$(byAttribute("type", "submit")).as("Submit button"),
            registerLink = root.$(byText("Зарегистрироваться")).as("Register link"),
            image = $("section img").as("Login page image"),
            error = $(".login__error").as("Login error message");

    @Nonnull
    @Step("Sign in by username = [{username}] and password = [{password}]")
    public MainPage login(String username, String password) {
        log.info("Login by username = [{}] and password [{}]", username, password);
        usernameInput.setValue(username);
        passwordInput.setValue(password);
        submitButton.click();
        return new MainPage();
    }

    @Nonnull
    @Step("Sign in with error by username = [{username}] and password = [{password}]")
    public LoginPage loginWithError(String username, String password) {
        log.info("Login by username = [{}] and password [{}]", username, password);
        usernameInput.setValue(username);
        passwordInput.setValue(password);
        submitButton.click();
        return this;
    }

    @Nonnull
    @Step("Go to register page")
    public RegisterPage goToRegisterPage() {
        log.info("Go to register page");
        registerLink.click();
        return new RegisterPage();
    }

    public LoginPage shouldHaveErrorMessage(String message) {
        log.info("Should visible error message: {}", message);
        error.shouldHave(text(message));
        return this;
    }

    @Override
    public LoginPage shouldVisiblePage() {
        registerLink.shouldBe(visible);
        image.shouldHave(attribute("alt", "Эрмитаж")).shouldBe(visible);
        return this;
    }

    public HeaderComponent getHeader() {
        return new HeaderComponent();
    }

}
