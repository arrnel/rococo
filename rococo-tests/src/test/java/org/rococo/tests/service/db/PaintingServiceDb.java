package org.rococo.tests.service.db;

import io.qameta.allure.Step;
import lombok.extern.slf4j.Slf4j;
import org.rococo.tests.config.Config;
import org.rococo.tests.data.entity.*;
import org.rococo.tests.data.repository.*;
import org.rococo.tests.data.repository.impl.springJdbc.*;
import org.rococo.tests.data.tpl.XaTransactionTemplate;
import org.rococo.tests.enums.EntityType;
import org.rococo.tests.ex.ArtistNotFoundException;
import org.rococo.tests.ex.CountryNotFoundException;
import org.rococo.tests.ex.MuseumNotFoundException;
import org.rococo.tests.ex.PaintingNotFoundException;
import org.rococo.tests.mapper.ImageMapper;
import org.rococo.tests.mapper.PaintingMapper;
import org.rococo.tests.model.PaintingDTO;
import org.rococo.tests.service.PaintingService;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.rococo.tests.enums.EntityType.PAINTING;

@Slf4j
@SuppressWarnings("ConstantConditions")
@ParametersAreNonnullByDefault
public class PaintingServiceDb implements PaintingService {

    private static final Config CFG = Config.getInstance();

    private final PaintingRepository paintingRepository = new PaintingRepositorySpringJdbc();
    private final ArtistRepository artistRepository = new ArtistRepositorySpringJdbc();
    private final CountryRepository countryRepository = new CountryRepositorySpringJdbc();
    private final MuseumRepository museumRepository = new MuseumRepositorySpringJdbc();
    private final FilesRepository filesRepository = new FilesRepositorySpringJdbc();
    private final XaTransactionTemplate xaTxTemplate = new XaTransactionTemplate(
            CFG.artistsJdbcUrl(),
            CFG.countriesJdbcUrl(),
            CFG.filesJdbcUrl(),
            CFG.museumsJdbcUrl(),
            CFG.paintingsJdbcUrl());

    @Nonnull
    @Override
    @Step("Add new painting: [{painting.title}]")
    public PaintingDTO add(PaintingDTO painting) {
        log.info("Add new painting: {}", painting);
        return xaTxTemplate.execute(() -> {
            var artistEntity = artistRepository.findById(painting.getArtist().getId())
                    .orElseThrow(() -> new ArtistNotFoundException(painting.getArtist().getId()));
            var museumEntity = museumRepository.findById(painting.getMuseum().getId())
                    .orElseThrow(() -> new MuseumNotFoundException(painting.getMuseum().getId()));
            var countryEntity = countryRepository.findById(museumEntity.getCountryId())
                    .orElseThrow(() -> new CountryNotFoundException(museumEntity.getCountryId()));
            var paintingEntity = paintingRepository.create(
                    PaintingMapper.toEntity(painting));
            var photo = filesRepository.create(
                    ImageMapper.fromBase64Image(PAINTING, paintingEntity.getId(), painting.getPhoto())).getContent().getData();
            return PaintingMapper.toDTO(paintingEntity, artistEntity, museumEntity, countryEntity, photo);
        });
    }

    @Nonnull
    @Override
    @Step("Find painting by id: [{id}]")
    public Optional<PaintingDTO> findById(UUID id) {
        log.info("Find painting with id: {}", id);
        return xaTxTemplate.execute(() ->
                paintingRepository.findById(id)
                        .map(this::enrichPainting));
    }

    @Nonnull
    @Override
    public Optional<PaintingDTO> findByTitle(String title) {
        log.info("Find painting with title: {}", title);
        return xaTxTemplate.execute(() ->
                paintingRepository.findByTitle(title)
                        .map(this::enrichPainting));
    }

    @Nonnull
    @Override
    public List<PaintingDTO> findAllByPartialTitle(String partialTitle) {
        log.info("Find all paintings by partial title: {}", partialTitle);
        return xaTxTemplate.execute(() ->
                enrichPaintings(paintingRepository.findAllByPartialTitle(partialTitle)));
    }

    @Nonnull
    @Override
    public List<PaintingDTO> findAllByArtistId(UUID artistId) {
        log.info("Find all paintings by artist id: {}", artistId);
        return xaTxTemplate.execute(() ->
                enrichPaintings(paintingRepository.findAllByArtistId(artistId)));
    }

    @Nonnull
    @Override
    @Step("Find all paintings")
    public List<PaintingDTO> findAll() {
        log.info("Find all paintings");
        return xaTxTemplate.execute(() ->
                enrichPaintings(paintingRepository.findAll()));
    }

