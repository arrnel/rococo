package org.rococo.tests.page;

import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Allure;
import io.qameta.allure.Step;
import lombok.extern.slf4j.Slf4j;
import org.rococo.tests.ex.ArtistNotFoundException;
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
            searchFieldElement = root.$(byAttribute("data-testid", "search")),
            addNewArtistButton = root.$(byText("Добавить художника")).as("Add new artist button"),
            artistListContainer = root.$(byAttribute("data-testid", "artists-list")).as("Artist list component");

    private final ArtistForm artistForm = new ArtistForm();
    private final ArtistPage artistPage = new ArtistPage();
    private final ItemsListComponent artistsList = new ItemsListComponent(ARTIST, artistListContainer);
    private final SearchField searchField = new SearchField(searchFieldElement);

    @Step("Search for artist by name: {artistName}")
    private void searchArtist(String artistName) {
        log.info("Searching artist by name: {}", artistName);
        searchField.search(artistName);
    }

    @Step("Open artist page: {artistName}")
    public ArtistPage openArtist(String artistName) {
        searchArtist(artistName);
        Allure.step("Click on artist [%s] link".formatted(artistName), () ->
                artistsList.getItemByName(artistName)
                        .orElseThrow(() -> new ArtistNotFoundException(artistName))
                        .$("a")
                        .click()
        );
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

    @Step("Update artist: {artistName}")
    public ArtistPage updateArtist(String artistName, ArtistDTO artist) {
        log.info("Update artist: {}", artistName);
        openArtist(artistName);
        artistPage.editArtist(artist);
        artistForm.shouldNotVisibleComponent();
        return artistPage;
    }

    @Step("Update artist: {artistName}")
    public ArtistForm updateArtistWithError(String artistName, ArtistDTO artist) {
        log.info("Update artist: {}", artistName);
        openArtist(artistName);
        artistPage.editArtist(artist);
        return artistForm;
    }

    @Step("Open add new artist form")
    private ArtistForm openAddNewArtistForm() {
        addNewArtistButton.click();
        artistForm.shouldVisibleComponent();
        return artistForm;
    }

    @Step("Check artist exists: {artistName}")
    public ArtistsPage shouldFoundArtist(String artistName) {
        log.info("Check artist exists: {}", artistName);
        searchArtist(artistName);
        artistsList.getItemByName(artistName)
                .orElseThrow(() -> new AssertionError("Artist with name [%s] not found".formatted(artistName)));
        return this;
    }

    @Step("Check artist not found: {artistName}")
    public ArtistsPage shouldNotFoundArtist(String artistName) {
        log.info("Check artist not exists: {}", artistName);
        searchArtist(artistName);
        artistsList.getItemByName(artistName)
                .ifPresent(element -> element.shouldNot(exist));
        return this;
    }

    @Step("Check artists found")
    public ArtistsPage shouldFoundArtists(List<String> artistsName) {
        log.info("Check artists found: {}", artistsName);
        var notFoundArtists = artistsName.stream()
                .filter(title -> {
                    try {
                        shouldNotFoundArtist(title);
                        return false;
                    } catch (AssertionError e) {
                        return true;
                    }
                })
                .toList();
        if (!notFoundArtists.isEmpty())
            throw new AssertionError("The following artists do not exist: " + notFoundArtists);
        return this;
    }

    @Step("Check artists not found")
    public ArtistsPage shouldNotFoundArtists(List<String> artistsName) {
        log.info("Check artists not found: {}", artistsName);
        var foundArtists = artistsName.stream()
                .filter(title -> {
                    try {
                        shouldFoundArtist(title);
                        return false;
                    } catch (AssertionError e) {
                        return true;
                    }
                })
                .toList();
        if (!foundArtists.isEmpty())
            throw new AssertionError("The following artists do not exist: " + foundArtists);
        return this;
    }

    @Step("Check artists found in search by query")
    public ArtistsPage shouldFoundArtists(String query, List<String> artistsName) {
        log.info("Check artists found in search results by query: {}. Artists name list: {}", query, artistsName);
        searchArtist(query);
        Allure.step("Verify search result by query [%s] contains artists: [%s]".formatted(query, artistsName), () -> {
            var missedArtistsName = artistsList.getMissedItemsName(artistsName);
            if (!missedArtistsName.isEmpty())
                throw new AssertionError("The following artists do not exist: " + missedArtistsName);
        });
        return this;
    }

    @Step("Check artists not found in search by query")
    public ArtistsPage shouldNotFoundArtists(String query, List<String> artistsName) {
        log.info("Check artists not found in search results by query: {}. Artists name list: {}", query, artistsName);
        searchArtist(query);
        Allure.step("Verify search result by query [%s] not contains artists: [%s]".formatted(query, artistsName), () -> {
            var foundArtistsName = artistsList.getFoundItemsName(artistsName);
            if (!foundArtistsName.isEmpty())
                throw new AssertionError("The following artists found: " + foundArtistsName);
        });
        return this;
    }

    @Step("Check add artist button not found")
    public ArtistsPage shouldNotExistAddNewArtistButton() {
        log.info("Check add artist button not found");
        addNewArtistButton.shouldNot(exist);
        return this;
    }

    @Step("Check artist empty list is displayed without filtering")
    public ArtistsPage shouldVisibleDefaultEmptyMuseumsList() {
        log.info("Check empty list is displayed without filtering");
        artistListContainer.shouldNot(exist);
        artistsList.shouldVisibleInitialEmptyListMessage();
        return this;
    }

    @Step("Check shows artist empty list by search: {query}")
    public ArtistsPage shouldHaveEmptySearchResult(String query) {
        log.info("Check shows artist empty list by search: {}", query);
        searchArtist(query);
        artistListContainer.shouldNot(exist);
        artistsList.shouldVisibleNoSearchResultMessage();
        return this;
    }

    @Override
    public ArtistsPage shouldVisiblePage() {
        header.shouldBe(visible).shouldHave(text(ARTISTS_PAGE_TITLE));
        return this;
    }
}
