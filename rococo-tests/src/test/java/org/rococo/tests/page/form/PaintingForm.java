package org.rococo.tests.page.form;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;
import lombok.extern.slf4j.Slf4j;
import org.rococo.tests.conditions.ScreenshotCondition;
import org.rococo.tests.config.Config;
import org.rococo.tests.model.ArtistDTO;
import org.rococo.tests.model.MuseumDTO;
import org.rococo.tests.model.PaintingDTO;
import org.rococo.tests.page.component.BaseComponent;
import org.rococo.tests.page.component.SelectField;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

import static com.codeborne.selenide.CollectionCondition.containExactTextsCaseSensitive;
import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selectors.*;
import static com.codeborne.selenide.Selenide.$;

@Slf4j
@ParametersAreNonnullByDefault
public final class PaintingForm extends BaseComponent<PaintingForm> {

    private static final Config CFG = Config.getInstance();
    private static final String ADD_PAINTING_TITLE = "Новая картина";
    private static final String UPDATE_PAINTING_TITLE = "Редактировать картину";

    private static final String errorLocatorTpl = ".//label[./input[@name='%s']]/span[@class='text-error-400']";

    private final SelenideElement header = self.$("header").as("Modal component header"),
            titleInput = self.$(byName("title")).as("Artist name input"),
            descriptionInput = self.$(byName("description")).as("Painting description input"),
            artistsSelectContainer = self.$(byName("authorId")).as("Artists select container"),
            museumsSelectContainer = self.$(byName("museumId")).as("Museums select container"),
            photo = self.$("img").as("Painting avatar"),
            photoInput = self.$(byName("content")).as("Painting photo input"),
            addButton = self.$(byText("Добавить")).as("Add painting button"),
            saveButton = self.$(byText("Сохранить")).as("Update painting button"),
            closeButton = self.$(byText("Закрыть")).as("Close edit painting form button"),
            titleErrorLabel = self.$x(errorLocatorTpl.formatted("title")).as("Artist name error label"),
            descriptionErrorLabel = self.$x(errorLocatorTpl.formatted("description")).as("Title error label"),
            photoErrorLabel = self.$x(errorLocatorTpl.formatted("content")).as("Title error label");

    private final SelectField artistsSelect = new SelectField(artistsSelectContainer);
    private final SelectField museumsSelect = new SelectField(museumsSelectContainer);

    public PaintingForm() {
        super($(byAttribute("data-testid", "modal-component")).as("Painting form"));
    }

    public PaintingForm(SelenideElement self) {
        super(self);
    }

    public void addNewPainting(PaintingDTO painting) {
        fillForm(painting);
        submitCreation();
    }

    public void updatePainting(PaintingDTO painting) {
        fillForm(painting);
        submitUpdate();
    }

    public void fillAndClose(PaintingDTO painting) {
        fillForm(painting);
        close();
    }

    @Step("Fill painting form")
    private void fillForm(PaintingDTO painting) {
        fillTitle(painting.getTitle());
        fillDescription(painting.getDescription());
        selectArtist(painting.getArtist().getName());
        selectMuseum(painting.getMuseum().getTitle());
        uploadPhoto(painting.getPathToPhoto());
    }

    @Step("Upload painting photo: {paintingPhoto}")
    private void uploadPhoto(String paintingPhoto) {
        photoInput.uploadFromClasspath(CFG.originalPhotoBaseDir() + paintingPhoto);
    }

    @Step("Fill title: {title}")
    private void fillTitle(String title) {
        titleInput.setValue(title);
    }

    @Step("Fill description: {description}")
    private void fillDescription(String description) {
        descriptionInput.setValue(description);
    }

    @Step("Select artist by name: {artistName}")
    private void selectArtist(String artistName) {
        artistsSelect.selectByExactName(artistName);
    }

    @Step("Select museum by title: {museumTitle}")
    private void selectMuseum(String museumTitle) {
        museumsSelect.selectByExactName(museumTitle);
    }

    @Step("Submit")
    private void submitCreation() {
        addButton.click();
    }

    @Step("Submit")
    private void submitUpdate() {
        saveButton.click();
    }

    @Step("Close")
    private void close() {
        closeButton.click();
    }

