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
        return paintingClient.findById(id);
    }

    @Nonnull
    @Override
    public Optional<PaintingDTO> findByTitle(String title) {
        log.info("Find painting with title: {}", title);
        return paintingClient.findByTitle(title);
    }

    @Nonnull
    @Override
    public List<PaintingDTO> findAllByPartialTitle(String partialTitle) {
        log.info("Find all paintings by partial title: {}", partialTitle);
        return findAllPaintings(partialTitle, null);
    }

    @Nonnull
    @Override
    public List<PaintingDTO> findAllByArtistId(UUID artistId) {
        log.info("Find all paintings by artist id: {}", artistId);
        return findAllPaintings(null, artistId);
    }

    @Nonnull
    @Override
    public List<PaintingDTO> findAll() {
        log.info("Find all paintings");
        return findAllPaintings(null, null);
    }

    @Nonnull
    @Override
    public PaintingDTO update(PaintingDTO painting) {
        log.info("Update painting: {}", painting);
        return paintingClient.update(painting);
    }

    @Override
    public void delete(UUID id) {
        log.info("Delete painting with id: {}", id);
        paintingClient.delete(id);
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

}
