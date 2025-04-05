package org.rococo.files.specs;

import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.rococo.files.data.entity.ImageFilter;
import org.rococo.files.data.entity.ImageMetadataEntity;
import org.rococo.files.specs.value.EqualEnumSpec;
import org.rococo.files.specs.value.InUUIDSpec;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class ImageSpecs implements EntitySpecs<Specification<ImageMetadataEntity>, ImageFilter> {

    private static final String ENTITY_ID_COLUMN = "entityId";
    private static final String ENTITY_TYPE_COLUMN = "entityType";
    private static final String CONTENT_ID_COLUMN = "content";

    private final InUUIDSpec inUUIDSpec;
    private final EqualEnumSpec equalEnumSpec;

    @Override
    public Specification<ImageMetadataEntity> findByCriteria(ImageFilter filter) {
        return (root, query, builder) -> {

            query.distinct(true);
            List<Predicate> predicates = new ArrayList<>();

            equalEnumSpec
                    .specify(ENTITY_TYPE_COLUMN, filter.entityType().name(), root, builder)
                    .ifPresent(predicates::add);

            inUUIDSpec
                    .specify(ENTITY_ID_COLUMN, filter.entityIds(), root, builder)
                    .ifPresent(predicates::add);

            return builder.and(predicates.toArray(Predicate[]::new));

        };
    }

}
