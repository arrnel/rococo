package org.rococo.tests.tests.web.data;

import net.datafaker.Faker;
import org.junit.jupiter.params.provider.Arguments;
import org.rococo.tests.util.DataGenerator;

import java.util.Arrays;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class DataProvider {

    private static final Faker FAKE = new Faker();
    private static final String[] allowedUsernameSpecials = new String[]{"_", "-", "."};

    private static final String USERNAME_PATTERN_ERROR_MESSAGE = "Username: 6-30 chars, lowercase letters, digits, [\"_\", \"-\", \".\"]. Must start/end with a letter or digit and no consecutive special chars.",
            PASSWORD_PATTERN_ERROR_MESSAGE = "Password: 5-20 chars, at least one uppercase, one lowercase, and one special char required.",
            NAME_LENGTH_CONSTRAINT_MIN = "Имя не может быть короче 3 символов",
            NAME_LENGTH_CONSTRAINT_MAX = "Имя не может быть длиннее 255 символов",
            LASTNAME_LENGTH_CONSTRAINT_MIN = "Фамилия не может быть короче 3 символов",
            LASTNAME_LENGTH_CONSTRAINT_MAX = "Фамилия не может быть длиннее 255 символов",
            TITLE_LENGTH_CONSTRAINT_MIN = "Название не может быть короче 3 символов",
            TITLE_LENGTH_CONSTRAINT_MAX = "Название не может быть длиннее 255 символов",
            BIOGRAPHY_LENGTH_CONSTRAINT_MIN = "Биография не может быть короче 10 символов",
            BIOGRAPHY_LENGTH_CONSTRAINT_MAX = "Биография не может быть длиннее 2000 символов",
            DESCRIPTION_LENGTH_CONSTRAINT_MIN = "Описание не может быть короче 10 символов",
            DESCRIPTION_LENGTH_CONSTRAINT_MAX = "Описание не может быть длиннее 2000 символов",
            CITY_LENGTH_CONSTRAINT_MIN = "Город не может быть короче 3 символов",
            CITY_LENGTH_CONSTRAINT_MAX = "Город не может быть длиннее 255 символов",
            IMAGE_CONSTRAINT_TOO_BIG = "Максимальный размер изображения 15 Mb",
            IMAGE_CONSTRAINT_INVALID_FORMAT = "Допустимые форматы изображений: '.jpg', '.jpeg', '.png'",
            AUTHOR_CONSTRAINT_NOT_EMPTY = "Укажите автора картины",
            COUNTRY_CONSTRAINT_NOT_EMPTY = "Укажите страну нахождения музея";

    private static final Range artistNameRange = new Range(3, 255);
    private static final Range artistBiographyRange = new Range(10, 2000);
    private static final Range museumTitleRange = new Range(3, 255);
    private static final Range museumDescriptionRange = new Range(10, 2000);
    private static final Range museumCityRange = new Range(3, 255);
    private static final Range paintingTitleRange = new Range(3, 255);
    private static final Range paintingDescriptionRange = new Range(10, 2000);
    private static final Range userFirstNameRange = new Range(3, 50);
    private static final Range userLastNameRange = new Range(3, 50);
    private static final Range usernameRange = new Range(6, 30);
    private static final Range passwordRange = new Range(5, 20);

    static Stream<Arguments> validArtistsName() {
        return Stream.of(
                Arguments.of("min", artistNameRange.min()),
                Arguments.of("max", artistNameRange.max())
        );
    }

    static Stream<Arguments> validArtistData() {
        return Stream.of(
                Arguments.of("min",
                        artistNameRange.min(),
                        artistBiographyRange.min()),
                Arguments.of("max",
                        artistNameRange.max(),
                        artistBiographyRange.max())
        );
    }

    static Stream<Arguments> invalidArtistData() {
        return Stream.of(
                Arguments.of("1",
                        1,
                        1,
                        new String[]{NAME_LENGTH_CONSTRAINT_MIN, BIOGRAPHY_LENGTH_CONSTRAINT_MIN}),
                Arguments.of("min - 1",
                        artistNameRange.min() - 1,
                        artistBiographyRange.min() - 1,
                        new String[]{NAME_LENGTH_CONSTRAINT_MIN, BIOGRAPHY_LENGTH_CONSTRAINT_MIN}),
                Arguments.of("max + 1",
                        artistNameRange.max() + 1,
                        artistBiographyRange.max() + 1,
                        new String[]{NAME_LENGTH_CONSTRAINT_MAX, BIOGRAPHY_LENGTH_CONSTRAINT_MAX}),
                Arguments.of("max + 10",
                        artistNameRange.max() + 10,
                        artistBiographyRange.max() + 10,
                        new String[]{NAME_LENGTH_CONSTRAINT_MAX, BIOGRAPHY_LENGTH_CONSTRAINT_MAX})
        );
    }

    static Stream<Arguments> validMuseumData() {
        return Stream.of(
                Arguments.of("min",
                        museumTitleRange.min(),
                        museumDescriptionRange.min()),
                Arguments.of("max",
                        museumTitleRange.max(),
                        museumDescriptionRange.max())
        );
    }

    static Stream<Arguments> invalidMuseumData() {
        return Stream.of(
                Arguments.of("1",
                        1,
                        1,
                        1,
                        new String[]{TITLE_LENGTH_CONSTRAINT_MIN, DESCRIPTION_LENGTH_CONSTRAINT_MIN, CITY_LENGTH_CONSTRAINT_MIN}),
                Arguments.of("min - 1",
                        museumTitleRange.min() - 1,
                        museumDescriptionRange.min() - 1,
                        museumCityRange.min() - 1,
                        new String[]{TITLE_LENGTH_CONSTRAINT_MIN, DESCRIPTION_LENGTH_CONSTRAINT_MIN, CITY_LENGTH_CONSTRAINT_MIN}),
                Arguments.of("max + 1",
                        museumTitleRange.max() + 1,
                        museumDescriptionRange.max() + 1,
                        museumCityRange.max() + 1,
                        new String[]{TITLE_LENGTH_CONSTRAINT_MAX, DESCRIPTION_LENGTH_CONSTRAINT_MAX, CITY_LENGTH_CONSTRAINT_MAX}),
                Arguments.of("max + 10",
                        museumTitleRange.max() + 10,
                        museumDescriptionRange.max() + 10,
                        museumCityRange.max() + 10,
                        new String[]{TITLE_LENGTH_CONSTRAINT_MAX, DESCRIPTION_LENGTH_CONSTRAINT_MAX, CITY_LENGTH_CONSTRAINT_MAX})
        );
    }

    static Stream<Arguments> validPaintingData() {
        return Stream.of(
                Arguments.of("min",
                        paintingTitleRange.min(),
                        paintingDescriptionRange.min()),
                Arguments.of("max",
                        paintingTitleRange.max(),
                        paintingDescriptionRange.max())
        );
    }

    static Stream<Arguments> invalidPaintingData() {
        return Stream.of(
                // Ignore empty case. Artist's name and biography inputs marked as required
                Arguments.of("1",
                        1,
                        1,
                        new String[]{TITLE_LENGTH_CONSTRAINT_MIN, DESCRIPTION_LENGTH_CONSTRAINT_MIN}),
                Arguments.of("min - 1",
                        paintingTitleRange.min() - 1,
                        paintingDescriptionRange.min() - 1,
                        new String[]{TITLE_LENGTH_CONSTRAINT_MIN, DESCRIPTION_LENGTH_CONSTRAINT_MIN}),
                Arguments.of("max + 1",
                        paintingTitleRange.max() + 1,
                        paintingDescriptionRange.max() + 1,
                        new String[]{TITLE_LENGTH_CONSTRAINT_MAX, DESCRIPTION_LENGTH_CONSTRAINT_MAX}),
                Arguments.of("max + 10",
                        paintingTitleRange.max() + 10,
                        paintingDescriptionRange.max() + 10,
                        new String[]{TITLE_LENGTH_CONSTRAINT_MAX, DESCRIPTION_LENGTH_CONSTRAINT_MAX})
        );
    }

    static Stream<Arguments> validProfileData() {
        return Stream.of(
                Arguments.of("min", userFirstNameRange.min(), userLastNameRange.min()),
                Arguments.of("max", userFirstNameRange.max(), userLastNameRange.max())
        );
    }

    static Stream<Arguments> invalidProfileData() {
        return Stream.of(
                Arguments.of("1",
                        1,
                        1,
                        new String[]{
                                NAME_LENGTH_CONSTRAINT_MIN,
                                LASTNAME_LENGTH_CONSTRAINT_MIN,
                        }),
                Arguments.of("min - 1",
                        userFirstNameRange.min() - 1,
                        userLastNameRange.min() - 1,
                        new String[]{
                                NAME_LENGTH_CONSTRAINT_MIN,
                                LASTNAME_LENGTH_CONSTRAINT_MIN
                        }),
                Arguments.of("max + 1",
                        userFirstNameRange.max() + 1,
                        userLastNameRange.max() + 1,
                        new String[]{
                                NAME_LENGTH_CONSTRAINT_MAX,
                                LASTNAME_LENGTH_CONSTRAINT_MAX
                        }),
                Arguments.of("max + 10",
                        userFirstNameRange.max() + 10,
                        userLastNameRange.max() + 10,
                        new String[]{
                                NAME_LENGTH_CONSTRAINT_MAX,
                                LASTNAME_LENGTH_CONSTRAINT_MAX
                        })
        );
    }

    static Stream<Arguments> validRegistrationDataLength() {
        return Stream.of(
                Arguments.of("min",
                        usernameRange.min(),
                        passwordRange.min()),
                Arguments.of("max",
                        usernameRange.max(),
                        passwordRange.max())
        );
    }

    static Stream<Arguments> invalidRegistrationDataLength() {
        return Stream.of(
                Arguments.of("1",
                        1,
                        1,
                        new String[]{USERNAME_PATTERN_ERROR_MESSAGE, PASSWORD_PATTERN_ERROR_MESSAGE}),
                Arguments.of("min - 1",
                        usernameRange.min() - 1,
                        passwordRange.min() - 1,
                        new String[]{USERNAME_PATTERN_ERROR_MESSAGE, PASSWORD_PATTERN_ERROR_MESSAGE}),
                Arguments.of("max + 1",
                        usernameRange.max() + 1,
                        passwordRange.max() + 1,
                        new String[]{USERNAME_PATTERN_ERROR_MESSAGE, PASSWORD_PATTERN_ERROR_MESSAGE}),
                Arguments.of("max + 10",
                        usernameRange.max() + 10,
                        passwordRange.max() + 10,
                        new String[]{USERNAME_PATTERN_ERROR_MESSAGE, PASSWORD_PATTERN_ERROR_MESSAGE}));
    }

    static Stream<Arguments> invalidUsernamePattern() {
        return Stream.of(
                Arguments.of("username starts and ends with valid special symbol",
                        "." + FAKE.internet().username() + "-",
                        USERNAME_PATTERN_ERROR_MESSAGE),
                Arguments.of("username contains multiple specials in a row",
                        insertTextInRandomPlace(FAKE.internet().username(), randomValidSpecialsForUsername(), false),
                        USERNAME_PATTERN_ERROR_MESSAGE),
                Arguments.of("username contains invalid special",
                        insertTextInRandomPlace(FAKE.internet().username(), randomInvalidSpecialForUsername(), false),
                        USERNAME_PATTERN_ERROR_MESSAGE)
        );
    }

    static Stream<Arguments> invalidPasswordPattern() {
        return Stream.of(
                Arguments.of("password not contains uppercase symbols",
                        DataGenerator.generatePassword().toLowerCase(),
                        PASSWORD_PATTERN_ERROR_MESSAGE),
                Arguments.of("password not contains lowercase symbols",
                        DataGenerator.generatePassword().toUpperCase(),
                        PASSWORD_PATTERN_ERROR_MESSAGE),
                Arguments.of("password not contains special symbols",
                        DataGenerator.generatePassword(true),
                        PASSWORD_PATTERN_ERROR_MESSAGE)
        );
    }

    private static String randomValidSpecialsForUsername() {

        final var length = new Random().nextInt(3) + 2;
        return IntStream.range(0, length)
                .mapToObj(i -> allowedUsernameSpecials[new Random().nextInt(allowedUsernameSpecials.length)])
                .collect(Collectors.joining());
    }

    private static String randomInvalidSpecialForUsername() {
        String special = null;
        while (special == null || Arrays.asList(allowedUsernameSpecials).contains(special)) {
            special = FAKE.examplify("^\\W$");
        }
        return special;
    }

    private static String insertTextInRandomPlace(String text, String subText, boolean includeEdges) {
        var delta = includeEdges ? 0 : 1;
        var randomIndex = new Random().nextInt(text.length() + 1 - delta) + 1;
        return text.substring(0, randomIndex) + subText + text.substring(randomIndex);
    }

    private record Range(
            int min,
            int max
    ) {

    }

}
