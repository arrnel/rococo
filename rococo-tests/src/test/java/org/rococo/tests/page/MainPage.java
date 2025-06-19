package org.rococo.tests.page;

import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Allure;
import io.qameta.allure.Step;
import lombok.extern.slf4j.Slf4j;
import org.rococo.tests.page.component.HeaderComponent;

import static com.codeborne.selenide.Condition.visible;

@Slf4j
public class MainPage extends BasePage<MainPage> {

    private static final String URL = BASE_URL;

    private final SelenideElement nav = root.$("nav"),
            title = nav.$("p").as("'Main page' title"),
            paintingsContainer = nav.$x(".//ul[.//div[text()='Картины']]"),
            paintingsImage = paintingsContainer.$("img").as("Paintings image"),
            paintingsText = paintingsContainer.$("div").as("Paintings text"),
            artistsContainer = nav.$x(".//ul[.//div[text()='Художники']]"),
            artistsImage = artistsContainer.$("img").as("Artists image"),
            artistsText = artistsContainer.$("div").as("Artists text"),
            museumsContainer = nav.$x(".//ul[.//div[text()='Музеи']]"),
            museumsImage = museumsContainer.$("img").as("Museums image"),
            museumsText = museumsContainer.$("div").as("Museums text");

    public MainPage open() {
        var stepText = "Open [Main] page: %s".formatted(URL);
        log.info(stepText);
        Allure.step(stepText, () -> Selenide.open(URL));
        return this;
    }

    @Step("Go to paintings page")
    public PaintingsPage goToPaintingsPage() {
        log.info("Open Paintings page");
        paintingsContainer.click();
        return new PaintingsPage();
    }

    @Step("Go to paintings page")
    public PaintingsPage goToMuseumsPage() {
        museumsContainer.click();
        return new PaintingsPage();
    }

    @Step("Go to paintings page")
    public PaintingsPage goToArtistsPage() {
        artistsContainer.click();
        return new PaintingsPage();
    }

    public HeaderComponent getHeader() {
        return new HeaderComponent();
    }

    @Override
    public MainPage shouldVisiblePage() {
        paintingsImage.shouldBe(visible);
        artistsImage.shouldBe(visible);
        museumsImage.shouldBe(visible);
        return this;
    }

}
