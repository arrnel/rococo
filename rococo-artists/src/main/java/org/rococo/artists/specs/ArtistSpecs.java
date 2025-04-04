package org.rococo.artists.specs;

import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.rococo.artists.data.ArtistEntity;
import org.rococo.artists.model.ArtistFilter;
import org.rococo.artists.specs.value.PartialTextSpec;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class ArtistSpecs implements org.rococo.artists.specs.EntitySpecs<Specification<ArtistEntity>, ArtistFilter> {

    private static final String NAME_COLUMN = "name";

    private final PartialTextSpec partialTextSpec;

    @Override
    public Specification<ArtistEntity> findByCriteria(ArtistFilter filter) {
        return (root, query, builder) -> {

            query.distinct(true);
            List<Predicate> predicates = new ArrayList<>();

            partialTextSpec
                    .specify(NAME_COLUMN, filter.query(), root, builder)
                    .ifPresent(predicates::add);

            return builder.and(predicates.toArray(Predicate[]::new));

        };
    }

}
