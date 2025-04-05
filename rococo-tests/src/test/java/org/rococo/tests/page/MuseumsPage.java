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

    @Step("Open museum by title: [{museumName}]")
    public MuseumPage openMuseum(String museumName) {
        museumsList.getByName(museumName).click();
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

    @Step("Update museum: {museum.title}")
    public MuseumsPage updateMuseum(String museumName, MuseumDTO museum) {
        log.info("Update museum: {}", museumName);
        museumsList.getByName(museumName).click();
        museumPage.updateMuseum(museum);
        museumForm.shouldNotVisibleComponent();
        return this;
    }

    @Step("Update museum: {museum.title}")
    public MuseumForm updateMuseumWithError(String museumName, MuseumDTO museum) {
        log.info("Update museum: {}", museumName);
        museumsList.getByName(museumName).click();
        museumPage.updateMuseumWithError(museum);
        return museumForm;
    }

    @Step("Open add new museum form")
    private MuseumForm openAddNewMuseumForm() {
        addNewMuseumButton.click();
        museumForm.shouldVisibleComponent();
        return new MuseumForm();
    }

    @Step("Check museum exists: [{museumName}]")
    public MuseumsPage shouldExistMuseum(String museumName) {
        log.info("Check museum exists: [{}]", museumName);
        museumsList.shouldContainsItem(museumName);
        return this;
    }

    @Step("Check museum not exists: [{museumName}]")
    public MuseumsPage shouldNotExistMuseum(String museumName) {
        log.info("Check museum not exists: [{}]", museumName);
        museumsList.shouldNotContainItem(museumName);
        return this;
    }

    @Step("Check museums exists: [{museumName}]")
    public MuseumsPage shouldExistMuseums(List<String> museumsName) {
        log.info("Check museums exists: [{}]", museumsName);
        museumsList.shouldContainItems(museumsName);
        return this;
    }

    @Step("Check museums not exists: [{museumName}]")
    public MuseumsPage shouldNotExistMuseums(List<String> museumsName) {
        log.info("Check museum not exists: [{}]", museumsName);
        museumsList.shouldNotContainItems(museumsName);
        return this;
    }

    @Step("Check museums founded in search by query: [{museumName}]")
    public MuseumsPage shouldContainsMuseumsInQuerySearch(String query, List<String> museumsName) {
        log.info("Check museum not exists: [{}]", museumsName);
        museumsList.shouldContainsItemsByQuery(query, museumsName);
        return this;
    }

    @Step("Check museums not founded in search by query: [{museumName}]")
    public MuseumsPage shouldNotContainsMuseumsByQuery(String query, List<String> museumsName) {
        log.info("Check museum not exists: [{}]", museumsName);
        museumsList.shouldNotContainsItemsByQuery(query, museumsName);
        return this;
    }

    @Step("Check add new museum button not exists")
    public MuseumsPage shouldNotExistsAddNewMuseumButton() {
        log.info("Check add new museum button not exists");
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
