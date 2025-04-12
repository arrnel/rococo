package org.rococo.tests.tests.web;

import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import net.datafaker.Faker;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.rococo.tests.jupiter.annotation.ApiLogin;
import org.rococo.tests.jupiter.annotation.Museum;
import org.rococo.tests.jupiter.annotation.Museums;
import org.rococo.tests.jupiter.annotation.User;
import org.rococo.tests.jupiter.annotation.meta.WebTest;
import org.rococo.tests.model.MuseumDTO;
import org.rococo.tests.page.MuseumPage;
import org.rococo.tests.page.MuseumsPage;
import org.rococo.tests.page.form.MuseumForm;
import org.rococo.tests.util.DataGenerator;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

import static com.codeborne.selenide.Selenide.open;

@WebTest
@Feature("WEB")
@Story("[WEB] Museums tests")
@DisplayName("[WEB] Museums tests")
@ParametersAreNonnullByDefault
class MuseumsWebTest {

    private static final Faker FAKE = new Faker();
    private static final String
            IMG_1 = "img/original/img.jpg",
            IMG_1_EXPECTED = "img/expected/museum/img.jpg",
            IMG_2 = "img/original/img.png",
            IMG_2_EXPECTED = "img/expected/museum/img.png",
            ILLEGAL_FORMAT_IMG = "img/original/img.gif";

    private final MuseumsPage museumsPage = new MuseumsPage();
    private final MuseumPage museumPage = new MuseumPage();
    private final MuseumForm museumForm = new MuseumForm();

    @ApiLogin(@User)
    @Test
    @DisplayName("Should add new museum with correct data")
    void shouldAddMuseumWithCorrectDataTest() {
        // Data
        var museum = DataGenerator.generateMuseum()
                .setPathToPhoto(IMG_1);

        // Steps
        open(MuseumsPage.URL, MuseumsPage.class)
                .addNewMuseum(museum);

        // Assertions
        museumsPage.shouldExistMuseum(museum.getTitle());
    }

    @Test
    @DisplayName("Check add new museum button not exists without authorization")
    void shouldNotAvailableAddMuseumWithoutAuthorizationTest() {
        // Steps
        open(MuseumsPage.URL, MuseumsPage.class)
                .shouldVisiblePage();

        // Assertions
        museumsPage.shouldNotExistsAddNewMuseumButton();
    }

    @ApiLogin(@User)
    @ParameterizedTest(name = "Case: [{0}]")
    @MethodSource("org.rococo.tests.tests.web.data.DataProvider#validMuseumData")
    @DisplayName("Check museum creates if data length is valid")
    void shouldCreateMuseumWithValidLengthDataTest(String caseName, String museumTitle, String museumDescription) {
        // Data
        var museum = DataGenerator.generateMuseum()
                .setTitle(museumTitle)
                .setDescription(museumDescription);

        // Steps
        open(MuseumsPage.URL, MuseumsPage.class)
                .addNewMuseum(museum);

        // Assertions
        museumsPage.shouldExistMuseum(museumTitle);
    }

    @ApiLogin(@User)
    @ParameterizedTest(name = "Case: [{0}]")
    @MethodSource("org.rococo.tests.tests.web.data.DataProvider#invalidMuseumData")
    @DisplayName("Check errors visible on add new museum form if fields have greater than max characters length")
    void shouldDisplayErrorsOnAddAristFormIfMuseumFieldsHaveGreaterThanMaxLengthTest(String caseName,
                                                                                     String museumTitle,
                                                                                     String museumDescription,
                                                                                     String city,
                                                                                     String[] errors
    ) {
        // Data
        var museum = DataGenerator.generateMuseum()
                .setTitle(museumTitle)
                .setDescription(museumDescription);
        museum.getLocation()
                .setCity(city);

        // Steps
        open(MuseumsPage.URL, MuseumsPage.class)
                .addNewMuseumWithError(museum);

        // Assertions
        museumForm.shouldHaveErrors(errors);
    }

    @ApiLogin(@User)
    @Test
    @DisplayName("Check error displayed on add museum form when uploading image with invalid format")
    void shouldDisplayErrorOnAddMuseumFormWhenUploadingInvalidImageFormat() {
        // Data
        var museum = DataGenerator.generateMuseum()
                .setPathToPhoto(ILLEGAL_FORMAT_IMG);

        // Steps
        open(MuseumsPage.URL, MuseumsPage.class)
                .addNewMuseumWithError(museum);

        // Assertions
        museumForm.shouldHaveErrors("Допустимые форматы изображений: '.jpg', '.jpeg', '.png'");
    }

