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
        return "img%s.%s".formatted(
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
                .title(FAKE.book().author() + ". " + FAKE.book().title() + "_" + FAKE.number().digits(10))
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
                .username(generateUsername())
                .firstName(FAKE.name().firstName())
                .lastName(FAKE.name().lastName())
                .photo(ImageUtil.generateImage())
                .pathToPhoto(randomImagePath())
                .build()
                .password("Rococo1!");
    }

    public static String generateUsername() {
        return "%s.%s.%s".formatted(FAKE.lorem().word(), FAKE.lorem().word(), FAKE.number().digits(3));
    }

    public static String generateUsername(int fixedLength) {
        return generateUsername(fixedLength, fixedLength);
    }

    public static String generateUsername(int min, int max) {
        var firstChar = FAKE.lorem().characters(1, false, false);
        var lastChar = FAKE.lorem().characters(1, false, true);
        var number = FAKE.number().numberBetween(min, max);
        number = Math.max(0, number - 2);
        var textBuilder = Text.TextSymbolsBuilder.builder()
                .len(number)
                .with(DIGITS)
                .build();

        return switch (min) {
            case 0 -> "";
            case 1 -> firstChar;
            case 2 -> firstChar + lastChar;
            default -> firstChar + FAKE.text().text(textBuilder) + lastChar;
        };
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
        if (number < 0)
            throw new IllegalArgumentException("Invalid [min; max] range");
        if (number < 3)
            return FAKE.lorem().characters(number);

        var textBuilder = Text.TextSymbolsBuilder.builder()
                .len(number)
                .with(EN_LOWERCASE, 1)
                .with(EN_UPPERCASE, 1);

        if (!excludeSpecial)
            textBuilder.with(DEFAULT_SPECIAL, 1);

        return FAKE.text().text(textBuilder.build());
    }

}
