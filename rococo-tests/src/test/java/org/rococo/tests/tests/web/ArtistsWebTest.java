package org.rococo.tests.tests.web;

import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import net.datafaker.Faker;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.rococo.tests.jupiter.annotation.*;
import org.rococo.tests.jupiter.annotation.meta.WebTest;
import org.rococo.tests.model.ArtistDTO;
import org.rococo.tests.page.ArtistPage;
import org.rococo.tests.page.ArtistsPage;
import org.rococo.tests.page.form.ArtistForm;
import org.rococo.tests.util.DataGenerator;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

import static com.codeborne.selenide.Selenide.open;

@WebTest
@Feature("WEB")
@Story("[WEB] Artists tests")
@DisplayName("[WEB] Artists tests")
@ParametersAreNonnullByDefault
class ArtistsWebTest {

    private static final Faker FAKE = new Faker();
    private static final String
            IMG_1 = "img.jpg",
            IMG_1_EXPECTED = "artist/img.jpg",
            IMG_2 = "img.png",
            IMG_2_EXPECTED = "artist/img.png",
            ILLEGAL_FORMAT_IMG = "img.gif";

    private final ArtistsPage artistsPage = new ArtistsPage();
    private final ArtistPage artistPage = new ArtistPage();
    private final ArtistForm artistForm = new ArtistForm();

    @ApiLogin(@User)
    @Test
    @DisplayName("Should add new artist with correct data")
    void shouldAddArtistWithCorrectDataTest() {
        // Data
        var artist = DataGenerator.generateArtist()
                .setPathToPhoto(IMG_1);

        // Steps
        open(ArtistsPage.URL, ArtistsPage.class)
                .addNewArtist(artist);

        // Assertions
        artistsPage.shouldExistArtist(artist.getName());
    }

    @Test
    @DisplayName("Check add new artist button not exists without authorization")
    void shouldNotAvailableAddArtistWithoutAuthorizationTest() {
        // Steps
        open(ArtistsPage.URL, ArtistsPage.class)
                .shouldVisiblePage();

        // Assertions
        artistsPage.shouldNotExistsAddNewArtistButton();
    }

    @ApiLogin(@User)
    @ParameterizedTest(name = "Case: [{0}]")
    @MethodSource("org.rococo.tests.tests.web.data.DataProvider#validArtistData")
    @DisplayName("Check artist creates if data length is valid")
    void shouldCreateArtistWithValidLengthDataTest(String caseName, int artistNameLength, int artistBioLength) {
        // Data
        var artistName = FAKE.lorem().characters(artistNameLength);
        var artistBio = FAKE.lorem().characters(artistBioLength);
        var artist = DataGenerator.generateArtist()
                .setName(artistName)
                .setBiography(artistBio);

        // Steps
        open(ArtistsPage.URL, ArtistsPage.class)
                .addNewArtist(artist);

        // Assertions
        artistsPage.shouldExistArtist(artistName);
    }

    @ApiLogin(@User)
    @ParameterizedTest(name = "Case: [{0}]")
    @MethodSource("org.rococo.tests.tests.web.data.DataProvider#invalidArtistData")
    @DisplayName("Check errors visible on add new artist form if fields have greater than max characters length")
    void shouldDisplayErrorsOnAddAristFormIfArtistFieldsHaveGreaterThanMaxLengthTest(String caseName,
                                                                                     int artistNameLength,
                                                                                     int artistBioLength,
                                                                                     String[] errors
    ) {
        // Data
        var artistName = FAKE.lorem().characters(artistNameLength);
        var artistBio = FAKE.lorem().characters(artistBioLength);
        var artist = DataGenerator.generateArtist()
                .setName(artistName)
                .setBiography(artistBio);

        // Steps
        open(ArtistsPage.URL, ArtistsPage.class)
                .addNewArtistWithError(artist);

        // Assertions
        artistForm.shouldHaveErrors(errors);
    }

    @ApiLogin(@User)
    @Test
    @DisplayName("Check errors visible on add new artist form if fields image has invalid format")
    void shouldDisplayErrorOnAddArtistFormWhenUploadingInvalidImageFormat() {
        // Data
        var artist = DataGenerator.generateArtist()
                .setPathToPhoto(ILLEGAL_FORMAT_IMG);

        // Steps
        open(ArtistsPage.URL, ArtistsPage.class)
                .addNewArtistWithError(artist);

        // Assertions
        artistForm.shouldHaveErrors("Допустимые форматы изображений: '.jpg', '.jpeg', '.png'");
    }

