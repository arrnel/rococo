package org.rococo.tests.page;

import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.rococo.tests.model.LocationDTO;
import org.rococo.tests.model.MuseumDTO;
import org.rococo.tests.page.form.MuseumForm;

import javax.annotation.ParametersAreNonnullByDefault;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selectors.byAttribute;
import static org.rococo.tests.conditions.LocationCondition.location;
import static org.rococo.tests.conditions.ScreenshotCondition.screenshot;

@Slf4j
@ParametersAreNonnullByDefault
public class MuseumPage extends BasePage<MuseumPage> {

    private final SelenideElement titleElement = root.$("header").as("Museum title"),
            locationElement = root.$(byAttribute("data-testid", "museum-geo")).as("Museum location"),
            editButton = root.$(byAttribute("data-testid", "edit-museum")).as("Edit museum button"),
            descriptionElement = root.$(byAttribute("data-testid", "museum-description")).as("Museum description"),
            photoElement = root.$("img").as("Museum image");

    private final MuseumForm museumForm = new MuseumForm();

    @Step("Update museum = [{museum.title}]")
    public MuseumPage updateMuseum(MuseumDTO museum) {
        log.info("Update museum with title: {}", museum.getTitle());
        editButton.click();
        museumForm.updateMuseum(museum);
        museumForm.shouldNotVisibleComponent();
        return this;
    }

    @Step("Update museum = [{museum.title}]")
    public MuseumForm updateMuseumWithError(MuseumDTO museum) {
        log.info("Update museum with title: {}", museum.getTitle());
        editButton.click();
        museumForm.updateMuseum(museum);
        return museumForm;
    }

    @Step("Check museum have all expected data")
    public MuseumPage shouldHaveAllData(MuseumDTO museum) {

        validateMuseumTextData(museum);
        validateMuseumImageData(museum);

        shouldHaveTitle(museum.getTitle());
        shouldHaveDescription(museum.getDescription());
        shouldHaveLocation(museum.getLocation());
        shouldHavePhoto(museum.getPhoto());
        shouldHaveScreenshot(museum.getPathToPhoto());

        return this;

    }

    @Step("Check museum have expected text data")
    public MuseumPage shouldHaveTextData(MuseumDTO museum) {

        validateMuseumTextData(museum);

        shouldHaveTitle(museum.getTitle());
        shouldHaveDescription(museum.getDescription());
        shouldHaveLocation(museum.getLocation());

        return this;

    }

    @Step("Museum should have expected title")
    public MuseumPage shouldHaveTitle(String title) {
        titleElement.shouldHave(text(title));
        return this;
    }

    @Step("Museum should have expected description")
    public MuseumPage shouldHaveDescription(String description) {
        descriptionElement.shouldHave(text(description));
        return this;
    }

    private MuseumPage shouldHaveLocation(LocationDTO location) {
        if (location.getCountry() == null || StringUtils.isAnyEmpty(location.getCountry().getName(), location.getCity()))
            throw new IllegalArgumentException("Museum location country, countryName and city can't equals null or be empty");
        return shouldHaveLocation(location.getCountry().getName(), location.getCity());
    }

    @Step("Museum should have location: [{countryName}, {city}]")
    public MuseumPage shouldHaveLocation(String countryName, String city) {
        locationElement.shouldHave(location(countryName, city));
        return this;
    }

    @Step("Museum photo src attribute should have expected value")
    public MuseumPage shouldHavePhoto(String base64photo) {
        log.info("Check museum photo src attribute contains expected value");
        photoElement.shouldBe(visible).shouldHave(text(base64photo));
        return this;
    }

    @Step("Museum photo should have expected screenshot")
    public MuseumPage shouldHaveScreenshot(String pathToScreenshot) {
        log.info("Check museum photo have expected screenshot");
        photoElement.shouldBe(visible).shouldHave(screenshot(pathToScreenshot, 0.1, 1000L));
        return this;
    }

    @Step("Museum photo should have expected screenshot")
    public MuseumPage shouldHaveScreenshot(String pathToScreenshot, double percentOfTolerance) {
        log.info("Check museum photo have expected screenshot");
        photoElement.shouldBe(visible).shouldHave(screenshot(pathToScreenshot, percentOfTolerance, 1000L));
        return this;
    }

    @Step("Museum photo should have expected screenshot")
    public MuseumPage shouldHaveScreenshot(String pathToScreenshot, boolean rewrite) {
        log.info("Check museum photo have expected screenshot");
        photoElement.shouldBe(visible).shouldHave(screenshot(pathToScreenshot, rewrite));
        return this;
    }

    @Step("Museum should contains visible edit button")
    public MuseumPage shouldVisibleEditButton() {
        log.info("Check museum contains visible edit button");
        editButton.shouldBe(visible);
        return this;
    }

    @Step("Museum should not contains edit button")
    public MuseumPage shouldNotContainsEditButton() {
        log.info("Check museum not contains edit button");
        editButton.shouldNotBe(visible);
        return this;
    }

    @Step("Check update museum button not exists")
    public MuseumPage shouldNotExistsUpdateMuseumButton() {
        log.info("Check update museum button not exists");
        editButton.shouldNot(exist);
        return this;
    }

    @Override
    public MuseumPage shouldVisiblePage() {
        titleElement.shouldBe(visible);
        locationElement.shouldBe(visible);
        return this;
    }

    private static void validateMuseumTextData(MuseumDTO museum) {
        if (StringUtils.isAnyEmpty(
                museum.getTitle(),
                museum.getDescription(),
                museum.getLocation().getCountry().getName(),
                museum.getLocation().getCity()
        )) {
            throw new IllegalArgumentException("Museum data can't equals null or be empty. Museum: " + museum);
        }
    }

    private static void validateMuseumImageData(MuseumDTO museum) {
        validateMuseumTextData(museum);
        if (StringUtils.isAnyEmpty(
                museum.getPhoto(),
                museum.getPathToPhoto()
        )) {
            throw new IllegalArgumentException("Museum data can't equals null or be empty. Museum: " + museum);
        }
    }

}
