package org.rococo.tests.page;

import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;
import lombok.extern.slf4j.Slf4j;
import org.rococo.tests.model.PaintingDTO;
import org.rococo.tests.page.component.ItemsListComponent;
import org.rococo.tests.page.component.SearchField;
import org.rococo.tests.page.form.PaintingForm;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selectors.byAttribute;
import static com.codeborne.selenide.Selectors.byText;
import static org.rococo.tests.enums.EntityType.PAINTING;

@Slf4j
@ParametersAreNonnullByDefault
public class PaintingsPage extends BasePage<PaintingsPage> {

    public static final String URL = BASE_URL + "/painting";

    private final SelenideElement title = root.$("h2").as("Paintings page title"),
            addNewPaintingButton = root.$(byText("Добавить картину")).as("Add new painting button"),
            searchFieldContainer = root.$(byAttribute("data-testid", "search")),
            paintingsListContainer = root.$(byAttribute("data-testid", "paintings-list")).as("Paintings list component"),
            paintingListContainer = root.$(byAttribute("data-testid", "paintings-list")).as("Artist list component"),
            emptyPaintingListContainer = root.$(byAttribute("data-testid", "empty-paintings-list")).as("Artist list component");

    private final PaintingPage paintingPage = new PaintingPage();
    private final PaintingForm paintingForm = new PaintingForm();
    private final ItemsListComponent paintingsList = new ItemsListComponent(PAINTING, paintingsListContainer, searchFieldContainer);

    @Step("Open painting by name: [{paintingTitle}]")
    public PaintingPage openPainting(String paintingTitle) {
        paintingsList.getByName(paintingTitle).click();
        return new PaintingPage();
    }

    @Step("Add new painting: {painting.title}")
    public PaintingsPage addNewPainting(PaintingDTO painting) {
        log.info("Add new painting: {}", painting.getTitle());
        openAddNewPaintingForm();
        paintingForm.addNewPainting(painting);
        paintingForm.shouldNotVisibleComponent();
        return this;
    }

    @Step("Add new painting: {painting.title}")
    public PaintingForm addNewPaintingWithError(PaintingDTO painting) {
        log.info("Add new painting: {}", painting.getTitle());
        openAddNewPaintingForm();
        paintingForm.addNewPainting(painting);
        return paintingForm;
    }

    @Step("Update painting: {painting.title}")
    public PaintingPage updatePainting(String paintingTitle, PaintingDTO painting) {
        log.info("Update painting: {}", paintingTitle);
        openPainting(paintingTitle);
        return paintingPage.updatePainting(painting);
    }

    @Step("Update painting: {painting.title}")
    public PaintingForm updatePaintingWithError(String paintingTitle, PaintingDTO painting) {
        openPainting(paintingTitle);
        return paintingPage.updatePaintingWithError(painting);
    }

    @Step("Open add new painting form")
    private PaintingForm openAddNewPaintingForm() {
        addNewPaintingButton.click();
        paintingForm.shouldVisibleComponent();
        return new PaintingForm();
    }

    @Step("Check painting exists: [{paintingTitle}]")
    public PaintingsPage shouldExistPainting(String paintingTitle) {
        log.info("Check painting exists: [{}]", paintingTitle);
        paintingsList.shouldContainsItem(paintingTitle);
        return this;
    }

    @Step("Check painting not exists: [{paintingTitle}]")
    public PaintingsPage shouldNotExistPainting(String paintingTitle) {
        log.info("Check painting not exists: [{}]", paintingTitle);
        paintingsList.shouldNotContainItem(paintingTitle);
        return this;
    }

    @Step("Check paintings exists: [{paintingTitle}]")
    public PaintingsPage shouldExistPaintings(List<String> paintingsName) {
        log.info("Check paintings exists: [{}]", paintingsName);
        paintingsList.shouldContainItems(paintingsName);
        return this;
    }

    @Step("Check paintings not exists: [{paintingTitle}]")
    public PaintingsPage shouldNotExistPaintings(List<String> paintingsName) {
        log.info("Check paintings not exists: [{}]", paintingsName);
        paintingsList.shouldNotContainItems(paintingsName);
        return this;
    }

    @Step("Check paintings founded in search by query: [{paintingTitle}]")
    public PaintingsPage shouldContainsPaintingsInQuerySearch(String query, List<String> paintingsName) {
        log.info("Check paintings not exists: [{}]", paintingsName);
        paintingsList.shouldContainsItemsByQuery(query, paintingsName);
        return this;
    }

    @Step("Check paintings not founded in search by query: [{paintingTitle}]")
    public PaintingsPage shouldNotContainsItemsByQuery(String query, List<String> paintingsName) {
        log.info("Check paintings not exists: [{}]", paintingsName);
        paintingsList.shouldNotContainsItemsByQuery(query, paintingsName);
        return this;
    }

    @Step("Check add new painting button not exists")
    public void shouldNotExistsAddNewPaintingButton() {
        log.info("Check add new painting button not exists");
        addNewPaintingButton.shouldNot(exist);
    }

    @Step("Check painting empty list is displayed without filtering")
    public PaintingsPage shouldVisibleDefaultEmptyPaintingsList() {
        log.info("Check empty list is displayed without filtering");
        paintingListContainer.shouldNot(exist);
        emptyPaintingListContainer.should(visible);
        return this;
    }

    @Step("Check painting empty list is displayed with filtering")
    public PaintingsPage shouldHaveEmptySearchResult() {
        log.info("Check empty list is displayed with filtering");
        paintingListContainer.shouldNot(exist);
        paintingsList.shouldBeEmpty();
        return this;
    }

    @Step("Check museums empty list is displayed with filtering")
    public void shouldHaveEmptySearchResultByQuery(String query) {
        log.info("Check empty list is displayed with filtering");
        new SearchField().search(query);
        paintingListContainer.shouldNot(exist);
        paintingsList.shouldBeEmpty();
    }

    @Override
    public PaintingsPage shouldVisiblePage() {
        title.shouldBe(visible).shouldHave(text("Картины"));
        return this;
    }
}
