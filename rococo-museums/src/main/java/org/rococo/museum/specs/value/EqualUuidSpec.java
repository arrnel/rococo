package org.rococo.museum.specs.value;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
public class EqualUuidSpec {

    public Optional<Predicate> specify(
            @Nonnull String fieldName,
            @Nullable UUID uuid,
            @Nonnull Root<?> root,
            @Nonnull CriteriaBuilder builder
    ) {
        return (uuid != null)
                ? Optional.of(builder.equal(root.get(fieldName), uuid))
                : Optional.empty();
    }

}
