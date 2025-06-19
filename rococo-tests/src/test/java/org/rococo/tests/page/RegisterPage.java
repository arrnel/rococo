package org.rococo.tests.page;

import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Allure;
import io.qameta.allure.Step;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import static com.codeborne.selenide.CollectionCondition.textsInAnyOrder;
import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selectors.*;
import static com.codeborne.selenide.Selenide.$;

@Slf4j
@ParametersAreNonnullByDefault
public class RegisterPage extends BasePage<RegisterPage> {

    private static final String URL = AUTH_URL + "/register";

    private final SelenideElement usernameInput = root.$(byName("username")).as("Username input"),
            passwordInput = root.$(byName("password")).as("Username input"),
            passwordConfirmInput = root.$(byName("passwordSubmit")).as("Username input"),
            submitButton = root.$(byAttribute("type", "submit")).as("Submit button"),
            loginLink = root.$(byText("Войти")).as("Login link"),
            welcomePageMessage = root.$(".form__subheader").as("Welcome submit message"),
            welcomeSubmitButton = root.$(byText("Войти в систему")).as("Welcome submit button"),
            image = $("section img").as("Registration page image"),
            usernameError = $(byAttribute("data-testid", "error-username")).as("Username error label"),
            passwordError = $(byAttribute("data-testid", "error-password")).as("Password error label"),
            confirmationPasswordError = $(byAttribute("data-testid", "error-password-submit")).as("Confirmation password error label");

    public RegisterPage open() {
        var stepText = "Open [Register] page: %s".formatted(URL);
        log.info(stepText);
        Allure.step(stepText, () -> Selenide.open(URL));
        return this;
    }

    @Nonnull
    @Step("Register new user by login = [{username}] and password = [{password}]")
    public MainPage register(String username, String password) {
        log.info("Register user by login = {} and password = {} ", username, password);
        shouldVisiblePage();
        usernameInput.setValue(username);
        passwordInput.setValue(password);
        passwordConfirmInput.setValue(password);
        submitButton.click();
        shouldVisibleWelcomePage();
        welcomeSubmitButton.click();
        return new MainPage();
    }

    private void shouldVisibleWelcomePage() {
        welcomePageMessage.shouldBe(visible).shouldHave(and("", text("Добро пожаловать в Ro"), text("coco")));
        welcomeSubmitButton.shouldBe(clickable);
    }

    @Step("Register new user by login = [{username}] and password = [{password}]")
    public RegisterPage registerWithError(String username, String password, String passwordConfirmation) {
        log.info("Register user by login = {}, password = {} and passwordConfirmation = {}", username, password, passwordConfirmation);
        shouldVisiblePage();
        usernameInput.setValue(username);
        passwordInput.setValue(password);
        passwordConfirmInput.setValue(passwordConfirmation);
        submitButton.click();
        return this;
    }

    public RegisterPage registerWithError(String username, String password) {
        return registerWithError(username, password, password);
    }

    @Step("Go to login page")
    public LoginPage goToLoginPage() {
        log.info("Go to login page");
        loginLink.click();
        return new LoginPage();
    }

    @Step("Check username error contains error text: {errorMessage}")
    public RegisterPage shouldHaveUsernameError(String errorMessage) {
        log.info("Should visible username error message: {}", errorMessage);
        usernameError.shouldHave(text(errorMessage));
        return this;
    }

    @Step("Check confirmation password error contains error text: {errorMessage}")
    public RegisterPage shouldHavePasswordError(String errorMessage) {
        log.info("Should visible password error message: {}", errorMessage);
        passwordError.shouldHave(text(errorMessage));
        return this;
    }

    @Step("Check confirmation password error contains error text: {errorMessage}")
    public RegisterPage shouldHaveConfirmationPasswordError(String errorMessage) {
        log.info("Should visible confirmation password error message: {}", errorMessage);
        confirmationPasswordError.shouldHave(text(errorMessage));
        return this;
    }

    @Step("Check register page contains expected errors")
    public void shouldHaveErrors(String[] errors) {
        log.info("Check register page contains expected errors: {}", String.join(", ", errors));
        root.$$(".form__error").shouldHave(textsInAnyOrder(errors));
    }

    @Override
    public RegisterPage shouldVisiblePage() {
        loginLink.shouldBe(visible);
        image.shouldHave(attribute("alt", "Ренуар")).shouldBe(visible);
        return this;
    }

}
