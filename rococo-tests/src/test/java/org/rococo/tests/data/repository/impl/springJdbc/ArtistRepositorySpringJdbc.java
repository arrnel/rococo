package org.rococo.tests.data.repository.impl.springJdbc;

import org.rococo.tests.data.dao.ArtistDao;
import org.rococo.tests.data.dao.impl.springJdbc.ArtistDaoSpringJdbc;
import org.rococo.tests.data.entity.ArtistEntity;
import org.rococo.tests.data.repository.ArtistRepository;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@ParametersAreNonnullByDefault
public class ArtistRepositorySpringJdbc implements ArtistRepository {

    private final ArtistDao artistDao = new ArtistDaoSpringJdbc();

    @Nonnull
    @Override
    public ArtistEntity add(ArtistEntity artist) {
        return artistDao.create(artist);
    }

    @Nonnull
    @Override
    public Optional<ArtistEntity> findById(UUID id) {
        return artistDao.findById(id);
    }

    @Nonnull
    @Override
    public Optional<ArtistEntity> findByName(String name) {
        return artistDao.findByName(name);
    }

    @Nonnull
    @Override
    public List<ArtistEntity> findAllByPartialName(String partialName) {
        return artistDao.findAllByPartialName(partialName);
    }

    @Nonnull
    @Override
    public List<ArtistEntity> findAll() {
        return artistDao.findAll();
    }

    @Nonnull
    @Override
    public ArtistEntity update(ArtistEntity artist) {
        return artistDao.update(artist);
    }

    @Override
    public void remove(ArtistEntity artist) {
        artistDao.remove(artist);
    }

    @Override
    public void removeAll() {
        artistDao.removeAll();
    }

}
