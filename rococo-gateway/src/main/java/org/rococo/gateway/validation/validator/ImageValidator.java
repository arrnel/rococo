package org.rococo.gateway.validation.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import org.rococo.gateway.config.AppProperty;
import org.rococo.gateway.validation.Image;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Arrays;
import java.util.Locale;
import java.util.stream.Stream;

@Component
@RequiredArgsConstructor
@ParametersAreNonnullByDefault
public class ImageValidator implements ConstraintValidator<Image, String> {

    private static final int ONE_MB = 1024 * 1024;
    private static final int MIN_MAX_SIZE = 1024 * 1024;

    private static final String INVALID_IMAGE_PATTERN_MESSAGE = "errors.validation.image.pattern";
    private static final String INVALID_IMAGE_SIZE_MESSAGE = "errors.validation.image.size";
    private static final String INVALID_IMAGE_FORMAT_MESSAGE = "errors.validation.image.format";
    private static final String PROPERTY_NODE = Image.class.getSimpleName();

    private final MessageSource messageSource;
    private final Locale locale = LocaleContextHolder.getLocale();

    Image anno;

    @Override
    public void initialize(Image constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
        anno = constraintAnnotation;
    }

    @Override
    public boolean isValid(final String base64img, final ConstraintValidatorContext context) {

        int maxSize = anno.maxSize() * ONE_MB;
        if (maxSize < MIN_MAX_SIZE)
            throw new IllegalArgumentException("Invalid image max size: [%d]. Min size = [%d]".formatted(maxSize, MIN_MAX_SIZE));

        if (!validateImagePattern(base64img, context)) return false;

        final var imgText = base64img
                .substring(base64img.indexOf(",") + 1);
        final var format = base64img
                .substring(0, base64img.indexOf(";"))
                .substring(base64img.indexOf("/") + 1);

        return Stream.of(
                        validateImageSize(
                                anno.maxSize(),
                                imgText,
                                context),
                        validateFormat(
                                anno.formats(),
                                format,
                                context))
                .allMatch(status -> status);

    }

    private boolean validateImagePattern(final String base64Img,
                                         final ConstraintValidatorContext context
    ) {

        if (!base64Img.matches(AppProperty.IMAGE_PATTERN)) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(
                            messageSource.getMessage(
                                    INVALID_IMAGE_PATTERN_MESSAGE,
                                    new Object[0],
                                    "Invalid image pattern(default)",
                                    locale)
                    )
                    .addPropertyNode(PROPERTY_NODE)
                    .addConstraintViolation();
            return false;
        }

        return true;

    }

    private Boolean validateFormat(final String[] expectedFormats,
                                   final String format,
                                   final ConstraintValidatorContext context
    ) {

        if (expectedFormats.length == 0)
            throw new IllegalArgumentException("Available image expected formats are empty");

        for (String expectedFormat : expectedFormats) {
            if (expectedFormat.equalsIgnoreCase(format))
                return true;
        }

        final var imageFormatText = Arrays.toString(anno.formats());
        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate(
                        messageSource.getMessage(
                                INVALID_IMAGE_FORMAT_MESSAGE,
                                new String[]{imageFormatText},
                                "Available image formats: " + imageFormatText,
                                locale))
                .addPropertyNode(PROPERTY_NODE)
                .addConstraintViolation();

        return false;

    }

    private boolean validateImageSize(final int maxSize,
                                      final String imgText,
                                      final ConstraintValidatorContext context
    ) {
        final int actualSize = (imgText.length() * 3) / 4;
        if (actualSize > maxSize * ONE_MB) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(
                            messageSource.getMessage(
                                    INVALID_IMAGE_SIZE_MESSAGE,
                                    new Integer[]{MIN_MAX_SIZE},
                                    "Invalid image size",
                                    locale))
                    .addPropertyNode(PROPERTY_NODE)
                    .addConstraintViolation();
            return false;
        }
        return true;
    }

}
