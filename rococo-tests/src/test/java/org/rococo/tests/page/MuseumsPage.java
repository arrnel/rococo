package org.rococo.tests.page;

import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;
import lombok.extern.slf4j.Slf4j;
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

    private final ItemsListComponent museumsList = new ItemsListComponent(MUSEUM, museumListContainer, searchFieldElement);
    private final MuseumPage museumPage = new MuseumPage();
    private final MuseumForm museumForm = new MuseumForm(museumFormContainer);

    @Step("Open museum by title: [{museumTitle}]")
    public MuseumPage openMuseum(String museumTitle) {
        museumsList.getByName(museumTitle).$("a").click();
        return new MuseumPage();
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

    @Step("Check museum exists: [{museumTitle}]")
    public MuseumsPage shouldExistMuseum(String museumTitle) {
        log.info("Check museum exists: [{}]", museumTitle);
        museumListContainer.shouldBe(visible);
        museumsList.shouldContainsItem(museumTitle);
        return this;
    }

    @Step("Check museum not exists: [{museumTitle}]")
    public MuseumsPage shouldNotExistMuseum(String museumTitle) {
        log.info("Check museum not exists: [{}]", museumTitle);
        museumListContainer.shouldBe(visible);
        museumsList.shouldNotContainItem(museumTitle);
        return this;
    }

    @Step("Check museums exists: [{museumTitle}]")
    public MuseumsPage shouldExistMuseums(List<String> museumsName) {
        log.info("Check museums exists: [{}]", museumsName);
        museumListContainer.shouldBe(visible);
        museumsList.shouldContainItems(museumsName);
        return this;
    }

    @Step("Check museums not exists: [{museumTitle}]")
    public MuseumsPage shouldNotExistMuseums(List<String> museumsName) {
        log.info("Check museums not exists: [{}]", museumsName);
        museumListContainer.shouldBe(visible);
        museumsList.shouldNotContainItems(museumsName);
        return this;
    }

    @Step("Check museums founded in search by query: [{museumTitle}]")
    public MuseumsPage shouldContainsMuseumsInQuerySearch(String query, List<String> museumsName) {
        log.info("Check museums not exists: [{}]", museumsName);
        museumListContainer.shouldBe(visible);
        museumsList.shouldContainsItemsByQuery(query, museumsName);
        return this;
    }

    @Step("Check museums not founded in search by query: [{museumTitle}]")
    public MuseumsPage shouldNotContainsMuseumsByQuery(String query, List<String> museumsName) {
        log.info("Check museums not exists: [{}]", museumsName);
        museumListContainer.shouldBe(visible);
        museumsList.shouldNotContainsItemsByQuery(query, museumsName);
        return this;
    }

    @Step("Check add new museum button not exists")
    public MuseumsPage shouldNotExistsAddNewMuseumButton() {
        log.info("Check add new museum button not exists");
        museumListContainer.shouldBe(visible);
        addNewMuseumButton.shouldNot(exist);
        return this;
    }

    @Step("Check add artist button not exists")
    public MuseumsPage shouldNotExistsAddNewArtistButton() {
        log.info("Check add artist button not exists");
        addNewMuseumButton.shouldNot(exist);
        return this;
    }

    @Step("Check museum empty list is displayed without filtering")
    public MuseumsPage shouldVisibleDefaultEmptyMuseumsList() {
        log.info("Check empty list is displayed without filtering");
        museumListContainer.shouldNot(exist);
        emptyMuseumListContainer.should(visible);
        return this;
    }

    @Step("Check museums empty list is displayed with filtering")
    public MuseumsPage shouldHaveEmptySearchResultByQuery(String query) {
        log.info("Check empty list is displayed with filtering");
        new SearchField().search(query);
        museumListContainer.shouldNot(exist);
        museumsList.shouldBeEmpty();
        return this;
    }

    @Override
    public MuseumsPage shouldVisiblePage() {
        pageTitle.shouldHave(text(PAGE_TITLE));
        return this;
    }

}
