package org.rococo.tests.page;

import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;
import lombok.extern.slf4j.Slf4j;
import org.rococo.tests.model.ArtistDTO;
import org.rococo.tests.page.component.ItemsListComponent;
import org.rococo.tests.page.component.SearchField;
import org.rococo.tests.page.form.ArtistForm;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selectors.byAttribute;
import static com.codeborne.selenide.Selectors.byText;
import static org.rococo.tests.enums.EntityType.ARTIST;

@Slf4j
@ParametersAreNonnullByDefault
public class ArtistsPage extends BasePage<ArtistsPage> {

    public static final String URL = BASE_URL + "/artist";
    private static final String ARTISTS_PAGE_TITLE = "Художники";

    private final SelenideElement header = root.$("h2").as("Artists page title"),
            searchFieldContainer = root.$(byAttribute("data-testid", "search")),
            addNewArtistButton = root.$(byText("Добавить художника")).as("Add new artist button"),
            artistListContainer = root.$(byAttribute("data-testid", "artists-list")).as("Artist list component"),
            emptyArtistListContainer = root.$(byAttribute("data-testid", "empty-artists-list")).as("Artist list component");

    private final ArtistForm artistForm = new ArtistForm();
    private final ArtistPage artistPage = new ArtistPage();
    private final ItemsListComponent artistsList = new ItemsListComponent(ARTIST, artistListContainer, searchFieldContainer);

    @Step("Open artist by name: [{artistName}]")
    public ArtistPage openArtist(String artistName) {
        artistsList.getByName(artistName).click();
        return artistPage;
    }

    @Step("Add new artist: {artist.name}")
    public ArtistsPage addNewArtist(ArtistDTO artist) {
        log.info("Add new artist: {}", artist.getName());
        openAddNewArtistForm();
        artistForm.addNewArtist(artist);
        artistForm.shouldNotVisibleComponent();
        return this;
    }

    @Step("Add new artist: {artist.name}")
    public ArtistForm addNewArtistWithError(ArtistDTO artist) {
        log.info("Add new artist: {}", artist.getName());
        openAddNewArtistForm();
        artistForm.addNewArtist(artist);
        return artistForm;
    }

    @Step("Update artist: {artist.name}")
    public ArtistPage updateArtist(String artistName, ArtistDTO artist) {
        log.info("Update artist: {}", artistName);
        artistsList.getByName(artistName).click();
        artistPage.editArtist(artist);
        artistForm.shouldNotVisibleComponent();
        return artistPage;
    }

    @Step("Update artist: {artist.name}")
    public ArtistForm updateArtistWithError(String artistName, ArtistDTO artist) {
        log.info("Update artist: {}", artistName);
        artistsList.getByName(artistName).click();
        artistPage.editArtist(artist);
        return artistForm;
    }

    @Step("Open add new artist form")
    private ArtistForm openAddNewArtistForm() {
        addNewArtistButton.click();
        artistForm.shouldVisibleComponent();
        return artistForm;
    }

    @Step("Check artist exists: [{artistName}]")
    public ArtistsPage shouldExistArtist(String artistName) {
        log.info("Check artist exists: [{}]", artistName);
        artistsList.shouldContainsItem(artistName);
        return this;
    }

    @Step("Check artist not exists: [{artistName}]")
    public ArtistsPage shouldNotExistArtist(String artistName) {
        log.info("Check artist not exists: [{}]", artistName);
        artistsList.shouldNotContainItem(artistName);
        return this;
    }

    @Step("Check artists exists: [{artistName}]")
    public ArtistsPage shouldExistArtists(List<String> artistsName) {
        log.info("Check artists exists: [{}]", artistsName);
        artistsList.shouldContainItems(artistsName);
        return this;
    }

    @Step("Check artists not exists: [{artistName}]")
    public ArtistsPage shouldNotExistArtists(List<String> artistsName) {
        log.info("Check artist not exists: [{}]", artistsName);
        artistsList.shouldNotContainItems(artistsName);
        return this;
    }

    @Step("Check artists founded in search by query: [{artistName}]")
    public ArtistsPage shouldContainsArtistsInQuerySearch(String query, List<String> artistsName) {
        log.info("Check artist not exists: [{}]", artistsName);
        artistsList.shouldContainsItemsByQuery(query, artistsName);
        return this;
    }

    @Step("Check artists not founded in search by query: [{artistName}]")
    public ArtistsPage shouldNotContainsItemsByQuery(String query, List<String> artistsName) {
        log.info("Check artist not exists: [{}]", artistsName);
        artistsList.shouldNotContainsItemsByQuery(query, artistsName);
        return this;
    }

    @Step("Check add artist button not exists")
    public ArtistsPage shouldNotExistsAddNewArtistButton() {
        log.info("Check add artist button not exists");
        addNewArtistButton.shouldNot(exist);
        return this;
    }

    @Step("Check artist empty list is displayed without filtering")
    public ArtistsPage shouldVisibleDefaultEmptyMuseumsList() {
        log.info("Check empty list is displayed without filtering");
        artistListContainer.shouldNot(exist);
        emptyArtistListContainer.should(visible);
        return this;
    }

    @Step("Check artist empty list is displayed with filtering")
    public ArtistsPage shouldHaveEmptySearchResultByQuery(String query) {
        log.info("Check empty list is displayed with filtering");
        new SearchField().search(query);
        artistListContainer.shouldNot(exist);
        artistsList.shouldBeEmpty();
        return this;
    }

    @Override
    public ArtistsPage shouldVisiblePage() {
        header.shouldBe(visible).shouldHave(text(ARTISTS_PAGE_TITLE));
        return this;
    }
}
