package org.rococo.tests.page.component;

import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Allure;
import io.qameta.allure.Step;
import lombok.extern.slf4j.Slf4j;
import org.rococo.tests.enums.Theme;
import org.rococo.tests.page.ArtistsPage;
import org.rococo.tests.page.LoginPage;
import org.rococo.tests.page.MuseumsPage;
import org.rococo.tests.page.PaintingsPage;
import org.rococo.tests.page.form.UserForm;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Objects;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selectors.byAttribute;
import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.$;
import static org.rococo.tests.enums.Theme.DARK;
import static org.rococo.tests.enums.Theme.LIGHT;

@Slf4j
@ParametersAreNonnullByDefault
public class HeaderComponent extends BaseComponent<HeaderComponent> {

    private final SelenideElement logo = self.$("h1").as("Logo"),
            paintingsLink = self.$(byText("Картины")).as("Header paintings link"),
            artistsLink = self.$(byText("Художники")).as("Header artists link"),
            museumsLink = self.$(byText("Музеи")).as("Header museums link"),
            themeSwitcher = self.$(byAttribute("aria-label", "Light Switch")).as("Theme switcher"),
            profileImg = self.$x(".//button[.//figure]").as("Menu button"),
            avatar = self.$("figure svg").as("Avatar"),
            avatarText = avatar.$("text").as("Avatar text"),
            loginButton = self.$(byText("Войти")).as("Login button");

    public HeaderComponent() {
        super($("header").as("[Header]"));
    }

    public HeaderComponent(SelenideElement self) {
        super(self);
    }

    @Step("Go to museums page")
    public MuseumsPage goToMuseumsPage() {
        museumsLink.click();
        return new MuseumsPage();
    }

    @Step("Go to artists page")
    public ArtistsPage goToArtistsPage() {
        artistsLink.click();
        return new ArtistsPage();
    }

    @Step("Go to paintings page")
    public PaintingsPage goToPaintingsPage() {
        paintingsLink.click();
        return new PaintingsPage();
    }

    @Step("Go to Login page")
    public LoginPage goToSignInPage() {
        log.info("Go to Login page");
        loginButton.click();
        return new LoginPage();
    }

    @Step("Logout")
    public LoginPage signOut() {
        return openUserProfile()
                .logout();
    }

    public UserForm openUserProfile() {
        profileImg.click();
        return new UserForm();
    }

    public HeaderComponent changeTheme() {
        var switchToTheme = getCurrentTheme() == LIGHT
                ? DARK
                : LIGHT;
        log.info("Change theme to: {}", switchToTheme);
        Allure.step("Change current theme to: " + switchToTheme, () -> themeSwitcher.click());
        return this;
    }

    @Nonnull
    private Theme getCurrentTheme() {
        var themeSwitcherTitleAttrib = Objects.requireNonNull(themeSwitcher.shouldBe(visible).getAttribute("title"));
        return themeSwitcherTitleAttrib.contains("Light") ? LIGHT : DARK;
    }

    @Step("Should visible login button")
    public HeaderComponent shouldVisibleLoginButton() {
        log.info("Should be visible login button");
        loginButton.shouldBe(visible);
        return this;
    }

    @Step("Should not visible login button")
    public HeaderComponent shouldNotVisibleLoginButton() {
        log.info("Should not exist login button");
        loginButton.shouldNot(exist);
        return this;
    }

    @Override
    public HeaderComponent shouldVisibleComponent() {
        self.shouldBe(visible);
        logo.shouldBe(visible);
        return this;
    }

    @Override
    public void shouldNotVisibleComponent() {
        logo.shouldNot(or("logo not visible or exist", visible, exist));
    }

}
