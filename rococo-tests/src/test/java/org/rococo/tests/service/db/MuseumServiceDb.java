package org.rococo.tests.service.db;

import io.qameta.allure.Step;
import lombok.extern.slf4j.Slf4j;
import org.rococo.tests.config.Config;
import org.rococo.tests.data.entity.CountryEntity;
import org.rococo.tests.data.entity.ImageMetadataEntity;
import org.rococo.tests.data.entity.MuseumEntity;
import org.rococo.tests.data.repository.CountryRepository;
import org.rococo.tests.data.repository.FilesRepository;
import org.rococo.tests.data.repository.MuseumRepository;
import org.rococo.tests.data.repository.impl.springJdbc.CountryRepositorySpringJdbc;
import org.rococo.tests.data.repository.impl.springJdbc.FilesRepositorySpringJdbc;
import org.rococo.tests.data.repository.impl.springJdbc.MuseumRepositorySpringJdbc;
import org.rococo.tests.data.tpl.XaTransactionTemplate;
import org.rococo.tests.ex.CountryNotFoundException;
import org.rococo.tests.ex.MuseumNotFoundException;
import org.rococo.tests.mapper.CountryMapper;
import org.rococo.tests.mapper.ImageMapper;
import org.rococo.tests.mapper.MuseumMapper;
import org.rococo.tests.model.MuseumDTO;
import org.rococo.tests.service.MuseumService;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.rococo.tests.enums.EntityType.MUSEUM;

@Slf4j
@SuppressWarnings("ConstantConditions")
@ParametersAreNonnullByDefault
public class MuseumServiceDb implements MuseumService {

    private static final Config CFG = Config.getInstance();

    private final MuseumRepository museumRepository = new MuseumRepositorySpringJdbc();
    private final FilesRepository filesRepository = new FilesRepositorySpringJdbc();
    private final CountryRepository countryRepository = new CountryRepositorySpringJdbc();
    private final XaTransactionTemplate xaTxTemplate = new XaTransactionTemplate(CFG.museumsJdbcUrl(), CFG.filesJdbcUrl(), CFG.countriesJdbcUrl());

    @Nonnull
    @Override
    @Step("Add new museum: [{museum.title}]")
    public MuseumDTO add(MuseumDTO museum) {

        log.info("Add new museum: {}", museum);

        return xaTxTemplate.execute(() -> {
            var currentDate = LocalDateTime.now();
            var country = countryRepository.findById(museum.getLocation().getCountry().getId())
                    .orElseThrow(() -> new CountryNotFoundException(museum.getLocation().getCountry().getId()));
            var museumEntity = museumRepository.add(MuseumMapper.fromDTO(museum)
                    .setCreatedDate(currentDate));
            var imageMetadata = filesRepository.create(
                    ImageMapper.fromBase64Image(MUSEUM, museumEntity.getId(), museum.getPhoto())
                            .setCreatedDate(currentDate));

            return MuseumMapper.toDTO(
                            museumEntity,
                            country,
                            imageMetadata.getContent().getData())
                    .setCountry(CountryMapper.toDTO(country));
        });
    }

    @Nonnull
    @Override
    @Step("Find museum by id: [{id}]")
    public Optional<MuseumDTO> findById(UUID id) {
        log.info("Find museum with id: {}", id);
        return xaTxTemplate.execute(() ->
                museumRepository.findById(id)
                        .map(this::enrichAndConvertToDTO));
    }

    @Nonnull
    @Override
    @Step("Find museum by title: [{title}]")
    public Optional<MuseumDTO> findByTitle(String title) {
        log.info("Find museum with title: {}", title);
        return xaTxTemplate.execute(() ->
                museumRepository.findByTitle(title)
                        .map(this::enrichAndConvertToDTO));
    }

    @Nonnull
    @Override
    @Step("Find all museums by titles")
    public List<MuseumDTO> findAllByPartialTitle(String title) {
        log.info("Find all museums by partial title: {}", title);
        return xaTxTemplate.execute(() -> {
            var museums = museumRepository.findAllByPartialTitle(title);
            return enrichAndConvertAllToDTO(
                    museums,
                    getMuseumsCountriesMap(),
                    getMuseumsImagesMap(museums));
        });
    }

