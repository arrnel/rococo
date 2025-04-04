package org.rococo.paintings.specs.value;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Component
public class PartialTextSpec {

    public Optional<Predicate> specify(
            @Nonnull String fieldName,
            @Nullable String text,
            @Nonnull Root<?> root,
            @Nonnull CriteriaBuilder builder
    ) {
        return (text != null && !text.trim().isEmpty())
                ? Optional.of(
                builder.like(
                        builder.lower(root.get(fieldName)),
                        "%" + text.toLowerCase() + "%"))
                : Optional.empty();

    }

    public Optional<Predicate> specify(
            @Nonnull String[] fieldNames,
            @Nullable String text,
            @Nonnull Root<?> root,
            @Nonnull CriteriaBuilder builder
    ) {
        List<Predicate> fieldPredicates = new ArrayList<>();
        if (text != null && !text.trim().isEmpty() && fieldNames.length > 0) {
            Arrays.stream(fieldNames)
                    .forEach(fieldName ->
                            fieldPredicates.add(
                                    builder.like(
                                            builder.lower(root.get(fieldName)),
                                            "%" + text.toLowerCase() + "%")));
        }
        return fieldPredicates.isEmpty()
                ? Optional.empty()
                : Optional.of(builder.or(fieldPredicates.toArray(Predicate[]::new)));
    }


}
