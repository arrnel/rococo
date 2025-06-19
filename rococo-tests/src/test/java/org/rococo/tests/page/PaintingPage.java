package org.rococo.tests.page;

import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Allure;
import io.qameta.allure.Step;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.rococo.tests.model.PaintingDTO;
import org.rococo.tests.page.form.PaintingForm;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.UUID;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selectors.byAttribute;
import static org.rococo.tests.conditions.ScreenshotCondition.screenshot;

@Slf4j
@ParametersAreNonnullByDefault
public class PaintingPage extends BasePage<PaintingPage> {

    private static final String URL = BASE_URL + "/painting/%s";

    private final SelenideElement photoContainer = root.$(byAttribute("data-testid", "painting-photo")).as("Painting photo"),
            titleLabel = root.$("header").as("Painting title"),
            artistLabel = root.$(byAttribute("data-testid", "artist-name")).as("Painting artist"),
            museumLabel = root.$(byAttribute("data-testid", "museum-title")).as("Painting museum"),
            descriptionLabel = root.$(byAttribute("data-testid", "painting-description")).as("Painting description"),
            editButton = root.$(byAttribute("data-testid", "edit-painting")).as("'Edit painting' button");

    private final PaintingForm paintingForm = new PaintingForm();

    public PaintingPage open(UUID paintingId) {
        var url = URL.formatted(paintingId);
        var stepText = "Open [Painting] page: %s".formatted(url);
        log.info(stepText);
        Allure.step(stepText, () -> Selenide.open(url));
        return this;
    }

    @Step("Check painting have expected text data")
    public PaintingPage shouldHaveTextData(PaintingDTO painting) {

        validatePaintingTextData(painting);

        shouldHaveTitle(painting.getTitle());
        shouldHaveDescription(painting.getDescription());
        shouldHaveArtist(painting.getArtist().getName());
        shouldHaveMuseum(painting.getMuseum().getTitle());

        return this;

    }

    @Step("Update painting ")
    public PaintingPage updatePainting(PaintingDTO painting) {
        log.info("Update painting {}", painting);
        editButton.click();
        paintingForm.updatePainting(painting);
        paintingForm.shouldNotVisibleComponent();
        return this;
    }

    @Step("Update painting ")
    public PaintingForm updatePaintingWithError(PaintingDTO painting) {
        log.info("Update painting  {}", painting);
        editButton.click();
        paintingForm.updatePainting(painting);
        return new PaintingForm();
    }

    @Step("Check painting has expected title")
    public PaintingPage shouldHaveTitle(String title) {
        log.info("Check painting has expected title: {}", title);
        this.titleLabel.shouldHave(text(title));
        return this;
    }

    @Step("Check painting has expected description")
    public PaintingPage shouldHaveDescription(String description) {
        log.info("Check painting has expected description: {}", description);
        descriptionLabel.shouldHave(text(description));
        return this;
    }

    @Step("Check painting has expected artist name")
    public PaintingPage shouldHaveArtist(String artistName) {
        log.info("Check painting has expected artist name: {}", artistName);
        artistLabel.shouldHave(text(artistName));
        return this;
    }

    @Step("Check painting has expected museum title")
    public PaintingPage shouldHaveMuseum(String museumTitle) {
        log.info("Check painting has expected museum title: {}", museumTitle);
        museumLabel.shouldHave(text(museumTitle));
        return this;
    }

    @Step("Painting photo src attribute should have expected value")
    public PaintingPage shouldHavePhoto(String base64photo) {
        log.info("Check painting photo src attribute has expected value");
        photoContainer.shouldBe(visible).shouldHave(text(base64photo));
        return this;
    }

    @Step("Painting photo should have expected screenshot")
    public PaintingPage shouldHaveScreenshot(String pathToScreenshot) {
        log.info("Check painting photo has expected screenshot");
        photoContainer.shouldBe(visible).shouldHave(screenshot(pathToScreenshot, 1000L));
        return this;
    }

    @Step("Painting photo should have expected screenshot")
    public PaintingPage shouldHaveScreenshot(String pathToScreenshot, boolean rewrite) {
        log.info("Check painting photo has expected screenshot");
        photoContainer.shouldBe(visible).shouldHave(screenshot(pathToScreenshot, rewrite));
        return this;
    }


    @Step("Painting should contains visible edit button")
    public PaintingPage shouldVisibleEditButton() {
        log.info("Check painting has visible edit button");
        editButton.shouldBe(visible);
        return this;
    }

    @Step("Painting should not contains edit button")
    public PaintingPage shouldNotContainsEditButton() {
        log.info("Check painting does not have edit button");
        editButton.shouldNotBe(visible);
        return this;
    }

    @Override
    public PaintingPage shouldVisiblePage() {
        titleLabel.shouldBe(visible);
        artistLabel.shouldBe(visible);
        return this;
    }

    private static void validatePaintingTextData(PaintingDTO painting) {
        if (StringUtils.isAnyEmpty(
                painting.getTitle(),
                painting.getDescription()
        )) {
            throw new IllegalArgumentException("Painting data can't equals null or be empty. Painting: " + painting);
        }
    }

    private static void validatePaintingImageData(PaintingDTO painting) {
        validatePaintingTextData(painting);
        if (StringUtils.isAnyEmpty(
                painting.getPhoto(),
                painting.getPathToPhoto()
        )) {
            throw new IllegalArgumentException("Painting data can't equals null or be empty. Painting: " + painting);
        }
    }

}