    @ApiLogin(@User)
    @Museum
    @Test
    @DisplayName("Should update museum with correct data")
    void shouldUpdateMuseumWithCorrectDataTest(MuseumDTO museum) {
        // Data
        var newMuseum = DataGenerator.generateMuseum()
                .setPathToPhoto(IMG_2);

        // Steps
        open(MuseumsPage.URL, MuseumsPage.class)
                .updateMuseum(museum.getTitle(), newMuseum)
                .notificationClose();

        // Assertions
        museumPage.shouldHaveTitle(newMuseum.getTitle())
                .shouldHaveDescription(newMuseum.getDescription())
                .shouldHaveScreenshot(IMG_2_EXPECTED);
    }

    @Museum
    @Test
    @DisplayName("Check update museum button not exists without authorization")
    void shouldNotAvailableUpdateMuseumWithoutAuthorizationTest(MuseumDTO museum) {
        // Steps
        open(MuseumsPage.URL, MuseumsPage.class)
                .openMuseum(museum.getTitle());

        // Assertions
        museumPage.shouldNotExistsUpdateMuseumButton();
    }

    @ApiLogin(@User)
    @Museum
    @ParameterizedTest(name = "Case: [{0}]")
    @MethodSource("org.rococo.tests.tests.web.data.DataProvider#validMuseumData")
    @DisplayName("Check museum updates if data length is valid")
    void shouldUpdateMuseumWithValidLengthDataTest(String caseName, String museumTitle, String museumDescription, MuseumDTO museum) {
        // Data
        var newMuseum = DataGenerator.generateMuseum()
                .setTitle(museumTitle)
                .setDescription(museumDescription);

        // Steps
        open(MuseumsPage.URL, MuseumsPage.class)
                .updateMuseum(museum.getTitle(), newMuseum);

        // Assertions
        museumPage.shouldHaveTitle(newMuseum.getTitle())
                .shouldHaveDescription(newMuseum.getDescription());
    }

    @ApiLogin(@User)
    @Museum
    @ParameterizedTest(name = "Case: [{0}]")
    @MethodSource("org.rococo.tests.tests.web.data.DataProvider#invalidMuseumData")
    @DisplayName("Check errors visible on update museum form if fields have greater than max characters length")
    void shouldVisibleErrorsOnMuseumUpdateFormIfMuseumFieldsHaveGreaterThanMaxLengthTest(
            String caseName,
            String museumTitle,
            String museumDescription,
            String city,
            String[] errors,
            MuseumDTO museum
    ) {
        // Data
        var newMuseum = DataGenerator.generateMuseum()
                .setTitle(museumTitle)
                .setDescription(museumDescription);
        newMuseum.getLocation().setCity(city);

        // Steps
        open(MuseumsPage.URL, MuseumsPage.class)
                .updateMuseumWithError(museum.getTitle(), newMuseum);

        // Assertions
        museumForm.shouldHaveErrors(errors);
    }


    @ApiLogin(@User)
    @Museum
    @Test
    @DisplayName("Check error displayed on update museum form when uploading image with invalid format")
    void shouldDisplayErrorOnUpdateMuseumFormWhenUploadingInvalidImageFormat() {
        // Data
        var museum = DataGenerator.generateMuseum()
                .setPathToPhoto(ILLEGAL_FORMAT_IMG);

        // Steps
        open(MuseumsPage.URL, MuseumsPage.class)
                .addNewMuseumWithError(museum);

        // Assertions
        museumForm.shouldHaveErrors("Допустимые форматы изображений: '.jpg', '.jpeg', '.png'");
    }

    @ApiLogin(@User)
    @Museums({
            @Museum(title = "Vincent Willem van Gogh"),
            @Museum(title = "Vanessa Cooper"),
            @Museum(title = "Ryan Sullivan")
    })
    @Test
    @DisplayName("Check museums found by filtered search")
    void shouldFindMuseumsWithFilterTest(List<MuseumDTO> museums) {
        // Steps && Assertion
        open(MuseumsPage.URL, MuseumsPage.class)
                .shouldContainsMuseumsInQuerySearch("vAn", museums.stream()
                        .map(MuseumDTO::getTitle)
                        .toList());
    }

    @Test
    @DisplayName("Check displayed empty filtered list container if museum not founded by query")
    void shouldDisplayMuseumAfterFilteringByNameTest() {
        // Steps && Assertion
        open(MuseumsPage.URL, MuseumsPage.class)
                .shouldHaveEmptySearchResultByQuery(FAKE.lorem().paragraph());
    }

    @Disabled
    @Test
    @DisplayName("Check displayed default empty list if museum not exists")
    void shouldDisplayEmptyListWhenMuseumsNotExistsTest() {
        // Steps && Assertion
        open(MuseumsPage.URL, MuseumsPage.class)
                .shouldVisibleDefaultEmptyMuseumsList();
    }

}
