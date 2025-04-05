package org.rococo.tests.page;

import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;
import org.rococo.tests.config.Config;
import org.rococo.tests.page.component.HeaderComponent;

import static com.codeborne.selenide.Condition.visible;

public class MainPage extends BasePage<MainPage> {

    public static final String URL = BASE_URL;

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

    @Step("Go to paintings page")
    public PaintingsPage goToPaintingsPage() {
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
