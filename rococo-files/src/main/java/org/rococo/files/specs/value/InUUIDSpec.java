package org.rococo.files.specs.value;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

@Component
public class InUUIDSpec {

    public Optional<Predicate> specify(
            @Nonnull String fieldName,
            @Nullable UUID[] ids,
            @Nonnull Root<?> root,
            @Nonnull CriteriaBuilder builder
    ) {

        if (ids == null || ids.length == 0)
            return Optional.empty();

        final var predicate = builder.in(root.get(fieldName));
        Stream.of(ids).forEach(predicate::value);
        return Optional.of(predicate);

    }

}
