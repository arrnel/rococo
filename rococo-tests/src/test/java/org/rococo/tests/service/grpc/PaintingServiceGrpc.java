package org.rococo.tests.service.grpc;

import lombok.extern.slf4j.Slf4j;
import org.rococo.tests.client.grpc.PaintingsGrpcClient;
import org.rococo.tests.model.PaintingDTO;
import org.rococo.tests.service.PaintingService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;

@Slf4j
@ParametersAreNonnullByDefault
public class PaintingServiceGrpc implements PaintingService {

    @Nonnull
    @Override
    public PaintingDTO add(PaintingDTO painting) {
        log.info("Add new painting: {}", painting);
        return withClient(paintingsClient ->
                paintingsClient.add(painting));
    }

    @Nonnull
    @Override
    public Optional<PaintingDTO> findById(UUID id) {
        log.info("Find painting with id: {}", id);
        return withClient(paintingsClient ->
                paintingsClient.findById(id));

    }

    @Nonnull
    @Override
    public Optional<PaintingDTO> findByTitle(String title) {
        log.info("Find painting with title: {}", title);
        return withClient(paintingsClient ->
                paintingsClient.findByTitle(title));
    }

    @Nonnull
    @Override
    public List<PaintingDTO> findAllByPartialTitle(String partialTitle) {
        log.info("Find all paintings by partial title: {}", partialTitle);
        return withClient(paintingsClient ->
                findAllPaintings(paintingsClient, partialTitle, null));
    }

    @Nonnull
    @Override
    public List<PaintingDTO> findAllByArtistId(UUID artistId) {
        log.info("Find all paintings by artist id: {}", artistId);
        return withClient(paintingsClient ->
                findAllPaintings(paintingsClient, null, artistId));
    }

    @Nonnull
    @Override
    public List<PaintingDTO> findAll() {
        log.info("Find all paintings");
        return withClient(paintingsClient ->
                findAllPaintings(paintingsClient, null, null));
    }

    @Nonnull
    @Override
    public PaintingDTO update(PaintingDTO painting) {
        log.info("Update painting: {}", painting);
        return withClient(paintingsClient ->
                paintingsClient.update(painting));
    }

    @Override
    public void delete(UUID id) {
        log.info("Delete painting with id: {}", id);
        withClient(paintingsClient -> {
            paintingsClient.delete(id);
            return null;
        });
    }

    @Override
    public void clearAll() {
        log.info("Truncate table \"rococo-paintings\" and remove all files with entity_type PAINTING from \"rococo-files\"");
        withClient(paintingsClient -> {
            findAllPaintings(paintingsClient, null, null).stream()
                    .map(PaintingDTO::getId)
                    .forEach(paintingsClient::delete);
            return null;
        });
    }

    private List<PaintingDTO> findAllPaintings(PaintingsGrpcClient paintingsClient, @Nullable String title, @Nullable UUID artistId) {
        // DON'T remove sort. Help to get all paintings in parallel test execution
        Pageable pageable = PageRequest.of(
                0,
                10, Sort.by(
                        Sort.Order.asc("createdDate"),
                        Sort.Order.asc("id")));
        List<PaintingDTO> allPaintings = new ArrayList<>();
        while (true) {
            Page<PaintingDTO> page = paintingsClient.findAll(title, artistId, pageable);
            allPaintings.addAll(page.getContent());
            if (!page.hasNext()) break;
            pageable = pageable.next();
        }
        return allPaintings;
    }

    private <T> T withClient(Function<PaintingsGrpcClient, T> operation) {
        try (PaintingsGrpcClient client = new PaintingsGrpcClient()) {
            return operation.apply(client);
        }
    }

}
