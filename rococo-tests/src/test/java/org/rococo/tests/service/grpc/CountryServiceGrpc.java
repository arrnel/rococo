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
import java.util.function.Function;

@Slf4j
@ParametersAreNonnullByDefault
public class CountryServiceGrpc implements CountryService {

    @Nonnull
    @Override
    public Optional<CountryDTO> findById(UUID id) {
        log.info("Find country by id: {}", id);
        return withClient(countriesClient ->
                countriesClient.findById(id));
    }

    @Nonnull
    @Override
    public Optional<CountryDTO> findByCode(CountryCode code) {
        log.info("Find country by code: {}", code);
        return withClient(countriesClient ->
                countriesClient.findByCode(code));
    }

    @Nonnull
    @Override
    public List<CountryDTO> findAll() {
        log.info("Find all countries");
        Pageable pageable = PageRequest.of(0, 300);
        return withClient(countriesClient ->
                countriesClient.findAll(pageable).getContent());
    }

    private <T> T withClient(Function<CountriesGrpcClient, T> operation) {
        try (CountriesGrpcClient client = new CountriesGrpcClient()) {
            return operation.apply(client);
        }
    }

}
