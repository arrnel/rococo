package org.rococo.tests.data.repository.impl.springJdbc;

import org.rococo.tests.data.dao.PaintingDao;
import org.rococo.tests.data.dao.impl.springJdbc.PaintingDaoSpringJdbc;
import org.rococo.tests.data.entity.PaintingEntity;
import org.rococo.tests.data.repository.PaintingRepository;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@ParametersAreNonnullByDefault
public class PaintingRepositorySpringJdbc implements PaintingRepository {

    private final PaintingDao paintingDao = new PaintingDaoSpringJdbc();

    @Nonnull
    @Override
    public PaintingEntity create(PaintingEntity painting) {
        return paintingDao.create(painting);
    }

    @Nonnull
    @Override
    public Optional<PaintingEntity> findById(UUID id) {
        return paintingDao.findById(id);
    }

    @Nonnull
    @Override
    public Optional<PaintingEntity> findByTitle(String title) {
        return paintingDao.findByTitle(title);
    }

    @Nonnull
    @Override
    public List<PaintingEntity> findAllByPartialTitle(String partialTitle) {
        return paintingDao.findAllByPartialTitle(partialTitle);
    }

    @Nonnull
    @Override
    public List<PaintingEntity> findAllByArtistId(UUID artistId) {
        return paintingDao.findAllByArtistId(artistId);
    }

    @Nonnull
    @Override
    public List<PaintingEntity> findAllByTitles(List<String> titles) {
        return paintingDao.findAll();
    }

    @Nonnull
    @Override
    public List<PaintingEntity> findAll() {
        return paintingDao.findAll();
    }

    @Nonnull
    @Override
    public PaintingEntity update(PaintingEntity painting) {
        return paintingDao.update(painting);
    }

    @Override
    public void remove(PaintingEntity painting) {
        paintingDao.remove(painting);
    }

    @Override
    public void removeAll() {
        paintingDao.removeAll();
    }

}
