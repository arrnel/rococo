package org.rococo.tests.tests.web;

import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import net.datafaker.Faker;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.rococo.tests.jupiter.annotation.*;
import org.rococo.tests.jupiter.annotation.ApiLogin;
import org.rococo.tests.jupiter.annotation.meta.WebTest;
import org.rococo.tests.model.ArtistDTO;
import org.rococo.tests.model.MuseumDTO;
import org.rococo.tests.model.PaintingDTO;
import org.rococo.tests.page.PaintingPage;
import org.rococo.tests.page.PaintingsPage;
import org.rococo.tests.page.form.PaintingForm;
import org.rococo.tests.util.DataGenerator;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

import static com.codeborne.selenide.Selenide.open;

@WebTest
@Feature("WEB")
@Story("[Web] Paintings tests")
@DisplayName("[WEB] Paintings tests")
@ParametersAreNonnullByDefault
class PaintingsWebTests {

    private static final Faker FAKE = new Faker();
    private static final String
            IMG_1 = "img/original/img.jpg",
            IMG_1_EXPECTED = "img/expected/painting/img.jpg",
            IMG_2 = "img/original/img.png",
            IMG_2_EXPECTED = "img/expected/painting/img.png",
            ILLEGAL_FORMAT_IMG = "img/original/img.gif";

    private final PaintingsPage paintingsPage = new PaintingsPage();
    private final PaintingPage paintingPage = new PaintingPage();
    private final PaintingForm paintingForm = new PaintingForm();

    @ApiLogin(@User)
    @Artist
    @Museum
    @Test
    @DisplayName("Should add new painting with correct data")
    void shouldAddPaintingWithCorrectDataTest(ArtistDTO artist, MuseumDTO museum) {
        // Data
        var painting = DataGenerator.generatePainting()
                .setArtist(artist)
                .setMuseum(museum)
                .setPathToPhoto(IMG_1);

        // Steps
        open(PaintingsPage.URL, PaintingsPage.class)
                .addNewPainting(painting);

        // Assertions
        paintingsPage.shouldExistPainting(painting.getTitle());
    }

    @Test
    @DisplayName("Check add new painting button not exists without authorization")
    void shouldNotAvailableAddPaintingWithoutAuthorizationTest() {
        // Steps
        open(PaintingsPage.URL, PaintingsPage.class);

        // Assertions
        paintingsPage.shouldNotExistsAddNewPaintingButton();
    }

    @ApiLogin(@User)
    @Artist
    @Museum
    @ParameterizedTest(name = "Case: [{0}]")
    @MethodSource("org.rococo.tests.tests.web.data.DataProvider#validPaintingData")
    @DisplayName("Check painting creates if data length is valid")
    void shouldCreatePaintingWithValidLengthDataTest(String caseName,
                                                     String paintingTitle,
                                                     String paintingDescription,
                                                     ArtistDTO artist,
                                                     MuseumDTO museumDTO
    ) {
        // Data
        var painting = DataGenerator.generatePainting()
                .setTitle(paintingTitle)
                .setDescription(paintingDescription)
                .setArtist(artist)
                .setMuseum(museumDTO);

        // Steps
        open(PaintingsPage.URL, PaintingsPage.class)
                .addNewPainting(painting);

        // Assertions
        paintingsPage.shouldExistPainting(paintingTitle);
    }

    @ApiLogin(@User)
    @Artist
    @Museum
    @ParameterizedTest(name = "Case: [{0}]")
    @MethodSource("org.rococo.tests.tests.web.data.DataProvider#invalidPaintingData")
    @DisplayName("Check errors visible on add new painting form if fields have greater than max characters length")
    void shouldDisplayErrorsOnAddAristFormIfPaintingFieldsHaveGreaterThanMaxLengthTest(String caseName,
                                                                                       String paintingTitle,
                                                                                       String paintingDescription,
                                                                                       String[] errors,
                                                                                       ArtistDTO artist,
                                                                                       MuseumDTO museum
    ) {
        // Data
        var painting = DataGenerator.generatePainting()
                .setTitle(paintingTitle)
                .setDescription(paintingDescription)
                .setArtist(artist)
                .setMuseum(museum);

        // Steps
        open(PaintingsPage.URL, PaintingsPage.class)
                .addNewPaintingWithError(painting);

        // Assertions
        paintingForm.shouldHaveErrors(errors);
    }

    @ApiLogin(@User)
    @Artist
    @Museum
    @Test
    @DisplayName("Check errors visible on add new painting form if image has invalid format")
    void shouldDisplayErrorOnAddPaintingFormWhenUploadingInvalidImageFormat(ArtistDTO artist, MuseumDTO museum) {
        // Data
        var painting = DataGenerator.generatePainting()
                .setArtist(artist)
                .setMuseum(museum)
                .setPathToPhoto(ILLEGAL_FORMAT_IMG);

        // Steps
        open(PaintingsPage.URL, PaintingsPage.class)
                .addNewPaintingWithError(painting);

        // Assertions
        paintingForm.shouldHaveErrors("Допустимые форматы изображений: '.jpg', '.jpeg', '.png'");
    }

