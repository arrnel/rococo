package org.rococo.tests.util;

import net.datafaker.Faker;
import net.datafaker.providers.base.Text;
import org.rococo.tests.enums.CountryCode;
import org.rococo.tests.model.*;

import static net.datafaker.providers.base.Text.*;

public class DataGenerator {

    private static final Faker FAKE = new Faker();

    public static final String[] availableImagesFormats = new String[]{"jpg", "jpeg", "png"};

    public static String randomImagePath() {
        return randomImagePath(FAKE.random().nextBoolean());
    }

    public static String randomImagePath(boolean isVertical) {
        return "img/original/img%s.%s".formatted(
                isVertical
                        ? "_vertical"
                        : "",
                availableImagesFormats[FAKE.random().nextInt(availableImagesFormats.length)]);
    }

    public static ArtistDTO generateArtist() {
        return ArtistDTO.builder()
                .name(FAKE.name().fullName())
                .biography(FAKE.lorem().paragraph())
                .photo(ImageUtil.generateImage())
                .pathToPhoto(randomImagePath())
                .build();
    }

    public static MuseumDTO generateMuseum() {
        var countryCode = CountryCode.random();
        return MuseumDTO.builder()
                .title(FAKE.book().author() + ". " + FAKE.book().title())
                .description(FAKE.lorem().paragraph())
                .location(new LocationDTO(
                        FAKE.address().cityName(),
                        new CountryDTO(null, countryCode.getValue(), countryCode)))
                .photo(ImageUtil.generateImage())
                .pathToPhoto(randomImagePath())
                .build();
    }

    public static PaintingDTO generatePainting() {
        return PaintingDTO.builder()
                .title(FAKE.lorem().sentence())
                .description(FAKE.lorem().paragraph())
                .artist(generateArtist())
                .museum(generateMuseum())
                .photo(ImageUtil.generateImage())
                .pathToPhoto(randomImagePath())
                .build();
    }

    public static UserDTO generateUser() {
        return UserDTO.builder()
                .username(FAKE.internet().username() + "." + FAKE.number().digits(3))
                .firstName(FAKE.name().firstName())
                .lastName(FAKE.name().lastName())
                .photo(ImageUtil.generateImage())
                .pathToPhoto(randomImagePath())
                .build()
                .password("Rococo1!");
    }

    public static String generateUsername() {
        return FAKE.internet().username();
    }

    public static String generatePassword() {
        return generatePassword(5, 20, false);
    }

    public static String generatePassword(boolean excludeSpecial) {
        return generatePassword(5, 20, excludeSpecial);
    }

    public static String generatePassword(int fixedLength) {
        return generatePassword(fixedLength, fixedLength, false);
    }

    public static String generatePassword(int fixedLength, boolean excludeSpecial) {
        return generatePassword(fixedLength, fixedLength, excludeSpecial);
    }


    public static String generatePassword(int min, int max) {
        return generatePassword(min, max, false);
    }

    public static String generatePassword(int min, int max, boolean excludeSpecial) {
        var number = FAKE.number().numberBetween(min, max);
        var textBuilder = Text.TextSymbolsBuilder.builder()
                .len(number)
                .with(EN_LOWERCASE, 1)
                .with(EN_UPPERCASE, 1);

        if (!excludeSpecial)
            textBuilder.with(DEFAULT_SPECIAL, 1);

        return FAKE.text().text(textBuilder.build());
    }

}