    @ApiLogin(@User)
    @Artist
    @Test
    @DisplayName("Should update artist with correct data")
    void shouldUpdateArtistWithCorrectDataTest(ArtistDTO artist) {
        // Data
        var newArtist = DataGenerator.generateArtist()
                .setPathToPhoto(IMG_2);

        // Steps
        open(ArtistsPage.URL, ArtistsPage.class)
                .updateArtist(artist.getName(), newArtist);

        // Assertions
        artistPage.shouldHaveName(newArtist.getName())
                .shouldHaveBio(newArtist.getBiography())
                .shouldHaveScreenshot(IMG_2_EXPECTED, 0.07);
    }

    @Artist
    @Test
    @DisplayName("Check update artist button not exists without authorization")
    void shouldNotAvailableUpdateArtistWithoutAuthorizationTest(ArtistDTO artist) {
        // Steps
        open(ArtistsPage.URL, ArtistsPage.class)
                .openArtist(artist.getName());

        // Assertions
        artistPage.shouldNotExistsUpdateArtistButton();
    }

    @ApiLogin(@User)
    @Artist
    @ParameterizedTest(name = "Case: [{0}]")
    @MethodSource("org.rococo.tests.tests.web.data.DataProvider#validArtistData")
    @DisplayName("Check artist updates if data length is valid")
    void shouldUpdateArtistWithValidLengthDataTest(String caseName,
                                                   int artistNameLength,
                                                   int artistBioLength,
                                                   ArtistDTO artist
    ) {
        // Data
        var artistName = FAKE.lorem().characters(artistNameLength);
        var artistBio = FAKE.lorem().characters(artistBioLength);
        var newArtist = DataGenerator.generateArtist()
                .setName(artistName)
                .setBiography(artistBio);

        // Steps
        open(ArtistsPage.URL, ArtistsPage.class)
                .updateArtist(artist.getName(), newArtist);

        // Assertions
        artistPage.shouldHaveName(newArtist.getName())
                .shouldHaveBio(newArtist.getBiography());
    }

    @ApiLogin(@User)
    @Artist
    @ParameterizedTest(name = "Case: [{0}]")
    @MethodSource("org.rococo.tests.tests.web.data.DataProvider#invalidArtistData")
    @DisplayName("Check errors visible on update artist form if fields have greater than max characters length")
    void shouldVisibleErrorsOnArtistUpdateFormIfArtistFieldsHaveGreaterThanMaxLengthTest(
            String caseName,
            int artistNameLength,
            int artistBioLength,
            String[] errors,
            ArtistDTO artist
    ) {
        // Data
        var artistName = FAKE.lorem().characters(artistNameLength);
        var artistBio = FAKE.lorem().characters(artistBioLength);
        var newArtist = DataGenerator.generateArtist()
                .setName(artistName)
                .setBiography(artistBio);

        // Steps
        open(ArtistsPage.URL, ArtistsPage.class)
                .updateArtistWithError(artist.getName(), newArtist);

        // Assertions
        artistForm.shouldHaveErrors(errors);
    }


    @ApiLogin(@User)
    @Artist
    @Test
    @DisplayName("Check error displayed on update artist form when uploading image with invalid format")
    void shouldDisplayErrorOnUpdateArtistFormWhenUploadingInvalidImageFormat() {
        // Data
        var artist = DataGenerator.generateArtist()
                .setPathToPhoto(ILLEGAL_FORMAT_IMG);

        // Steps
        open(ArtistsPage.URL, ArtistsPage.class)
                .addNewArtistWithError(artist);

        // Assertions
        artistForm.shouldHaveErrors("Допустимые форматы изображений: '.jpg', '.jpeg', '.png'");
    }

    @ApiLogin(@User)
    @Artists({
            @Artist(name = "Vincent Willem van Gogh"),
            @Artist(name = "Vanessa Cooper"),
            @Artist(name = "Ryan Sullivan")
    })
    @Test
    @DisplayName("Check artists found by filtered search")
    void shouldFindArtistsWithFilterTest(List<ArtistDTO> artists) {
        // Data
        var query = "vAn";
        var expectedArtistNames = artists.stream()
                .map(ArtistDTO::getName)
                .toList();

        // Steps & Assertion
        open(ArtistsPage.URL, ArtistsPage.class)
                .shouldContainsArtistsInQuerySearch(query, expectedArtistNames);
    }

    @Test
    @DisplayName("Check displayed empty filtered list container if artist not founded by query")
    void shouldDisplayArtistAfterFilteringByNameTest() {
        // Steps & Assertion
        open(ArtistsPage.URL, ArtistsPage.class)
                .shouldHaveEmptySearchResultByQuery(FAKE.lorem().paragraph());
    }

    @DisabledByIssue(issueId = "32")
    @Test
    @DisplayName("Check displayed default empty list if artist not exists")
    void shouldDisplayEmptyListWhenArtistsNotExistsTest() {
        // Steps & Assertion
        open(ArtistsPage.URL, ArtistsPage.class)
                .shouldVisibleDefaultEmptyMuseumsList();
    }

}
