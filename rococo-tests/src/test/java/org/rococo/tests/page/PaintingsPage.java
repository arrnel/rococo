package org.rococo.tests.page;

import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Allure;
import io.qameta.allure.Step;
import lombok.extern.slf4j.Slf4j;
import org.rococo.tests.ex.PaintingNotFoundException;
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

    private final SelenideElement pageTitle = root.$("h2").as("Paintings page title"),
            addNewPaintingButton = root.$(byText("Добавить картину")).as("Add new painting button"),
            searchFieldElement = root.$(byAttribute("data-testid", "search")),
            paintingListContainer = root.$(byAttribute("data-testid", "paintings-list")).as("Painting list component"),
            emptyPaintingListContainer = root.$(byAttribute("data-testid", "empty-paintings-list")).as("Empty painting list component");

    private final PaintingPage paintingPage = new PaintingPage();
    private final PaintingForm paintingForm = new PaintingForm();
    private final ItemsListComponent paintingsList = new ItemsListComponent(PAINTING, paintingListContainer);
    private final SearchField searchField = new SearchField(searchFieldElement);

    @Step("Search for painting by title: {paintingTitle}")
    private void searchPainting(String paintingTitle) {
        log.info("Searching painting by title: {}", paintingTitle);
        searchField.search(paintingTitle);
    }


    @Step("Open painting by title: {paintingTitle}")
    public PaintingPage openPainting(String paintingTitle) {
        searchPainting(paintingTitle);
        Allure.step("Click on painting [%s] link".formatted(paintingTitle), () ->
                paintingsList.getItemByName(paintingTitle)
                        .orElseThrow(() -> new PaintingNotFoundException(paintingTitle))
                        .$("a")
                        .click()
        );
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

    @Step("Update painting: {paintingTitle}")
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

    @Step("Check painting found: {paintingTitle}")
    public PaintingsPage shouldFoundPainting(String paintingTitle) {
        log.info("Check painting exists: {}", paintingTitle);
        searchPainting(paintingTitle);
        paintingsList.getItemByName(paintingTitle)
                .orElseThrow(() -> new AssertionError("Painting with title = [%s] not found".formatted(paintingTitle)));
        return this;
    }

    @Step("Check painting not found: {paintingTitle}")
    public PaintingsPage shouldNotFoundPainting(String paintingTitle) {
        log.info("Check painting not exists: {}", paintingTitle);
        searchPainting(paintingTitle);
        paintingsList.getItemByName(paintingTitle)
                .ifPresent(element -> element.shouldNot(exist));
        return this;
    }

    @Step("Check paintings exists: {paintingTitle}")
    public PaintingsPage shouldFoundPaintings(List<String> paintingsTitle) {
        log.info("Check paintings found: {}", paintingsTitle);
        var notFoundPaintings = paintingsTitle.stream()
                .filter(title -> {
                    try {
                        shouldNotFoundPainting(title);
                        return false;
                    } catch (AssertionError e) {
                        return true;
                    }
                })
                .toList();
        if (!notFoundPaintings.isEmpty())
            throw new AssertionError("The following paintings do not exist: " + notFoundPaintings);
        return this;
    }

    @Step("Check paintings not found")
    public PaintingsPage shouldNotFoundPaintings(List<String> paintingsTitle) {
        log.info("Check paintings not found: {}", paintingsTitle);
        var foundPaintings = paintingsTitle.stream()
                .filter(title -> {
                    try {
                        shouldFoundPainting(title);
                        return false;
                    } catch (AssertionError e) {
                        return true;
                    }
                })
                .toList();
        if (!foundPaintings.isEmpty())
            throw new AssertionError("The following paintings do not exist: " + foundPaintings);
        return this;
    }

    @Step("Check paintings founded in search by query: {paintingsTitle}")
    public PaintingsPage shouldFoundPaintings(String query, List<String> paintingsTitle) {
        log.info("Check paintings found in search results by query: {}. Paintings title list: {}", query, paintingsTitle);
        searchPainting(query);
        Allure.step("Verify search result by query [%s] contains paintings: [%s]".formatted(query, paintingsTitle), () -> {
            var missedPaintingsTitle = paintingsList.getMissedItemsName(paintingsTitle);
            if (!missedPaintingsTitle.isEmpty())
                throw new AssertionError("The following paintings do not exist: " + missedPaintingsTitle);
        });
        return this;
    }

    @Step("Check paintings not founded in search by query: {paintingsTitle}")
    public PaintingsPage shouldNotFoundPaintings(String query, List<String> paintingsTitle) {
        log.info("Check paintings not found in search results by query: {}. Paintings title list: {}", query, paintingsTitle);
        searchPainting(query);
        Allure.step("Verify search result by query [%s] not contains paintings: [%s]".formatted(query, paintingsTitle), () -> {
            var foundPaintingsTitle = paintingsList.getFoundItemsName(paintingsTitle);
            if (!foundPaintingsTitle.isEmpty())
                throw new AssertionError("The following paintings found: " + foundPaintingsTitle);
        });
        return this;
    }

    @Step("Check add new painting button not exists")
    public PaintingsPage shouldNotExistsAddNewPaintingButton() {
        log.info("Check add new painting button not exists");
        addNewPaintingButton.shouldNot(exist);
        return this;
    }

    @Step("Check painting empty list is displayed without filtering")
    public PaintingsPage shouldVisibleDefaultEmptyPaintingsList() {
        log.info("Check empty list is displayed without filtering");
        paintingListContainer.shouldNot(exist);
        emptyPaintingListContainer.should(visible);
        return this;
    }

    @Step("Check shows painting empty list by search: {query}")
    public PaintingsPage shouldHaveEmptySearchResult(String query) {
        log.info("Check shows painting empty list by search: {}", query);
        searchPainting(query);
        paintingListContainer.shouldNot(exist);
        paintingsList.shouldVisibleNoSearchResultMessage();
        return this;
    }

    @Override
    public PaintingsPage shouldVisiblePage() {
        pageTitle.shouldBe(visible).shouldHave(text("Картины"));
        return this;
    }
}
