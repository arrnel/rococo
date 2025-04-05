package org.rococo.tests.service.db;

import io.qameta.allure.Step;
import lombok.extern.slf4j.Slf4j;
import org.rococo.tests.config.Config;
import org.rococo.tests.data.repository.CountryRepository;
import org.rococo.tests.data.repository.impl.springJdbc.CountryRepositorySpringJdbc;
import org.rococo.tests.enums.CountryCode;
import org.rococo.tests.mapper.CountryMapper;
import org.rococo.tests.model.CountryDTO;
import org.rococo.tests.service.CountryService;
import org.springframework.jdbc.support.JdbcTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import static org.rococo.tests.data.tpl.DataSources.dataSource;

@Slf4j
@ParametersAreNonnullByDefault
public class CountryServiceDb implements CountryService {

    private static final Config CFG = Config.getInstance();

    private final CountryRepository countryRepository = new CountryRepositorySpringJdbc();
    private final TransactionTemplate txTemplate = new TransactionTemplate(new JdbcTransactionManager(dataSource(CFG.countriesJdbcUrl())));

    @Nonnull
    @Override
    @Step("Find country by id: [{id}]")
    public Optional<CountryDTO> findById(UUID id) {
        log.info("Find country by id: {}", id);
        return Objects.requireNonNull(
                txTemplate.execute(status ->
                        countryRepository.findById(id)
                                .map(CountryMapper::toDTO)));
    }

    @Nonnull
    @Override
    @Step("Find country by code: [{code}]")
    public Optional<CountryDTO> findByCode(CountryCode code) {
        log.info("Find country by code: {}", code);
        return Objects.requireNonNull(
                txTemplate.execute(status ->
                        countryRepository.findByCode(code)
                                .map(CountryMapper::toDTO)));
    }

    @Nonnull
    @Override
    @Step("Find all countries")
    public List<CountryDTO> findAll() {
        log.info("Find all countries");
        return Objects.requireNonNull(
                txTemplate.execute(status ->
                        countryRepository.findAll().stream()
                                .map(CountryMapper::toDTO)
                                .toList()));
    }

}
