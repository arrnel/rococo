package org.rococo.museum.specs;

import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.rococo.museum.data.MuseumEntity;
import org.rococo.museum.model.MuseumFilter;
import org.rococo.museum.specs.value.EqualUuidSpec;
import org.rococo.museum.specs.value.PartialTextSpec;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class MuseumSpecs implements EntitySpecs<Specification<MuseumEntity>, MuseumFilter> {

    private static final String TITLE_COLUMN = "title";
    private static final String COUNTRY_COLUMN = "countryId";
    private static final String CITY_COLUMN = "city";

    private final PartialTextSpec partialTextSpec;
    private final EqualUuidSpec equalUuidSpec;

    @Override
    public Specification<MuseumEntity> findByCriteria(MuseumFilter filter) {
        return (root, query, builder) -> {

            query.distinct(true);
            List<Predicate> predicates = new ArrayList<>();

            partialTextSpec
                    .specify(TITLE_COLUMN, filter.query(), root, builder)
                    .ifPresent(predicates::add);

            equalUuidSpec
                    .specify(COUNTRY_COLUMN, filter.countryId(), root, builder)
                    .ifPresent(predicates::add);

            partialTextSpec
                    .specify(CITY_COLUMN, filter.city(), root, builder)
                    .ifPresent(predicates::add);

            return builder.and(predicates.toArray(Predicate[]::new));
        };
    }


}
