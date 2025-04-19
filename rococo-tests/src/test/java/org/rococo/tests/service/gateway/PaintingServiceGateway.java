package org.rococo.tests.service.gateway;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.rococo.tests.client.gateway.PaintingsApiClient;
import org.rococo.tests.ex.TokenIsEmptyException;
import org.rococo.tests.model.PaintingDTO;
import org.rococo.tests.model.Token;
import org.rococo.tests.service.PaintingService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@AllArgsConstructor
@ParametersAreNonnullByDefault
public class PaintingServiceGateway implements PaintingService {

    private final PaintingsApiClient paintingClient = new PaintingsApiClient();

    @Getter
    @Setter
    private Token token;

    @Nonnull
    @Override
    public PaintingDTO add(PaintingDTO painting) {
        log.info("Add new painting: {}", painting);
        checkToken();
        return paintingClient.add(token.token(), painting);
    }

    @Nonnull
    @Override
    public Optional<PaintingDTO> findById(UUID id) {
        log.info("Find painting by id: {}", id);
        return paintingClient.findById(id);
    }

    @Nonnull
    @Override
    public Optional<PaintingDTO> findByTitle(String title) {
        log.info("Find painting by title: {}", title);
        return findPaintings(title).stream()
                .filter(painting -> painting.getTitle().equals(title))
                .findFirst();
    }

    @Nonnull
    @Override
    public List<PaintingDTO> findAllByPartialTitle(String partialTitle) {
        log.info("Find all paintings by partial title: {}", partialTitle);
        return findPaintings(partialTitle);
    }

    @Nonnull
    @Override
    public List<PaintingDTO> findAllByArtistId(UUID artistId) {
        log.info("Find all paintings with artist id: {}", artistId);
        return findPaintings(artistId);
    }

    @Nonnull
    @Override
    public List<PaintingDTO> findAll() {
        log.info("Find all paintings");
        return findPaintings(null);
    }

    @Nonnull
    @Override
    public PaintingDTO update(PaintingDTO painting) {
        log.info("Update painting: {}", painting);
        checkToken();
        return paintingClient.update(token.token(), painting);
    }

    @Override
    public void delete(UUID id) {
        log.info("Delete painting with id: {}", id);
        checkToken();
        paintingClient.delete(token.token(), id);
    }

    @Override
    public void clearAll() {
        log.info("Clear all museums");
        checkToken();
        findPaintings(null)
                .forEach(museum -> paintingClient.delete(token.token(), museum.getId()));
    }

    private void checkToken() {
        if (token == null || token.token() == null || token.token().isEmpty())
            throw new TokenIsEmptyException();
    }

    private List<PaintingDTO> findPaintings(@Nullable Object type) {

        var isId = type instanceof UUID;
        var isName = type == null || type instanceof String;

        if (!isName && !isId) throw new IllegalArgumentException("type should be instance of String or UUID");

        List<PaintingDTO> allPaintings = new ArrayList<>();
        Pageable pageable = PageRequest.of(0, 10);

        while (true) {
            Page<PaintingDTO> page = isName
                    ? paintingClient.findAll((String) type, pageable.getPageNumber(), pageable.getPageSize())
                    : paintingClient.findAll((UUID) type, pageable.getPageNumber(), pageable.getPageSize());
            allPaintings.addAll(page.getContent());
            if (!page.hasNext()) break;
            pageable = page.nextPageable();
        }

        return allPaintings;
    }

}
