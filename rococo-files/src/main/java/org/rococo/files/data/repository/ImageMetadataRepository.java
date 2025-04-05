package org.rococo.files.data.repository;

import org.rococo.files.data.entity.EntityType;
import org.rococo.files.data.entity.ImageMetadataEntity;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.EntityGraph.EntityGraphType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@ParametersAreNonnullByDefault
public interface ImageMetadataRepository extends JpaRepository<ImageMetadataEntity, UUID>, JpaSpecificationExecutor<ImageMetadataEntity> {

    @Nonnull
    @EntityGraph(type = EntityGraphType.LOAD, attributePaths = "content")
    Optional<ImageMetadataEntity> findByEntityTypeAndEntityId(EntityType type, UUID entityId);

    @Nonnull
    @EntityGraph(type = EntityGraphType.LOAD, attributePaths = "content")
    List<ImageMetadataEntity> findAll(@Nullable Specification<ImageMetadataEntity> spec);

}
