package org.rococo.tests.client.gateway;

import io.qameta.allure.Step;
import okhttp3.logging.HttpLoggingInterceptor;
import org.rococo.tests.client.gateway.core.RestClient;
import org.rococo.tests.enums.CountryCode;
import org.rococo.tests.model.CountryDTO;
import org.springframework.data.domain.Page;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Optional;
import java.util.UUID;

@ParametersAreNonnullByDefault
public class CountriesApiClient extends RestClient {

    private final CountriesApi countriesApi;

    public CountriesApiClient() {
        super(CFG.gatewayUrl(), HttpLoggingInterceptor.Level.HEADERS);
        this.countriesApi = create(CountriesApi.class);
    }

    @Step("Send request GET: [rococo-countries]/api/country")
    public Optional<CountryDTO> findById(UUID id) {
        return executeWithOptional(countriesApi.findById(id));
    }

    @Step("Send request GET: [rococo-countries]/api/country/code")
    public Optional<CountryDTO> findByCode(CountryCode code) {
        if (code == CountryCode.EMPTY) throw new IllegalArgumentException("Country code cannot be empty");
        return executeWithOptional(countriesApi.findByCode(code.name()));
    }

    @Step("Send request GET: [rococo-countries]/api/country")
    public Page<CountryDTO> findAll(int page, int size) {
        return execute(countriesApi.findAll(page, size));
    }

}
