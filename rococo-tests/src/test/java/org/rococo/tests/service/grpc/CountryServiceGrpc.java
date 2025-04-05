package org.rococo.tests.service.grpc;

import lombok.extern.slf4j.Slf4j;
import org.rococo.tests.client.grpc.CountriesGrpcClient;
import org.rococo.tests.enums.CountryCode;
import org.rococo.tests.model.CountryDTO;
import org.rococo.tests.service.CountryService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@ParametersAreNonnullByDefault
public class CountryServiceGrpc implements CountryService {

    private final CountriesGrpcClient countryClient = new CountriesGrpcClient();

    @Nonnull
    @Override
    public Optional<CountryDTO> findById(UUID id) {
        return countryClient.findById(id);
    }

    @Nonnull
    @Override
    public Optional<CountryDTO> findByCode(CountryCode code) {
        return countryClient.findByCode(code);
    }

    @Nonnull
    @Override
    public List<CountryDTO> findAll() {
        log.info("Find all countries");
        Pageable pageable = PageRequest.of(0, 300);
        return countryClient.findAll(pageable).getContent();
    }

}
