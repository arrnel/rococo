package org.rococo.tests.service.gateway;

import io.qameta.allure.Step;
import lombok.extern.slf4j.Slf4j;
import org.rococo.tests.client.gateway.CountriesApiClient;
import org.rococo.tests.enums.CountryCode;
import org.rococo.tests.model.CountryDTO;
import org.rococo.tests.service.CountryService;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@ParametersAreNonnullByDefault
public class CountryServiceGateway implements CountryService {

    private final CountriesApiClient countryClient = new CountriesApiClient();

    @Nonnull
    @Override
    @Step("Find country by id = [{id}]")
    public Optional<CountryDTO> findById(UUID id) {
        log.info("Find country by id = [%s]");
        return countryClient.findById(id);
    }

    @Nonnull
    @Override
    @Step("Find country by code = [{code}]")
    public Optional<CountryDTO> findByCode(CountryCode code) {
        log.info("Find country by code = [%s]");
        return countryClient.findByCode(code);
    }

    @Nonnull
    @Override
    @Step("Find all countries")
    public List<CountryDTO> findAll() {
        log.info("Find all countries");
        return countryClient.findAll(0, 300).getContent();
    }

}
