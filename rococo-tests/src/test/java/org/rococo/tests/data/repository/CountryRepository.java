package org.rococo.tests.data.repository;


import org.rococo.tests.data.entity.CountryEntity;
import org.rococo.tests.enums.CountryCode;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@ParametersAreNonnullByDefault
public interface CountryRepository {

    @Nonnull
    CountryEntity create(CountryEntity user);

    @Nonnull
    Optional<CountryEntity> findById(UUID id);

    @Nonnull
    Optional<CountryEntity> findByCode(CountryCode code);

    @Nonnull
    List<CountryEntity> findAll();

    @Nonnull
    CountryEntity update(CountryEntity user);

    void remove(CountryEntity user);

    void removeAll();

}
