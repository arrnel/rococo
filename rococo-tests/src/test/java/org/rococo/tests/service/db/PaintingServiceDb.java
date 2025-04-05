package org.rococo.tests.service.db;

import io.qameta.allure.Step;
import lombok.extern.slf4j.Slf4j;
import org.rococo.tests.config.Config;
import org.rococo.tests.data.entity.ImageMetadataEntity;
import org.rococo.tests.data.entity.PaintingEntity;
import org.rococo.tests.data.repository.FilesRepository;
import org.rococo.tests.data.repository.PaintingRepository;
import org.rococo.tests.data.repository.impl.springJdbc.FilesRepositorySpringJdbc;
import org.rococo.tests.data.repository.impl.springJdbc.PaintingRepositorySpringJdbc;
import org.rococo.tests.data.tpl.XaTransactionTemplate;
import org.rococo.tests.enums.EntityType;
import org.rococo.tests.ex.PaintingNotFoundException;
import org.rococo.tests.mapper.ImageMapper;
import org.rococo.tests.mapper.PaintingMapper;
import org.rococo.tests.model.PaintingDTO;
import org.rococo.tests.service.PaintingService;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.rococo.tests.enums.EntityType.PAINTING;

@Slf4j
@SuppressWarnings("unchecked")
@ParametersAreNonnullByDefault
public class PaintingServiceDb implements PaintingService {

    private static final Config CFG = Config.getInstance();

    private final PaintingRepository paintingRepository = new PaintingRepositorySpringJdbc();
    private final FilesRepository filesRepository = new FilesRepositorySpringJdbc();
    private final XaTransactionTemplate xaTxTemplate = new XaTransactionTemplate(CFG.filesJdbcUrl(), CFG.paintingsJdbcUrl());

    @Nonnull
    @Override
    @Step("Add new painting: [{painting.title}]")
    public PaintingDTO add(PaintingDTO painting) {
        log.info("Add new painting: {}", painting);
        return xaTxTemplate.execute(() -> {
            var paintingEntity = paintingRepository.create(
                    PaintingMapper.toEntity(painting));
            var photo = filesRepository.create(
                    ImageMapper.fromBase64Image(PAINTING, paintingEntity.getId(), painting.getPhoto())).getContent().getData();
            return PaintingMapper.toDTO(paintingEntity, photo);
        });
    }

    @Nonnull
    @Override
    @Step("Find painting by id: [{id}]")
    public Optional<PaintingDTO> findById(UUID id) {
        log.info("Find painting with id: {}", id);
        return xaTxTemplate.execute(() ->
                paintingRepository.findById(id)
                        .map(painting -> PaintingMapper.toDTO(
                                painting,
                                findPaintingImage(painting.getId()))));
    }

    @Nonnull
    @Override
    public Optional<PaintingDTO> findByTitle(String title) {
        log.info("Find painting with title: {}", title);
        return xaTxTemplate.execute(() ->
                paintingRepository.findByTitle(title)
                        .map(painting -> PaintingMapper.toDTO(
                                painting,
                                findPaintingImage(painting.getId()))));
    }

    @Nonnull
    @Override
    public List<PaintingDTO> findAllByPartialTitle(String partialTitle) {
        log.info("Find all paintings by partial title: {}", partialTitle);
        return xaTxTemplate.execute(() -> enrichAndConvertAllToDTO(paintingRepository.findAllByPartialTitle(partialTitle)));
    }

    @Nonnull
    @Override
    public List<PaintingDTO> findAllByArtistId(UUID artistId) {
        log.info("Find all paintings by artist id: {}", artistId);
        return xaTxTemplate.execute(() -> enrichAndConvertAllToDTO(paintingRepository.findAllByArtistId(artistId)));
    }

    @Nonnull
    @Override
    @Step("Find all paintings")
    public List<PaintingDTO> findAll() {
        log.info("Find all paintings");
        return xaTxTemplate.execute(() -> enrichAndConvertAllToDTO(paintingRepository.findAll()));

    }

    @Nonnull
    @Override
    @Step("Update painting with id: [{painting.id}]")
    public PaintingDTO update(PaintingDTO painting) {

        log.info("Update painting: {}", painting);

        return xaTxTemplate.execute(() -> {

            var paintingEntity = paintingRepository.update(PaintingMapper.updateFromDTO(
                    paintingRepository.findById(painting.getId())
                            .orElseThrow(() -> new PaintingNotFoundException(painting.getId())),
                    painting));

            var im = ImageMapper.fromBase64Image(PAINTING, paintingEntity.getId(), painting.getPhoto());
            var image = filesRepository.findByEntityTypeAndEntityId(PAINTING, painting.getId())
                    .map(oldIm -> filesRepository.update(im))
                    .orElseGet(() -> filesRepository.create(im)).getContent().getData();

            return PaintingMapper.toDTO(paintingEntity, image);

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
    private List<PaintingDTO> enrichAndConvertAllToDTO(List<PaintingEntity> paintings) {

        var paintingsIds = paintings.stream()
                .map(PaintingEntity::getId)
                .toList();

        var paintingsImagesMap = findPaintingThumbnailImagesAndConvertToMap(paintingsIds);
        return paintings.stream()
                .map(painting -> PaintingMapper.toDTO(
                        painting,
                        paintingsImagesMap.get(painting.getId())))
                .toList();
    }

    @Nullable
    private byte[] findPaintingImage(UUID entityId) {
        return filesRepository.findByEntityTypeAndEntityId(EntityType.PAINTING, entityId)
                .map(im -> im.getContent().getData())
                .orElse(null);
    }

    @Nonnull
    private Map<UUID, byte[]> findPaintingThumbnailImagesAndConvertToMap(List<UUID> museumsIds) {
        return filesRepository.findAllByEntityTypeAndEntityIds(EntityType.PAINTING, museumsIds).stream()
                .collect(Collectors.toMap(
                        ImageMetadataEntity::getEntityId,
                        im -> im.getContent().getThumbnailData()));
    }

}
