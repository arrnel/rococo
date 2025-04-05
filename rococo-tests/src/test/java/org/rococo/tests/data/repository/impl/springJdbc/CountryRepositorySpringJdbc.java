package org.rococo.tests.data.repository.impl.springJdbc;

import org.rococo.tests.data.dao.CountryDao;
import org.rococo.tests.data.dao.impl.springJdbc.CountryDaoSpringJdbc;
import org.rococo.tests.data.entity.CountryEntity;
import org.rococo.tests.data.repository.CountryRepository;
import org.rococo.tests.enums.CountryCode;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@ParametersAreNonnullByDefault
public class CountryRepositorySpringJdbc implements CountryRepository {

    private final CountryDao countryDao = new CountryDaoSpringJdbc();

    @Nonnull
    @Override
    public CountryEntity create(CountryEntity museum) {
        return countryDao.create(museum);
    }

    @Nonnull
    @Override
    public Optional<CountryEntity> findById(UUID id) {
        return countryDao.findById(id);
    }

    @Nonnull
    @Override
    public Optional<CountryEntity> findByCode(CountryCode code) {
        return countryDao.findByCode(code);
    }

    @Nonnull
    @Override
    public List<CountryEntity> findAll() {
        return countryDao.findAll();
    }

    @Nonnull
    @Override
    public CountryEntity update(CountryEntity museum) {
        return countryDao.update(museum);
    }

    @Override
    public void remove(CountryEntity museum) {
        countryDao.remove(museum);
    }

    @Override
    public void removeAll() {
        countryDao.removeAll();
    }

}
