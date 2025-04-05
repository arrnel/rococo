package org.rococo.tests.service.grpc;

import lombok.extern.slf4j.Slf4j;
import org.rococo.tests.client.grpc.FilesGrpcClient;
import org.rococo.tests.client.grpc.PaintingsGrpcClient;
import org.rococo.tests.model.ImageDTO;
import org.rococo.tests.model.PaintingDTO;
import org.rococo.tests.service.PaintingService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.*;
import java.util.stream.Collectors;

import static org.rococo.tests.enums.EntityType.PAINTING;

@Slf4j
@ParametersAreNonnullByDefault
public class PaintingServiceGrpc implements PaintingService {

    private final PaintingsGrpcClient paintingClient = new PaintingsGrpcClient();
    private final FilesGrpcClient filesClient = new FilesGrpcClient();

    @Nonnull
    @Override
    public PaintingDTO add(PaintingDTO painting) {
        log.info("Add new painting: {}", painting);
        var newArtist = paintingClient.add(painting);
        filesClient.addImage(PAINTING, newArtist.getId(), painting.getPhoto());
        return newArtist.setPhoto(painting.getPhoto());
    }

    @Nonnull
    @Override
    public Optional<PaintingDTO> findById(UUID id) {
        log.info("Find painting with id: {}", id);
        return paintingClient.findById(id)
                .map(painting -> painting.setPhoto(
                        filesClient.findImage(PAINTING, painting.getId())
                                .map(ImageDTO::getContent)
                                .orElse(null)));
    }

    @Nonnull
    @Override
    public Optional<PaintingDTO> findByTitle(String title) {
        log.info("Find painting with title: {}", title);
        return findAllPaintings(title, null).stream()
                .filter(painting -> painting.getTitle().equals(title))
                .findFirst()
                .map(painting -> painting.setPhoto(
                        filesClient.findImage(PAINTING, painting.getId())
                                .map(ImageDTO::getContent)
                                .orElse(null)));
    }

    @Nonnull
    @Override
    public List<PaintingDTO> findAllByPartialTitle(String partialTitle) {
        log.info("Find all paintings by partial title: {}", partialTitle);
        return enrichAll(findAllPaintings(partialTitle, null));
    }

    @Nonnull
    @Override
    public List<PaintingDTO> findAllByArtistId(UUID artistId) {
        log.info("Find all paintings by artist id: {}", artistId);
        return enrichAll(findAllPaintings(null, artistId));
    }

    @Nonnull
    @Override
    public List<PaintingDTO> findAll() {
        log.info("Find all paintings");
        return enrichAll(findAllPaintings(null, null));
    }

    @Nonnull
    @Override
    public PaintingDTO update(PaintingDTO painting) {
        log.info("Update painting: {}", painting);
        var updatedPainting = paintingClient.update(painting);
        filesClient.findImage(PAINTING, painting.getId())
                .ifPresentOrElse(
                        image -> filesClient.update(PAINTING, painting.getId(), painting.getPhoto()),
                        () -> filesClient.addImage(PAINTING, painting.getId(), painting.getPhoto()));
        return updatedPainting.setPhoto(painting.getPhoto());
    }

    @Override
    public void delete(UUID id) {
        log.info("Delete painting with id: {}", id);
        paintingClient.delete(id);
        filesClient.delete(PAINTING, id);
    }

    @Override
    public void clearAll() {
        log.info("Truncate table \"rococo-paintings\" and remove all files with entity_type PAINTING from \"rococo-files\"");
        findAllPaintings(null, null).stream()
                .map(PaintingDTO::getId)
                .forEach(this::delete);
    }

    private List<PaintingDTO> findAllPaintings(@Nullable String title, @Nullable UUID artistId) {
        List<PaintingDTO> allPaintings = new ArrayList<>();
        Pageable pageable = PageRequest.of(0, 10);

        while (true) {
            Page<PaintingDTO> paintings = paintingClient.findAll(title, artistId, pageable);
            allPaintings.addAll(paintings.getContent());
            if (!paintings.hasNext()) break;
            pageable = pageable.next();
        }
        return allPaintings;
    }

    @Nonnull
    private List<PaintingDTO> enrichAll(List<PaintingDTO> paintings) {

        var paintingIds = paintings.stream()
                .map(PaintingDTO::getId)
                .toList();

        Map<UUID, String> paintingBase64ImageMap = filesClient.findAll(PAINTING, paintingIds).stream()
                .collect(Collectors.toMap(
                        ImageDTO::getEntityId,
                        ImageDTO::getContent));

        return paintings.stream()
                .map(painting -> painting.setPhoto(paintingBase64ImageMap.get(painting.getId())))
                .toList();
    }

}
