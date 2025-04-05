package org.rococo.gateway.validation.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import org.rococo.gateway.validation.Columns;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Order;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

@Component
@RequiredArgsConstructor
public class ColumnsValidator implements ConstraintValidator<Columns, Pageable> {

    private static final String ERROR_PATH = "errors.validation.paintings.find_all.columns.contains_invalid_fields";
    private final MessageSource messageSource;
    private final Locale locale = LocaleContextHolder.getLocale();

    Columns anno;

    @Override
    public void initialize(Columns constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
        this.anno = constraintAnnotation;
    }

    @Override
    public boolean isValid(Pageable pageable, ConstraintValidatorContext context) {

        if (anno.value().length == 0) throw new IllegalArgumentException("Columns must have at least one value");

        final List<String> expectedColumns = List.of(anno.value());
        final var actualColumns = pageable.getSort().stream()
                .map(Order::getProperty)
                .toArray(String[]::new);

        if (Arrays.stream(actualColumns).allMatch(expectedColumns::contains)) return true;

        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate(
                        messageSource.getMessage(
                                ERROR_PATH,
                                new String[]{expectedColumns.toString()},
                                "Request contains invalid columns. Available columns: " + expectedColumns,
                                locale))
                .addPropertyNode(Columns.class.getSimpleName())
                .addConstraintViolation();

        return false;

    }

}