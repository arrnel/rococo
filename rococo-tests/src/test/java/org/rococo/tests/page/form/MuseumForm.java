package org.rococo.tests.page.form;

import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;
import lombok.extern.slf4j.Slf4j;
import org.rococo.tests.config.Config;
import org.rococo.tests.model.MuseumDTO;
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
public final class MuseumForm extends BaseComponent<MuseumForm> {

    private static final Config CFG = Config.getInstance();
    private static final String ADD_MUSEUM_TITLE = "Новый музей";
    private static final String UPDATE_MUSEUM_TITLE = "Редактировать музей";

    private static final String errorLocatorTpl = ".//label[./input[@name='%s']]/span[@class='text-error-400']";

    private final SelenideElement header = self.$x(".//div[contains(@class, 'card') and ./header]").as("Museum form title"),
            titleInput = self.$(byName("title")).as("Museum title input"),
            descriptionInput = self.$(byName("description")).as("Museum description input"),
            photo = self.$(byAttribute("data-testid", "avatar")).$("img").as("Museum photo input"),
            countries = self.$(byName("countryId")).as("Countries select container"),
            city = self.$(byName("city")).as("City input"),
            photoInput = self.$(byName("photo")).as("Museum photo input"),
            addButton = self.$(byText("Добавить")).as("Submit museum creation button"),
            saveButton = self.$(byText("Сохранить")).as("Submit museum update button"),
            closeButton = self.$(byText("Закрыть")).as("Close edit museum form button"),
            titleErrorLabel = self.$x(errorLocatorTpl.formatted("title")).as("Museum title error label"),
            descriptionErrorLabel = self.$x(errorLocatorTpl.formatted("description")).as("Museum description error label"),
            photoErrorLabel = self.$x(errorLocatorTpl.formatted("photo")).as("Museum description error label");

    private final SelectField countrySelect = new SelectField(countries);

    public MuseumForm() {
        super($(byAttribute("data-testid", "modal-component")).as("Museum form"));
    }

    public MuseumForm(SelenideElement self) {
        super(self);
    }

    public void addNewMuseum(MuseumDTO museum) {
        fillForm(museum);
        addButton.click();
    }

    public void updateMuseum(MuseumDTO museum) {
        fillForm(museum);
        saveButton.click();
    }

    public void fillAndClose(MuseumDTO museum) {
        fillForm(museum);
        closeButton.click();
    }

    private void fillForm(MuseumDTO museum) {
        titleInput.setValue(museum.getTitle());
        descriptionInput.setValue(museum.getDescription());
        city.setValue(museum.getLocation().getCity());
        countrySelect.selectByExactName(museum.getLocation().getCountry().getName());
        photoInput.uploadFromClasspath(CFG.originalPhotoBaseDir() + museum.getPathToPhoto());
    }

    @Step("Check country select contains country with name: {countryTitle}")
    public void shouldContainsCountryTitle(String countryTitle) {
        countrySelect.shouldContainItem(countryTitle);
    }

    @Step("Check country select contains countries with name: {countryTitles}")
    public void shouldContainsCountryTitles(List<String> countryTitles) {
        countrySelect.shouldContainItems(countryTitles);
    }

    @Step("Check museum name error is visible")
    public MuseumForm shouldVisibleMuseumTitleError() {
        log.info("Check museum name error is visible");
        titleErrorLabel.shouldBe(visible);
        return this;
    }

    @Step("Check museum name error has expected text")
    public MuseumForm shouldMuseumTitleErrorHasText(String errorText) {
        log.info("Check museum name error has text = [{}]", errorText);
        titleErrorLabel.shouldHave(text(errorText));
        return this;
    }

    @Step("Check museum description error is visible")
    public MuseumForm shouldVisibleMuseumDescriptionError() {
        log.info("Check museum description error is visible");
        descriptionErrorLabel.shouldBe(visible);
        return this;
    }

    @Step("Check museum description error has expected text")
    public MuseumForm shouldMuseumDescriptionErrorHaveText(String errorText) {
        log.info("Check museum description error has text = [{}]", errorText);
        descriptionErrorLabel.shouldHave(text(errorText));
        return this;
    }

    @Step("Check museum photo error is visible")
    public MuseumForm shouldVisibleMuseumPhotoError() {
        log.info("Check museum photo error is visible");
        photoErrorLabel.shouldBe(visible);
        return this;
    }

    @Step("Check museum photo error has expected text")
    public MuseumForm shouldMuseumPhotoErrorHaveText(String errorText) {
        log.info("Check museum photo error has text = [{}]", errorText);
        photoErrorLabel.shouldHave(text(errorText));
        return this;
    }

    @Step("Check museum form has errors: [{errors}]")
    public MuseumForm shouldHaveErrors(String... errors) {
        log.info("Check museum form has visible errors");
        self.$$("form .text-error-400").as("Museum Errors").should(containExactTextsCaseSensitive(errors));
        return this;
    }

    @Override
    public MuseumForm shouldVisibleComponent() {
        header.shouldBe(visible).shouldHave(or("", text(ADD_MUSEUM_TITLE), text(UPDATE_MUSEUM_TITLE)));
        return this;
    }

    @Override
    public void shouldNotVisibleComponent() {
        header.shouldNot(or("header not visible or exist", visible, exist));
    }

}
