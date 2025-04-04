package org.rococo.countries.data;

import org.springframework.data.jpa.repository.JpaRepository;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Optional;
import java.util.UUID;

@ParametersAreNonnullByDefault
public interface CountryRepository extends JpaRepository<CountryEntity, UUID> {

    @Nonnull
    Optional<CountryEntity> findById(UUID id);

    @Nonnull
    Optional<CountryEntity> findByCode(CountryCode code);

}