    @Nonnull
    @Override
    @Step("Find all museums by titles")
    public List<MuseumDTO> findAll() {
        log.info("Find all museums");
        return xaTxTemplate.execute(() -> {
            var museums = museumRepository.findAll();
            return enrichAndConvertAllToDTO(
                    museums,
                    getMuseumsCountriesMap(),
                    getMuseumsImagesMap(museums));
        });
    }

    @Nonnull
    @Override
    @Step("Update museum with id: [{museum.id}]")
    public MuseumDTO update(MuseumDTO museum) {

        log.info("Update museum: {}", museum);

        return xaTxTemplate.execute(() -> {

            var museumEntity = museumRepository.findById(museum.getId())
                    .map(oldMuseum -> museumRepository.update(
                            MuseumMapper.updateFromDTO(oldMuseum, museum)))
                    .orElseThrow(() -> new MuseumNotFoundException(museum.getId()));
            var country = countryRepository.findById(museum.getLocation().getCountry().getId())
                    .orElseThrow(() -> new CountryNotFoundException(museum.getLocation().getCountry().getId()));

            var newMetadata = ImageMapper.fromBase64Image(MUSEUM, museum.getId(), museum.getPhoto());
            filesRepository.findByEntityTypeAndEntityId(MUSEUM, museum.getId())
                    .ifPresentOrElse(
                            oldMetadata -> {
                                if (!oldMetadata.getContentHash().equals(newMetadata.getContentHash()))
                                    filesRepository.update(newMetadata.setCreatedDate(oldMetadata.getCreatedDate()));
                            },
                            () -> filesRepository.create(newMetadata.setCreatedDate(LocalDateTime.now()))
                    );

            return MuseumMapper.toDTO(museumEntity, country, newMetadata.getContent().getData());

        });
    }

    @Override
    @Step("Delete museum with id: [{id}]")
    public void delete(UUID id) {
        log.info("Delete museum with id: {}", id);
        xaTxTemplate.execute(() -> {
            museumRepository.findById(id)
                    .ifPresent(museumRepository::remove);
            filesRepository.findByEntityTypeAndEntityId(MUSEUM, id)
                    .ifPresent(filesRepository::remove);
            return null;
        });
    }

    @Override
    @Step("Clear table \"rococo-museums\" and remove all files with entity_type MUSEUM from \"rococo-files\"")
    public void clearAll() {

        log.info("Truncate table \"rococo-museums\" and remove all files with entity_type MUSEUM from \"rococo-files\"");
        xaTxTemplate.execute(() -> {
            museumRepository.removeAll();
            filesRepository.removeAll(MUSEUM);
            return null;
        });
    }

    @Nonnull
    private MuseumDTO enrichAndConvertToDTO(MuseumEntity museumEntity) {
        var photo = filesRepository.findByEntityTypeAndEntityId(MUSEUM, museumEntity.getId())
                .map(metadata -> metadata.getContent().getData())
                .orElse(null);
        var country = countryRepository.findById(museumEntity.getCountryId())
                .orElse(null);
        return MuseumMapper.toDTO(museumEntity, country, photo);
    }

    @Nonnull
    private static List<MuseumDTO> enrichAndConvertAllToDTO(List<MuseumEntity> museums,
                                                            Map<UUID, CountryEntity> countriesMap,
                                                            Map<UUID, byte[]> museumsImagesMap
    ) {
        return museums.stream()
                .map(museum -> MuseumMapper.toDTO(
                        museum,
                        countriesMap.get(museum.getCountryId()),
                        museumsImagesMap.getOrDefault(museum.getId(), null)))
                .toList();
    }

    @Nonnull
    private Map<UUID, byte[]> getMuseumsImagesMap(final List<MuseumEntity> museums) {
        return filesRepository.findAllByEntityTypeAndEntityIds(
                        MUSEUM,
                        museums.stream()
                                .map(MuseumEntity::getId)
                                .toList())
                .stream()
                .collect(Collectors.toMap(
                        ImageMetadataEntity::getEntityId,
                        img -> img.getContent().getThumbnailData()));
    }

    @Nonnull
    private Map<UUID, CountryEntity> getMuseumsCountriesMap() {
        return countryRepository.findAll()
                .stream()
                .collect(Collectors.toMap(
                        CountryEntity::getId,
                        country -> country));
    }

}
