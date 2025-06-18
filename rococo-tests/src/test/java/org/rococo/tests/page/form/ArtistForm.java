package org.rococo.tests.page.form;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Allure;
import io.qameta.allure.Step;
import lombok.extern.slf4j.Slf4j;
import org.rococo.tests.conditions.ScreenshotCondition;
import org.rococo.tests.config.Config;
import org.rococo.tests.model.ArtistDTO;
import org.rococo.tests.page.component.BaseComponent;

import javax.annotation.ParametersAreNonnullByDefault;

import static com.codeborne.selenide.CollectionCondition.containExactTextsCaseSensitive;
import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selectors.*;
import static com.codeborne.selenide.Selenide.$;

@Slf4j
@ParametersAreNonnullByDefault
public final class ArtistForm extends BaseComponent<ArtistForm> {

    private static final Config CFG = Config.getInstance();
    private static final String ADD_ARTIST_TITLE = "Новый художник";
    private static final String UPDATE_ARTIST_TITLE = "Редактировать художника";

    private static final String errorLocatorTpl = ".//label[./input[@name='%s']]/span[@class='text-error-400']";

    private final SelenideElement header = self.$x(".//div[contains(@class, 'card') and ./header]").as("Artist form title"),
            nameInput = self.$(byName("name")).as("Artist name input"),
            bioInput = self.$(byName("biography")).as("Artist bio input"),
            avatar = self.$(byAttribute("data-testid", "avatar")).$("img").as("Artist avatar input"),
            photoInput = self.$(byName("photo")).as("Artist photo input"),
            addButton = self.$(byText("Добавить")).as("Submit artist creation button"),
            saveButton = self.$(byText("Сохранить")).as("Submit artist update button"),
            closeButton = self.$(byText("Закрыть")).as("Close edit artist form button"),
            nameErrorLabel = self.$x(errorLocatorTpl.formatted("name")).as("Artist name error label"),
            bioErrorLabel = self.$x(errorLocatorTpl.formatted("biography")).as("Title error label"),
            avatarErrorLabel = self.$x(errorLocatorTpl.formatted("photo")).as("Title error label");

    public ArtistForm(SelenideElement self) {
        super(self);
    }

    public ArtistForm() {
        super($(byAttribute("data-testid", "modal-component")).as("Artist form"));
    }

    public void addNewArtist(ArtistDTO artist) {
        fillForm(artist);
        submitCreation();
    }

    public void updateArtist(ArtistDTO artist) {
        fillForm(artist);
        submitUpdating();
    }

    @Step("Fill artist form")
    private void fillForm(ArtistDTO artist) {
        var artistAvatarPath = CFG.originalPhotoBaseDir() + artist.getPathToPhoto();
        Allure.step("Set artist name: %s".formatted(artist.getName()),
                () -> nameInput.setValue(artist.getName()));
        Allure.step("Set artist bio: %s".formatted(artist.getName()),
                () -> bioInput.setValue(artist.getBiography()));
        Allure.step("Upload artist photo from path: %s".formatted(artistAvatarPath),
                () -> photoInput.uploadFromClasspath(artistAvatarPath));
    }

    @Step("Press add button")
    private void submitCreation() {
        addButton.click();
    }

    @Step("Press save button")
    public void submitUpdating() {
        saveButton.click();
    }

    public void shouldHaveImage(String base64Image) {
        avatar.shouldHave(Condition.attribute("src", base64Image));
    }

    public void shouldHaveScreenshot(String pathToImage, boolean rewriteScreenshot) {
        avatar.shouldHave(ScreenshotCondition.screenshot(pathToImage, 0.05, 1000, rewriteScreenshot));
    }

    @Step("Check artist name error is visible")
    public ArtistForm shouldVisibleArtistNameError() {
        log.info("Check artist name error is visible");
        nameErrorLabel.shouldBe(visible);
        return this;
    }

    @Step("Check artist name error has expected text")
    public ArtistForm shouldArtistNameErrorHasText(String errorText) {
        log.info("Check artist name error has text = [{}]", errorText);
        nameErrorLabel.shouldHave(text(errorText));
        return this;
    }

    @Step("Check artist biography error is visible")
    public ArtistForm shouldVisibleArtistBiographyError() {
        log.info("Check artist biography error is visible");
        bioErrorLabel.shouldBe(visible);
        return this;
    }

    @Step("Check artist biography error has expected text")
    public ArtistForm shouldArtistBiographyErrorHaveText(String errorText) {
        log.info("Check artist biography error has text = [{}]", errorText);
        bioErrorLabel.shouldHave(text(errorText));
        return this;
    }

    @Step("Check artist name error is visible")
    public ArtistForm shouldVisibleArtistAvatarError() {
        log.info("Check artist name error is visible");
        avatarErrorLabel.shouldBe(visible);
        return this;
    }

    @Step("Check artist name error has expected text")
    public ArtistForm shouldArtistAvatarErrorHasText(String errorText) {
        log.info("Check artist name error has text = [{}]", errorText);
        avatarErrorLabel.shouldHave(text(errorText));
        return this;
    }

    @Step("Check artist form has errors: [{errors}]")
    public ArtistForm shouldHaveErrors(String... errors) {
        log.info("Check artist form has visible errors");
        self.$$("form .text-error-400").as("Artist Errors").should(containExactTextsCaseSensitive(errors));
        return this;
    }

    @Override
    public ArtistForm shouldVisibleComponent() {
        header.should(visible).shouldHave(or("text", text(ADD_ARTIST_TITLE), text(UPDATE_ARTIST_TITLE)));
        return this;
    }

    @Override
    public void shouldNotVisibleComponent() {
        header.shouldNot(or("", visible, exist));
    }

}
