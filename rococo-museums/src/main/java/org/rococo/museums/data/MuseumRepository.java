package org.rococo.museums.data;

import jakarta.annotation.Nonnull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Optional;
import java.util.UUID;

@ParametersAreNonnullByDefault
public interface MuseumRepository extends JpaRepository<MuseumEntity, UUID>, JpaSpecificationExecutor<MuseumEntity> {

    @Nonnull
    Optional<MuseumEntity> findById(UUID id);

    @Nonnull
    Optional<MuseumEntity> findByTitle(String title);


}
