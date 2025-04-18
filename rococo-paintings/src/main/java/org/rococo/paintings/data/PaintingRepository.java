package org.rococo.paintings.data;

import jakarta.annotation.Nonnull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Optional;
import java.util.UUID;

@ParametersAreNonnullByDefault
public interface PaintingRepository extends JpaRepository<PaintingEntity, UUID>, JpaSpecificationExecutor<PaintingEntity> {

    @Nonnull
    Optional<PaintingEntity> findById(UUID id);

    @Nonnull
    Optional<PaintingEntity> findByTitle(String title);

}