    public PaintingForm shouldHaveImage(String base64Image) {
        photo.shouldHave(Condition.attribute("src", base64Image));
        return this;
    }

    public PaintingForm shouldHaveScreenshot(String pathToImage, boolean rewriteScreenshot) {
        photo.shouldHave(ScreenshotCondition.screenshot(pathToImage, 0.05, 1000, rewriteScreenshot));
        return this;
    }

    public PaintingForm shouldContainArtists(List<ArtistDTO> artists) {
        return shouldContainArtistsNames(artists.stream().map(ArtistDTO::getName).toList());
    }

    @Step("Check artist select contains artists names: {artistsNames}")
    public PaintingForm shouldContainArtistsNames(List<String> artistsNames) {
        log.info("Check artist select contains artists names: {}", artistsNames);
        artistsSelect.shouldContainItems(artistsNames);
        return this;
    }

    @Step("Check artist select contains artist name: {artistName}")
    public PaintingForm shouldContainArtistName(String artistName) {
        log.info("Check artist select contains artist name: {}", artistName);
        artistsSelect.shouldContainItem(artistName);
        return this;
    }


    public PaintingForm shouldContainMuseums(List<MuseumDTO> museums) {
        return shouldContainMuseumsTitles(museums.stream().map(MuseumDTO::getTitle).toList());
    }

    @Step("Check museum select contains museums titles: {museumsNames}")
    public PaintingForm shouldContainMuseumsTitles(List<String> museumsNames) {
        var isContainsInvalidValues = museumsNames.stream()
                .anyMatch(name -> name == null || name.isEmpty());
        if (isContainsInvalidValues)
            throw new IllegalArgumentException("Museums list can't contains null or empty values");
        artistsSelect.shouldContainItems(museumsNames);
        return this;
    }

    @Step("Check artist select contains artist name: {museumTitle}")
    public PaintingForm shouldContainMuseumTitle(String museumTitle) {
        log.info("Check artist select contains artist name: {}", museumTitle);
        if (museumTitle.isEmpty())
            throw new IllegalArgumentException("Museum title is empty");
        artistsSelect.shouldContainItem(museumTitle);
        return this;
    }

    @Step("Check painting name error is visible")
    public PaintingForm shouldVisiblePaintingTitleError() {
        log.info("Check painting name error is visible");
        titleErrorLabel.shouldBe(visible);
        return this;
    }

    @Step("Check painting name error has expected text")
    public PaintingForm shouldPaintingTitleErrorHasText(String errorText) {
        log.info("Check painting name error has text = [{}]", errorText);
        titleErrorLabel.shouldHave(text(errorText));
        return this;
    }

    @Step("Check painting description error is visible")
    public PaintingForm shouldVisiblePaintingDescriptionError() {
        log.info("Check painting description error is visible");
        descriptionErrorLabel.shouldBe(visible);
        return this;
    }

    @Step("Check painting description error has expected text")
    public PaintingForm shouldPaintingDescriptionErrorHaveText(String errorText) {
        log.info("Check painting description error has text = [{}]", errorText);
        descriptionErrorLabel.shouldHave(text(errorText));
        return this;
    }

    @Step("Check painting photo error is visible")
    public PaintingForm shouldVisiblePaintingPhotoError() {
        log.info("Check painting photo error is visible");
        photoErrorLabel.shouldBe(visible);
        return this;
    }

    @Step("Check painting photo error has expected text")
    public PaintingForm shouldPaintingPhotoErrorHaveText(String errorText) {
        log.info("Check painting photo error has text = [{}]", errorText);
        photoErrorLabel.shouldHave(text(errorText));
        return this;
    }

    @Step("Check painting form has errors: [{errors}]")
    public PaintingForm shouldHaveErrors(String... errors) {
        log.info("Check artist form has visible errors");
        self.$$("form .text-error-400").as("Artist Errors").should(containExactTextsCaseSensitive(errors));
        return this;
    }

    @Override
    public PaintingForm shouldVisibleComponent() {
        header.shouldBe(visible).shouldHave(or("text", text(ADD_PAINTING_TITLE), text(UPDATE_PAINTING_TITLE)));
        return this;
    }

    @Override
    public void shouldNotVisibleComponent() {
        header.shouldNot(or("header not visible or exist", visible, exist));
    }

}
