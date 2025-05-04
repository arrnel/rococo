package org.rococo.tests.page.form;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;
import org.rococo.tests.conditions.ScreenshotCondition;
import org.rococo.tests.config.Config;
import org.rococo.tests.model.UserDTO;
import org.rococo.tests.page.LoginPage;
import org.rococo.tests.page.component.BaseComponent;
import org.rococo.tests.page.component.HeaderComponent;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selectors.*;
import static com.codeborne.selenide.Selenide.$;

public final class UserForm extends BaseComponent<UserForm> {

    private static final Config CFG = Config.getInstance();
    private static final String PROFILE = "Профиль";

    private final SelenideElement username = self.$("h4").as("Username"),
            avatar = self.$(byAttribute("data-testid", "avatar")).as("User avatar"),
            photoInput = self.$(byName("content")).as("User photo input"),
            firstName = self.$(byName("firstname")).as("User first name"),
            lastName = self.$(byName("surname")).as("User last name"),
            saveButton = self.$(byText("Сохранить")).as("Submit artist update button"),
            logoutButton = self.$(byText("Выйти")).as("Logout button"),
            closeButton = self.$(byText("Закрыть")).as("Close edit artist form button");

    public UserForm() {
        super($(byAttribute("data-testid", "modal-component")));
    }

    public UserForm(SelenideElement self) {
        super(self);
    }

    @Step("Update user data")
    public HeaderComponent update(UserDTO user) {
        fillForm(user);
        saveButton.click();
        return new HeaderComponent();
    }

    @Step("Fill user form and close")
    public HeaderComponent fillAndClose(UserDTO user) {
        fillForm(user);
        closeButton.click();
        return new HeaderComponent();
    }

    @Step("Press logout button")
    public LoginPage logout() {
        logoutButton.click();
        return new LoginPage();
    }

    private UserForm fillForm(UserDTO user) {
        firstName.setValue(user.getFirstName());
        lastName.setValue(user.getFirstName());
        if (user.getPathToPhoto() != null)
            photoInput.uploadFromClasspath(CFG.originalPhotoBaseDir() + user.getPathToPhoto());
        return this;
    }

    public void shouldHaveImage(String base64Image) {
        avatar.shouldHave(Condition.attribute("src", base64Image));
    }

    public void shouldHaveScreenshot(String pathToImage, boolean rewriteScreenshot) {
        avatar.shouldHave(ScreenshotCondition.screenshot(pathToImage, 0.05, 1000, rewriteScreenshot));
    }

    @Override
    public UserForm shouldVisibleComponent() {
        self.shouldBe(visible);
        username.shouldBe(visible);
        avatar.shouldBe(visible);
        firstName.shouldBe(visible);
        lastName.shouldBe(visible);
        logoutButton.shouldBe(visible);
        return this;
    }

    @Override
    public void shouldNotVisibleComponent() {
        username.shouldNot(or("username field not visible or exist", visible, exist));
    }

}