    @Nonnull
    @Override
    @Step("Update painting with id: [{painting.id}]")
    public PaintingDTO update(PaintingDTO painting) {

        log.info("Update painting: {}", painting);

        return xaTxTemplate.execute(() -> {

            var oldPaintingEntity = paintingRepository.findById(painting.getId())
                    .orElseThrow(() -> new PaintingNotFoundException(painting.getId()));

            var artistEntity = artistRepository.findById(painting.getArtist().getId())
                    .orElseThrow(() -> new ArtistNotFoundException(painting.getArtist().getId()));
            var museumEntity = museumRepository.findById(painting.getMuseum().getId())
                    .orElseThrow(() -> new MuseumNotFoundException(painting.getMuseum().getId()));
            var countryEntity = countryRepository.findById(museumEntity.getCountryId())
                    .orElseThrow(() -> new CountryNotFoundException(museumEntity.getCountryId()));
            var paintingRequest = PaintingMapper.updateFromDTO(oldPaintingEntity, painting);
            var paintingEntity = paintingRepository.update(paintingRequest);

            var photo = Optional.ofNullable(painting.getPhoto())
                    .map(p -> {
                        var meta = ImageMapper.fromBase64Image(PAINTING, paintingEntity.getId(), painting.getPhoto());
                        return filesRepository.findByEntityTypeAndEntityId(PAINTING, painting.getId()).isPresent()
                                ? filesRepository.update(meta)
                                : filesRepository.create(meta);
                    })
                    .orElse(ImageMetadataEntity.empty())
                    .getContent()
                    .getData();

            return PaintingMapper.toDTO(paintingEntity, artistEntity, museumEntity, countryEntity, photo);

        });

    }

    @Override
    @Step("Delete painting with id: [{id}]")
    public void delete(UUID id) {

        log.info("Delete painting with id: {}", id);
        xaTxTemplate.execute(() -> {
            paintingRepository.findById(id)
                    .ifPresent(paintingRepository::remove);
            filesRepository.findByEntityTypeAndEntityId(PAINTING, id)
                    .ifPresent(filesRepository::remove);
            return null;
        });

    }

    @Override
    @Step("Truncate table \"rococo-paintings\" and remove all files with entity_type PAINTING from \"rococo-files\"")
    public void clearAll() {
        log.info("Truncate table \"rococo-paintings\" and remove all files with entity_type PAINTING from \"rococo-files\"");
        xaTxTemplate.execute(() -> {
            paintingRepository.removeAll();
            filesRepository.removeAll(PAINTING);
            return null;
        });
    }

    @Nonnull
    private PaintingDTO enrichPainting(PaintingEntity painting) {
        var artistEntity = artistRepository.findById(painting.getArtistId())
                .orElseThrow(() -> new ArtistNotFoundException(painting.getArtistId()));
        var museumEntity = museumRepository.findById(painting.getMuseumId())
                .orElseThrow(() -> new MuseumNotFoundException(painting.getMuseumId()));
        var countryEntity = countryRepository.findById(museumEntity.getCountryId())
                .orElseThrow(() -> new CountryNotFoundException(museumEntity.getCountryId()));
        var photo = filesRepository.findByEntityTypeAndEntityId(EntityType.PAINTING, painting.getId())
                .map(im -> im.getContent().getData())
                .orElse(null);
        return PaintingMapper.toDTO(painting, artistEntity, museumEntity, countryEntity, photo);
    }

    @Nonnull
    private List<PaintingDTO> enrichPaintings(List<PaintingEntity> paintings) {

        var paintingIds = paintings.stream()
                .map(PaintingEntity::getId)
                .toList();
        var museumIds = paintings.stream()
                .map(PaintingEntity::getMuseumId)
                .toList();
        var artistIds = paintings.stream()
                .map(PaintingEntity::getArtistId)
                .toList();

        var artistEntitiesMap = artistRepository.findAllByIds(artistIds).stream()
                .collect(Collectors.toMap(ArtistEntity::getId, artist -> artist));
        var museumEntitiesMap = museumRepository.findAllByIds(museumIds).stream()
                .collect(Collectors.toMap(MuseumEntity::getId, museum -> museum));
        var countriesMap = countryRepository.findAll().stream()
                .collect(Collectors.toMap(CountryEntity::getId, country -> country));
        var paintingsImagesMap = filesRepository.findAllByEntityTypeAndEntityIds(EntityType.PAINTING, paintingIds).stream()
                .collect(Collectors.toMap(
                        ImageMetadataEntity::getEntityId,
                        im -> im.getContent().getThumbnailData()));

        return paintings.stream()
                .map(painting -> PaintingMapper.toDTO(
                        painting,
                        artistEntitiesMap.get(painting.getArtistId()),
                        museumEntitiesMap.get(painting.getMuseumId()),
                        countriesMap.get(museumEntitiesMap.get(painting.getMuseumId()).getCountryId()),
                        paintingsImagesMap.get(painting.getId())))
                .toList();
    }

}
