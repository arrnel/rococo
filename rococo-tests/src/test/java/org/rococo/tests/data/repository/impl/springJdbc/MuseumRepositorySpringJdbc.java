package org.rococo.tests.data.repository.impl.springJdbc;

import org.rococo.tests.data.dao.MuseumDao;
import org.rococo.tests.data.dao.impl.springJdbc.MuseumDaoSpringJdbc;
import org.rococo.tests.data.entity.MuseumEntity;
import org.rococo.tests.data.repository.MuseumRepository;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@ParametersAreNonnullByDefault
public class MuseumRepositorySpringJdbc implements MuseumRepository {

    private final MuseumDao museumDao = new MuseumDaoSpringJdbc();

    @Nonnull
    @Override
    public MuseumEntity add(MuseumEntity museum) {
        return museumDao.create(museum);
    }

    @Nonnull
    @Override
    public Optional<MuseumEntity> findById(UUID id) {
        return museumDao.findById(id);
    }

    @Nonnull
    @Override
    public Optional<MuseumEntity> findByTitle(String title) {
        return museumDao.findByTitle(title);
    }

    @Nonnull
    @Override
    public List<MuseumEntity> findAllByPartialTitle(String partialTitle) {
        return museumDao.findAllByPartialTitle(partialTitle);
    }

    @Nonnull
    @Override
    public List<MuseumEntity> findAll() {
        return museumDao.findAll();
    }

    @Nonnull
    @Override
    public MuseumEntity update(MuseumEntity museum) {
        return museumDao.update(museum);
    }

    @Override
    public void remove(MuseumEntity museum) {
        museumDao.remove(museum);
    }

    @Override
    public void removeAll() {
        museumDao.removeAll();
    }

}
