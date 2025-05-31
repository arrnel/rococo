package org.rococo.tests.page;

import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Allure;
import io.qameta.allure.Step;
import lombok.extern.slf4j.Slf4j;
import org.rococo.tests.ex.MuseumNotFoundException;
import org.rococo.tests.model.MuseumDTO;
import org.rococo.tests.page.component.ItemsListComponent;
import org.rococo.tests.page.component.SearchField;
import org.rococo.tests.page.form.MuseumForm;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selectors.byAttribute;
import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.$;
import static org.rococo.tests.enums.EntityType.MUSEUM;

@Slf4j
@ParametersAreNonnullByDefault
public class MuseumsPage extends BasePage<MuseumsPage> {

    public static final String URL = BASE_URL + "/museum";
    private static final String PAGE_TITLE = "Музеи";

    private final SelenideElement pageTitle = root.$("h2").as("Museum title"),
            addNewMuseumButton = root.$(byText("Добавить музей")).as("Add new museum"),
            museumListContainer = root.$(byAttribute("data-testid", "museums-list")),
            emptyMuseumListContainer = root.$(byAttribute("data-testid", "empty-museums-list")),
            searchFieldElement = root.$(byAttribute("data-testid", "search")).as("Search field"),
            museumFormContainer = $(byAttribute("data-testid", "modal-component")).as("Edit museum form");

    private final MuseumPage museumPage = new MuseumPage();
    private final MuseumForm museumForm = new MuseumForm(museumFormContainer);
    private final ItemsListComponent museumsList = new ItemsListComponent(MUSEUM, museumListContainer);
    private final SearchField searchField = new SearchField(searchFieldElement);

    @Step("Search for museum by title: [{museumTitle}]")
    private void searchMuseum(String museumTitle) {
        log.info("Searching museum by title: {}", museumTitle);
        searchField.search(museumTitle);
    }

    @Step("Open museum by title: [{museumTitle}]")
    public MuseumPage openMuseum(String museumTitle) {
        searchMuseum(museumTitle);
        Allure.step("Click on museum [%s] link".formatted(museumTitle), () ->
                museumsList.getItemByName(museumTitle)
                        .orElseThrow(() -> new MuseumNotFoundException(museumTitle))
                        .$("a")
                        .click()
        );
        return museumPage;
    }

    @Step("Add new museum: {museum.title}")
    public MuseumsPage addNewMuseum(MuseumDTO museum) {
        log.info("Add new museum: {}", museum.getTitle());
        openAddNewMuseumForm();
        museumForm.addNewMuseum(museum);
        museumForm.shouldNotVisibleComponent();
        return this;
    }

    @Step("Add new museum: {museum.title}")
    public MuseumForm addNewMuseumWithError(MuseumDTO museum) {
        log.info("Add new museum: {}", museum.getTitle());
        openAddNewMuseumForm();
        museumForm.addNewMuseum(museum);
        return museumForm;
    }

    @Step("Update museum: {museumTitle}")
    public MuseumsPage updateMuseum(String museumTitle, MuseumDTO museum) {
        log.info("Update museum: {}", museumTitle);
        openMuseum(museumTitle);
        museumPage.updateMuseum(museum);
        museumForm.shouldNotVisibleComponent();
        return this;
    }

    @Step("Update museum: {museumTitle}")
    public MuseumForm updateMuseumWithError(String museumTitle, MuseumDTO museum) {
        log.info("Update museum: {}", museumTitle);
        openMuseum(museumTitle);
        museumPage.updateMuseumWithError(museum);
        return museumForm;
    }

    @Step("Open add new museum form")
    private MuseumForm openAddNewMuseumForm() {
        addNewMuseumButton.click();
        museumForm.shouldVisibleComponent();
        return new MuseumForm();
    }

    @Step("Check museum found: [{museumTitle}]")
    public MuseumsPage shouldFoundMuseum(String museumTitle) {
        log.info("Check museum found: [{}]", museumTitle);
        museumsList.getItemByName(museumTitle)
                .orElseThrow(() -> new AssertionError("Museum with title [%s] not found".formatted(museumTitle)))
                .shouldBe(visible);
        return this;
    }

