package org.rococo.tests.service;

import org.rococo.tests.enums.CountryCode;
import org.rococo.tests.model.CountryDTO;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@ParametersAreNonnullByDefault
public interface CountryService {

    @Nonnull
    Optional<CountryDTO> findById(UUID id);

    @Nonnull
    Optional<CountryDTO> findByCode(CountryCode code);

    @Nonnull
    List<CountryDTO> findAll();

}
