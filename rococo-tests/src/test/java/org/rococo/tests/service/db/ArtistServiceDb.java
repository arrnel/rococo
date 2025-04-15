package org.rococo.tests.service.db;

import io.qameta.allure.Step;
import lombok.extern.slf4j.Slf4j;
import org.rococo.tests.config.Config;
import org.rococo.tests.data.entity.ArtistEntity;
import org.rococo.tests.data.entity.ImageMetadataEntity;
import org.rococo.tests.data.repository.ArtistRepository;
import org.rococo.tests.data.repository.FilesRepository;
import org.rococo.tests.data.repository.impl.springJdbc.ArtistRepositorySpringJdbc;
import org.rococo.tests.data.repository.impl.springJdbc.FilesRepositorySpringJdbc;
import org.rococo.tests.data.tpl.XaTransactionTemplate;
import org.rococo.tests.ex.ArtistNotFoundException;
import org.rococo.tests.mapper.ArtistMapper;
import org.rococo.tests.mapper.ImageMapper;
import org.rococo.tests.model.ArtistDTO;
import org.rococo.tests.service.ArtistService;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.rococo.tests.enums.EntityType.ARTIST;

@Slf4j
@SuppressWarnings("ConstantConditions")
@ParametersAreNonnullByDefault
public class ArtistServiceDb implements ArtistService {

    private static final Config CFG = Config.getInstance();

    private final ArtistRepository artistRepository = new ArtistRepositorySpringJdbc();
    private final FilesRepository filesRepository = new FilesRepositorySpringJdbc();
    private final XaTransactionTemplate xaTxTemplate = new XaTransactionTemplate(CFG.artistsJdbcUrl(), CFG.filesJdbcUrl());

    @Nonnull
    @Override
    @Step("Add new artist: [{artist.name}]")
    public ArtistDTO add(ArtistDTO artist) {
        log.info("Add new artist: {}", artist);
        return xaTxTemplate.execute(() -> {
            var ae = artistRepository.add(
                    ArtistMapper.fromDTO(artist));
            var imageMeta = filesRepository.create(
                    ImageMapper.fromBase64Image(ARTIST, ae.getId(), artist.getPhoto()));
            return ArtistMapper.toDTO(ae, imageMeta.getContent().getData());
        });
    }

    @Nonnull
    @Override
    @Step("Find artist by id: [{id}]")
    public Optional<ArtistDTO> findById(UUID id) {
        log.info("Find artist with id: {}", id);
        return xaTxTemplate.execute(() ->
                artistRepository.findById(id)
                        .map(this::enrichAndConvertToDTO));
    }

    @Nonnull
    @Override
    @Step("Find artist by name: [{name}]")
    public Optional<ArtistDTO> findByName(String name) {
        log.info("Find artist with name: {}", name);
        return xaTxTemplate.execute(() ->
                artistRepository.findByName(name)
                        .map(this::enrichAndConvertToDTO));
    }

    @Nonnull
    @Override
    @Step("Find all artists")
    public List<ArtistDTO> findAllByPartialName(String partialName) {
        log.info("Find all artists by names: names");
        return xaTxTemplate.execute(() ->
                enrichAndConvertAllToDTO(artistRepository.findAllByPartialName(partialName)));
    }

    @Nonnull
    @Override
    @Step("Find all artists")
    public List<ArtistDTO> findAll() {
        log.info("Find all artists");
        return xaTxTemplate.execute(() ->
                enrichAndConvertAllToDTO(artistRepository.findAll()));
    }

    @Nonnull
    @Override
    @Step("Update artist with id: [{artist.id}]")
    public ArtistDTO update(ArtistDTO artist) {
        log.info("Update artist: {}", artist);
        return xaTxTemplate.execute(() -> {
            var artistEntity = artistRepository.findById(artist.getId())
                    .orElseThrow(() -> new ArtistNotFoundException(artist.getId()));

            var photo = filesRepository.findByEntityTypeAndEntityId(ARTIST, artist.getId())
                    .map(oldMeta -> {
                        var newMetadata = ImageMapper.fromBase64Image(ARTIST, artist.getId(), artist.getPhoto());
                        return oldMeta.getContentHash().equals(newMetadata.getContentHash()) && !artist.getPhoto().isEmpty()
                                ? oldMeta.getContent().getData()
                                : filesRepository.update(newMetadata).getContent().getData();
                    })
                    .orElseGet(() -> artist.getPhoto().isEmpty()
                            ? null
                            : filesRepository.create(ImageMapper.fromBase64Image(ARTIST, artist.getId(), artist.getPhoto())).getContent().getData()
                    );

            return ArtistMapper.toDTO(
                    artistRepository.update(
                            ArtistMapper.updateFromDTO(artistEntity, artist)),
                    photo);
        });
    }

    @Override
    @Step("Delete artist with id: [{id}]")
    public void delete(UUID id) {
        log.info("Delete artist with id: {}", id);
        xaTxTemplate.execute(() -> {
            artistRepository.findById(id)
                    .ifPresent(artistRepository::remove);
            filesRepository.findByEntityTypeAndEntityId(ARTIST, id)
                    .ifPresent(filesRepository::remove);
            return null;
        });
    }

    @Override
    @Step("Clear table \"rococo-artists\" and remove all files with entity_type ARTIST from \"rococo-files\"")
    public void clearAll() {
        log.info("Truncate table \"rococo-artists\" and remove all files with entity_type ARTIST from \"rococo-files\"");
        xaTxTemplate.execute(() -> {
            artistRepository.removeAll();
            filesRepository.removeAll(ARTIST);
            return null;
        });
    }

    @Nonnull
    private ArtistDTO enrichAndConvertToDTO(ArtistEntity entity) {
        var photo = filesRepository.findByEntityTypeAndEntityId(ARTIST, entity.getId())
                .map(im -> im.getContent().getData())
                .orElse(null);
        return ArtistMapper.toDTO(entity, photo);
    }

    @Nonnull
    private List<ArtistDTO> enrichAndConvertAllToDTO(List<ArtistEntity> artists) {

        var artistIds = artists.stream().map(ArtistEntity::getId).toList();
        var artistsImage = filesRepository.findAllByEntityTypeAndEntityIds(ARTIST, artistIds);

        Map<UUID, byte[]> artistBase64ImageMap = artistsImage.stream()
                .collect(Collectors.toMap(
                        ImageMetadataEntity::getEntityId,
                        image -> image.getContent().getThumbnailData()
                ));

        return artists.stream()
                .map(artist -> ArtistMapper.toDTO(
                        artist,
                        artistBase64ImageMap.get(artist.getId())))
                .toList();
    }

}