    @ApiLogin(@User)
    @Artist
    @Museum
    @Painting
    @Test
    @DisplayName("Should update painting with correct data")
    void shouldUpdatePaintingWithCorrectDataTest(PaintingDTO painting, ArtistDTO artist, MuseumDTO museum) {
        // Data
        var newPainting = DataGenerator.generatePainting()
                .setArtist(artist)
                .setMuseum(museum)
                .setPathToPhoto(IMG_2);

        System.out.println("NEW PAINTING: " + newPainting);

        // Steps
        open(PaintingsPage.URL, PaintingsPage.class)
                .updatePainting(painting.getTitle(), newPainting)
                .notificationClose();

        // Assertions
        paintingPage.shouldHaveTitle(newPainting.getTitle())
                .shouldHaveDescription(newPainting.getDescription())
                .shouldHaveScreenshot(IMG_2_EXPECTED);
    }

    @Painting
    @Test
    @DisplayName("Check update painting button not exists without authorization")
    void shouldNotAvailableUpdatePaintingWithoutAuthorizationTest(PaintingDTO painting) {
        // Steps
        open(PaintingsPage.URL, PaintingsPage.class)
                .openPainting(painting.getTitle());

        // Assertions
        paintingPage.shouldNotContainsEditButton();
    }

    @ApiLogin(@User)
    @Artist
    @Museum
    @Painting
    @ParameterizedTest(name = "Case: [{0}]")
    @MethodSource("org.rococo.tests.tests.web.data.DataProvider#validPaintingData")
    @DisplayName("Check painting updates if data length is valid")
    void shouldUpdatePaintingWithValidLengthDataTest(String caseName,
                                                     String paintingTitle,
                                                     String paintingDescription,
                                                     PaintingDTO painting,
                                                     ArtistDTO artist,
                                                     MuseumDTO museum
    ) {
        // Data
        var newPainting = DataGenerator.generatePainting()
                .setArtist(artist)
                .setMuseum(museum)
                .setTitle(paintingTitle)
                .setDescription(paintingDescription);

        // Steps
        open(PaintingsPage.URL, PaintingsPage.class)
                .updatePainting(painting.getTitle(), newPainting);

        // Assertions
        paintingPage.shouldHaveTitle(newPainting.getTitle())
                .shouldHaveDescription(newPainting.getDescription());
    }

    @ApiLogin(@User)
    @Artist
    @Museum
    @Painting
    @ParameterizedTest(name = "Case: [{0}]")
    @MethodSource("org.rococo.tests.tests.web.data.DataProvider#invalidPaintingData")
    @DisplayName("Check errors visible on update painting form if fields have greater than max characters length")
    void shouldVisibleErrorsOnPaintingUpdateFormIfPaintingFieldsHaveGreaterThanMaxLengthTest(
            String caseName,
            String paintingTitle,
            String paintingDescription,
            String[] errors,
            PaintingDTO painting,
            ArtistDTO artist,
            MuseumDTO museum
    ) {
        // Data
        var newPainting = DataGenerator.generatePainting()
                .setTitle(paintingTitle)
                .setDescription(paintingDescription)
                .setArtist(artist)
                .setMuseum(museum);

        // Steps
        open(PaintingsPage.URL, PaintingsPage.class)
                .updatePaintingWithError(painting.getTitle(), newPainting);

        // Assertions
        paintingForm.shouldHaveErrors(errors);
    }


    @ApiLogin(@User)
    @Painting
    @Test
    @DisplayName("Check error displayed on update painting form when uploading image with invalid format")
    void shouldDisplayErrorOnUpdatePaintingFormWhenUploadingInvalidImageFormat(PaintingDTO painting) {
        // Data
        painting.setPathToPhoto(ILLEGAL_FORMAT_IMG);

        // Steps
        open(PaintingsPage.URL, PaintingsPage.class)
                .updatePaintingWithError(painting.getTitle(), painting);

        // Assertions
        paintingForm.shouldPaintingPhotoErrorHaveText("Допустимые форматы изображений: '.jpg', '.jpeg', '.png'");
    }

    @ApiLogin(@User)
    @Paintings({
            @Painting(title = "Vincent Willem van Gogh"),
            @Painting(title = "Vanessa Cooper"),
            @Painting(title = "Ryan Sullivan")
    })
    @Test
    @DisplayName("Check paintings found by filtered search")
    void shouldFindPaintingsWithFilterTest(List<PaintingDTO> paintings) {
        // Steps && Assertion
        open(PaintingsPage.URL, PaintingsPage.class)
                .shouldContainsPaintingsInQuerySearch("vAn", paintings.stream()
                        .map(PaintingDTO::getTitle)
                        .toList());
    }

    @Test
    @DisplayName("Check displayed empty filtered list container if painting not founded by query")
    void shouldDisplayPaintingAfterFilteringByNameTest() {
        // Steps && Assertion
        open(PaintingsPage.URL, PaintingsPage.class)
                .shouldHaveEmptySearchResultByQuery(FAKE.lorem().paragraph());
    }

    @Disabled
    @Test
    @DisplayName("Check displayed default empty list if painting not exists")
    void shouldDisplayEmptyListWhenPaintingsNotExistsTest() {
        // Steps && Assertion
        open(PaintingsPage.URL, PaintingsPage.class)
                .shouldVisibleDefaultEmptyPaintingsList();
    }

}
