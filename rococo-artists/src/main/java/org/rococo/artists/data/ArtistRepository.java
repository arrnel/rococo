package org.rococo.artists.data;

import jakarta.annotation.Nonnull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@ParametersAreNonnullByDefault
public interface ArtistRepository extends JpaRepository<ArtistEntity, UUID>, JpaSpecificationExecutor<ArtistEntity> {

    @Nonnull
    Optional<ArtistEntity> findById(UUID id);

    @Nonnull
    Optional<ArtistEntity> findByName(String name);

    @Nonnull
    @Query("SELECT a FROM ArtistEntity a WHERE a.name IN (:names)")
    List<ArtistEntity> findAllByNames(List<String> names);

}
