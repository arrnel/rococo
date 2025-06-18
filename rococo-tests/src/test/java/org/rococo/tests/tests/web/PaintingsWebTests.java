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
import org.rococo.tests.model.MuseumDTO;
import org.rococo.tests.model.PaintingDTO;
import org.rococo.tests.page.PaintingPage;
import org.rococo.tests.page.PaintingsPage;
import org.rococo.tests.page.form.PaintingForm;
import org.rococo.tests.util.DataGenerator;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

@WebTest
@Feature("WEB")
@Story("[WEB] Paintings tests")
@DisplayName("[WEB] Paintings tests")
@ParametersAreNonnullByDefault
class PaintingsWebTests {

    private static final Faker FAKE = new Faker();
    private static final String
            IMG_1 = "img.jpg",
            IMG_1_EXPECTED = "painting/img.jpg",
            IMG_2 = "img.png",
            IMG_2_EXPECTED = "painting/img.png",
            ILLEGAL_FORMAT_IMG = "img.gif";

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
        paintingsPage.open()
                .addNewPainting(painting)

                // Assertions
                .shouldFoundPainting(painting.getTitle());
    }

    @Test
    @DisplayName("Check add new painting button not exists without authorization")
    void shouldNotAvailableAddPaintingWithoutAuthorizationTest() {
        // Steps
        paintingsPage.open()

                // Assertions
                .shouldNotExistsAddNewPaintingButton();
    }

    @ApiLogin(@User)
    @Artist
    @Museum
    @ParameterizedTest(name = "Case: [{0}]")
    @MethodSource("org.rococo.tests.tests.web.data.DataProvider#validPaintingData")
    @DisplayName("Check painting creates if data length is valid")
    void shouldCreatePaintingWithValidLengthDataTest(String caseName,
                                                     int paintingTitleLength,
                                                     int paintingDescriptionLength,
                                                     ArtistDTO artist,
                                                     MuseumDTO museumDTO
    ) {
        // Data
        var paintingTitle = FAKE.lorem().characters(paintingTitleLength);
        var paintingDescription = FAKE.lorem().characters(paintingDescriptionLength);
        var painting = DataGenerator.generatePainting()
                .setTitle(paintingTitle)
                .setDescription(paintingDescription)
                .setArtist(artist)
                .setMuseum(museumDTO);

        // Steps
        paintingsPage.open()
                .addNewPainting(painting)

                // Assertions
                .shouldFoundPainting(paintingTitle);
    }

    @ApiLogin(@User)
    @Artist
    @Museum
    @ParameterizedTest(name = "Case: [{0}]")
    @MethodSource("org.rococo.tests.tests.web.data.DataProvider#invalidPaintingData")
    @DisplayName("Check errors visible on add new painting form if fields have greater than max characters length")
    void shouldDisplayErrorsOnAddAristFormIfPaintingFieldsHaveGreaterThanMaxLengthTest(String caseName,
                                                                                       int paintingTitleLength,
                                                                                       int paintingDescriptionLength,
                                                                                       String[] errors,
                                                                                       ArtistDTO artist,
                                                                                       MuseumDTO museum
    ) {
        // Data
        var paintingTitle = FAKE.lorem().characters(paintingTitleLength);
        var paintingDescription = FAKE.lorem().characters(paintingDescriptionLength);
        var painting = DataGenerator.generatePainting()
                .setTitle(paintingTitle)
                .setDescription(paintingDescription)
                .setArtist(artist)
                .setMuseum(museum);

        // Steps
        paintingsPage.open()
                .addNewPaintingWithError(painting)

                // Assertions
                .shouldHaveErrors(errors);
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
        paintingsPage.open()
                .addNewPaintingWithError(painting)

                // Assertions
                .shouldHaveErrors("Допустимые форматы изображений: '.jpg', '.jpeg', '.png'");
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

        // Steps
        paintingPage.open(painting.getId())
                .updatePainting(newPainting)
                .notificationClose()

                // Assertions
                .shouldHaveTitle(newPainting.getTitle())
                .shouldHaveDescription(newPainting.getDescription());
    }

    @Painting
    @Test
    @DisplayName("Check update painting button not exists without authorization")
    void shouldNotAvailableUpdatePaintingWithoutAuthorizationTest(PaintingDTO painting) {
        // Steps
        paintingPage.open(painting.getId())

                // Assertions
                .shouldNotContainsEditButton();
    }

    @ApiLogin(@User)
    @Artist
    @Museum
    @Painting
    @ParameterizedTest(name = "Case: [{0}]")
    @MethodSource("org.rococo.tests.tests.web.data.DataProvider#validPaintingData")
    @DisplayName("Check painting updates if data length is valid")
    void shouldUpdatePaintingWithValidLengthDataTest(String caseName,
                                                     int paintingTitleLength,
                                                     int paintingDescriptionLength,
                                                     PaintingDTO painting,
                                                     ArtistDTO artist,
                                                     MuseumDTO museum
    ) {
        // Data
        var paintingTitle = FAKE.lorem().characters(paintingTitleLength);
        var paintingDescription = FAKE.lorem().characters(paintingDescriptionLength);
        var newPainting = DataGenerator.generatePainting()
                .setArtist(artist)
                .setMuseum(museum)
                .setTitle(paintingTitle)
                .setDescription(paintingDescription);

        // Steps
        paintingPage.open(painting.getId())
                .updatePainting(newPainting)

                // Assertions
                .shouldHaveTitle(newPainting.getTitle())
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
            int paintingTitleLength,
            int paintingDescriptionLength,
            String[] errors,
            PaintingDTO painting,
            ArtistDTO artist,
            MuseumDTO museum
    ) {
        // Data
        var paintingTitle = FAKE.lorem().characters(paintingTitleLength);
        var paintingDescription = FAKE.lorem().characters(paintingDescriptionLength);
        var newPainting = DataGenerator.generatePainting()
                .setTitle(paintingTitle)
                .setDescription(paintingDescription)
                .setArtist(artist)
                .setMuseum(museum);

        // Steps
        paintingPage.open(painting.getId())
                .updatePaintingWithError(newPainting)

                // Assertions
                .shouldHaveErrors(errors);
    }


    @ApiLogin(@User)
    @Painting
    @Test
    @DisplayName("Check error displayed on update painting form when uploading image with invalid format")
    void shouldDisplayErrorOnUpdatePaintingFormWhenUploadingInvalidImageFormat(PaintingDTO painting) {
        // Data
        painting.setPathToPhoto(ILLEGAL_FORMAT_IMG);

        // Steps
        paintingPage.open(painting.getId())
                .updatePaintingWithError(painting)

                // Assertions
                .shouldPaintingPhotoErrorHaveText("Допустимые форматы изображений: '.jpg', '.jpeg', '.png'");
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
        // Steps & Assertions
        paintingsPage.open()
                .shouldFoundPaintings("vAn", paintings.stream()
                        .map(PaintingDTO::getTitle)
                        .toList());
    }

    @Test
    @DisplayName("Check displayed empty filtered list container if painting not founded by query")
    void shouldDisplayPaintingAfterFilteringByNameTest() {
        // Steps & Assertions
        paintingsPage.open()
                .shouldHaveEmptySearchResult(FAKE.lorem().paragraph());
    }

    @DisabledByIssue(issueId = "32")
    @Test
    @DisplayName("Check displayed default empty list if painting not exists")
    void shouldDisplayEmptyListWhenPaintingsNotExistsTest() {
        // Steps & Assertions
        paintingsPage.open()
                .shouldVisibleDefaultEmptyPaintingsList();
    }

}
