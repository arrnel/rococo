package org.rococo.paintings.specs;

import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.rococo.paintings.data.PaintingEntity;
import org.rococo.paintings.model.PaintingFilter;
import org.rococo.paintings.specs.value.EqualUuidSpec;
import org.rococo.paintings.specs.value.PartialTextSpec;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class PaintingSpecs implements EntitySpecs<Specification<PaintingEntity>, PaintingFilter> {

    private static final String TITLE_COLUMN = "title";
    private static final String DESCRIPTION_COLUMN = "description";
    private static final String ARTIST_ID_COLUMN = "artistId";
    private static final String MUSEUM_ID_COLUMN = "museumId";

    private final PartialTextSpec partialTextSpec;
    private final EqualUuidSpec equalUuidSpec;

    @Override
    public Specification<PaintingEntity> findByCriteria(PaintingFilter filter) {
        return (root, query, builder) -> {

            query.distinct(true);
            List<Predicate> predicates = new ArrayList<>();

            partialTextSpec
                    .specify(TITLE_COLUMN, filter.query(), root, builder)
                    .ifPresent(predicates::add);

            equalUuidSpec
                    .specify(ARTIST_ID_COLUMN, filter.artistId(), root, builder)
                    .ifPresent(predicates::add);

            equalUuidSpec
                    .specify(MUSEUM_ID_COLUMN, filter.museumId(), root, builder)
                    .ifPresent(predicates::add);

            return builder.and(predicates.toArray(Predicate[]::new));

        };
    }

}
