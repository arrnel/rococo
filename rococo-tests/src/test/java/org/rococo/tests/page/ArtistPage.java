package org.rococo.tests.page;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;
import lombok.extern.slf4j.Slf4j;
import org.rococo.tests.model.ArtistDTO;
import org.rococo.tests.model.PaintingDTO;
import org.rococo.tests.page.component.HeaderComponent;
import org.rococo.tests.page.form.ArtistForm;
import org.rococo.tests.page.form.PaintingForm;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

import static com.codeborne.selenide.CollectionCondition.textsInAnyOrder;
import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selectors.byAttribute;
import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.$;
import static org.rococo.tests.conditions.ScreenshotCondition.screenshot;

@Slf4j
@ParametersAreNonnullByDefault
public class ArtistPage extends BasePage<ArtistPage> {

    private final SelenideElement nameElement = root.$(byAttribute("data-testid", "artist-name")),
            biographyElement = root.$(byAttribute("data-testid", "artist-biography")),
            photoElement = root.$(byAttribute("data-testid", "avatar")),
            editButton = root.$(byAttribute("data-testid", "edit-artist")),
            addPaintingButton = root.$(byText("Добавить картину")),
            paintingsListTitle = root.$(byAttribute("data-testid", "artist-paintings-title")).as("Artist paintings list title"),
            paintingsTable = root.$(byAttribute("data-testid", "artist-paintings")).as("Artist paintings table"),
            emptyPaintingsContainer = root.$(byAttribute("data-testid", "artist-paintings")).as("Artist empty paintings container"),
            emptyPaintingsText = emptyPaintingsContainer.$("p").as("Empty paintings list text"),
            modalComponent = $(byAttribute("data-testid", "modal-component"));

    private final ElementsCollection paintingsTitles = paintingsTable.$$("li .text-center").as("Artist paintings titles list");

    private final ArtistForm artistForm = new ArtistForm(modalComponent);
    private final PaintingForm editPaintingForm = new PaintingForm(modalComponent);

    @Step("Update artist = [{artist.name}]")
    public ArtistPage editArtist(ArtistDTO artist) {
        log.info("Update artist: {}", artist);
        editButton.click();
        artistForm.updateArtist(artist);
        return this;
    }

    @Step("Add new painting = [{painting.title}] to artist = [{painting.artist.name}]")
    public ArtistPage addPainting(PaintingDTO painting) {
        var artistName = nameElement.getText();
        log.info("Add new painting with title = [{}] to artist = [{}]", painting.getTitle(), artistName);
        addPaintingButton.click();
        editPaintingForm.addNewPainting(painting);
        return this;
    }

    @Step("Go to painting [{paintingTitle}]")
    public ArtistPage goToPainting(String paintingTitle) {
        log.info("Go to painting [{}]", paintingTitle);
        paintingsTable.shouldBe(visible);
        paintingsTitles.filterBy(text(paintingTitle)).first().click();
        return new ArtistPage();
    }

    public HeaderComponent getHeader() {
        return new HeaderComponent();
    }

    @Step("Should have name")
    public ArtistPage shouldHaveName(String name) {
        log.info("Artist should have name: {}", name);
        nameElement.shouldHave(text(name));
        return this;
    }

    @Step("Should have name")
    public ArtistPage shouldHaveBio(String bio) {
        log.info("Artist should have bio: {}", bio);
        biographyElement.shouldHave(text(bio));
        return this;
    }

    @Step("Should have image")
    public ArtistPage shouldHavePhoto(String base64photo) {
        log.info("Artist should have exact base64 photo");
        photoElement.shouldHave(attribute("src", base64photo));
        return this;
    }

    @Step("Should have screenshot")
    public ArtistPage shouldHaveScreenshot(String pathToPhoto) {
        log.info("Artist should have exact photo screenshot");
        photoElement.shouldHave(screenshot(pathToPhoto));
        return this;
    }

    @Step("Should have screenshot")
    public ArtistPage shouldHaveScreenshot(String pathToPhoto, boolean rewrite) {
        log.info("Artist should have exact photo screenshot");
        photoElement.shouldHave(screenshot(pathToPhoto, rewrite));
        return this;
    }

    @Step("Check artists should not have paintings")
    public void shouldNotHavePaintings() {
        emptyPaintingsContainer.shouldBe(visible);
        emptyPaintingsText.shouldHave(text("Пока что список картин этого художника пуст."));
    }

    @Step("Check artists contains paintings in any order")
    public void shouldContainsPaintingTitles(List<PaintingDTO> paintings) {
        log.info("Check artists contains paintings in any order with titles: {}", paintings);
        paintingsTable.shouldBe(visible);
        var expectedPaintingsTitles = paintings.stream()
                .map(PaintingDTO::getTitle)
                .toList();
        paintingsTitles.shouldHave(textsInAnyOrder(expectedPaintingsTitles));
    }

    @Step("Check update artist button not exists")
    public ArtistPage shouldNotExistsUpdateArtistButton() {
        log.info("Check update artist button not exists");
        editButton.shouldNot(exist);
        return this;
    }

    @Override
    public ArtistPage shouldVisiblePage() {
        nameElement.shouldBe(visible);
        editButton.shouldBe(visible);
        addPaintingButton.shouldBe(visible);
        return this;
    }

}
