package org.rococo.museum.specs.value;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class EqualTextSpec {

    public Optional<Predicate> specify(
            @Nonnull String fieldName,
            @Nullable String text,
            @Nonnull Root<?> root,
            @Nonnull CriteriaBuilder builder
    ) {
        return (text != null && !text.trim().isEmpty())
                ? Optional.of(builder.equal(builder.lower(root.get(fieldName)), text.toLowerCase()))
                : Optional.empty();
    }

}