    @Step("Check museum not found: [{museumTitle}]")
    public MuseumsPage shouldNotFoundMuseum(String museumTitle) {
        log.info("Check museum not found: [{}]", museumTitle);
        museumsList.getItemByName(museumTitle)
                .ifPresent(element -> {
                    throw new AssertionError("Museum with title [" + museumTitle + "] exists");
                });
        return this;
    }

    @Step("Check museums found: [{museumTitle}]")
    public MuseumsPage shouldFoundMuseums(List<String> museumsTitle) {
        log.info("Check museums found: [{}]", museumsTitle);
        var notFoundMuseums = museumsTitle.stream()
                .filter(title -> {
                            try {
                                shouldNotFoundMuseum(title);
                                return false;
                            } catch (AssertionError e) {
                                return true;
                            }
                        }
                )
                .toList();
        if (!notFoundMuseums.isEmpty())
            throw new AssertionError("The following museums do not exist: " + notFoundMuseums);
        return this;
    }

    @Step("Check museums not found")
    public MuseumsPage shouldNotFoundMuseums(List<String> museumsTitle) {
        log.info("Check museums not found: [{}]", museumsTitle);
        var foundMuseums = museumsTitle.stream()
                .filter(title -> {
                            try {
                                shouldFoundMuseum(title);
                                return false;
                            } catch (AssertionError e) {
                                return true;
                            }
                        }
                )
                .toList();
        if (!foundMuseums.isEmpty())
            throw new AssertionError("The following museums do not exist: " + foundMuseums);
        return this;
    }

    @Step("Check museums found in search by query: [{query}]")
    public MuseumsPage shouldFoundMuseums(String query, List<String> museumsTitle) {

        log.info("Check museums found by query: {}. Museums: [{}]", query, museumsTitle);

        searchMuseum(query);

        Allure.step("Verify search result by query [%s] contains museums: [%s]".formatted(query, museumsTitle), () -> {
            var missedMuseumsTitle = museumsList.getMissedItemsName(museumsTitle);
            if (!missedMuseumsTitle.isEmpty())
                throw new AssertionError("The following museums do not exist: " + missedMuseumsTitle);
        });

        return this;

    }

    @Step("Check museums not found in search by query: [{query}]")
    public MuseumsPage shouldNotFoundMuseums(String query, List<String> museumsTitle) {

        log.info("Check museums not found by query: {}. Museums[{}]", query, museumsTitle);

        searchMuseum(query);

        Allure.step("Verify search result by query [%s] not contains museums: [%s]".formatted(query, museumsTitle), () -> {
            var foundMuseumsTitle = museumsList.getFoundItemsName(museumsTitle);
            if (!foundMuseumsTitle.isEmpty())
                throw new AssertionError("The following museums exists: " + foundMuseumsTitle);
        });

        return this;

    }

    @Step("Check add new museum button not exists")
    public MuseumsPage shouldNotExistAddNewMuseumButton() {
        log.info("Check add new museum button not exists");
        museumListContainer.shouldBe(visible);
        addNewMuseumButton.shouldNot(exist);
        return this;
    }

    @Step("Check museum empty list is displayed without filtering")
    public MuseumsPage shouldVisibleDefaultEmptyMuseumsList() {
        log.info("Check empty list is displayed without filtering");
        museumListContainer.shouldNot(exist);
        museumsList.shouldVisibleInitialEmptyListMessage();
        return this;
    }

    @Step("Check museums empty list is displayed with filtering")
    public MuseumsPage shouldHaveEmptySearchResult(String query) {
        log.info("Check shows museum empty list by search: {}", query);
        searchMuseum(query);
        museumListContainer.shouldNot(exist);
        museumsList.shouldVisibleNoSearchResultMessage();
        return this;
    }

    @Override
    public MuseumsPage shouldVisiblePage() {
        pageTitle.shouldHave(text(PAGE_TITLE));
        return this;
    }

}
