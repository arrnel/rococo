package org.rococo.tests.page;

import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Nonnull;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;

@Slf4j
public class ErrorPage extends BasePage<ErrorPage> {

    private static final String PAGE_TITLE = "Страница не найдена";

    private final SelenideElement errorMessage = root.$("p").as("Error page message"),
            goToMainMenuButton = root.$("a").as("Go to main page button");

    @Nonnull
    @Step("Go to main page")
    public MainPage goToMainPage() {
        log.info("Go to main page");
        goToMainMenuButton.click();
        return new MainPage();
    }

    @Override
    public ErrorPage shouldVisiblePage() {
        errorMessage.shouldHave(text(PAGE_TITLE));
        goToMainMenuButton.shouldBe(visible);
        return this;
    }

}
