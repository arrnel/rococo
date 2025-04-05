package org.rococo.tests.service.gateway;

import io.qameta.allure.Step;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.rococo.tests.client.gateway.ArtistsApiClient;
import org.rococo.tests.ex.TokenIsEmptyException;
import org.rococo.tests.model.ArtistDTO;
import org.rococo.tests.model.Token;
import org.rococo.tests.service.ArtistService;
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
public class ArtistServiceGateway implements ArtistService {

    private final ArtistsApiClient artistClient = new ArtistsApiClient();

    @Getter
    @Setter
    private Token token;

    @Nonnull
    @Override
    @Step("Add new artist: [{artist.name}]")
    public ArtistDTO add(ArtistDTO artist) {
        log.info("Add new artist: {}", artist);
        checkToken();
        return artistClient.add(token.token(), artist);
    }

    @Nonnull
    @Override
    @Step("Find artist by id: [{id}]")
    public Optional<ArtistDTO> findById(UUID id) {
        log.info("Find artist with id: {}", id);
        return artistClient.findById(id);
    }

    @Nonnull
    @Override
    @Step("Find artist by name: [{name}]")
    public Optional<ArtistDTO> findByName(String name) {
        log.info("Find artist with name: {}", name);
        return findArtists(name).stream()
                .filter(a -> a.getName().equals(name))
                .findFirst();
    }

    @Nonnull
    @Override
    @Step("Find all artists")
    public List<ArtistDTO> findAllByPartialName(String partialName) {
        log.info("Find all artists by partial name: {}", partialName);
        return findArtists(partialName);
    }

    @Nonnull
    @Override
    @Step("Find all artists")
    public List<ArtistDTO> findAll() {
        log.info("Find all artists");
        return findArtists(null);
    }

    @Nonnull
    @Override
    @Step("Update artist with id: [{artist.id}]")
    public ArtistDTO update(ArtistDTO artist) {
        log.info("Update artist: {}", artist);
        checkToken();
        return artistClient.update(token.token(), artist);
    }

    @Override
    @Step("Delete artist with id: [{id}]")
    public void delete(UUID id) {
        log.info("Delete artist with id: {}", id);
        checkToken();
        if (token == null) throw new TokenIsEmptyException();
        artistClient.delete(token.token(), id);
    }

    @Override
    @Step("Clear table \"rococo-artists\" and remove all files with entity_type ARTIST from \"rococo-files\"")
    public void clearAll() {
        log.info("Truncate table \"rococo-artists\" and remove all files with entity_type ARTIST from \"rococo-files\"");
        checkToken();
        findArtists(null)
                .forEach(a -> artistClient.delete(token.token(), a.getId()));
    }

    private void checkToken() {
        if (token == null || token.token() == null || token.token().isEmpty())
            throw new TokenIsEmptyException();
    }

    private List<ArtistDTO> findArtists(@Nullable String name) {
        List<ArtistDTO> allArtists = new ArrayList<>();
        Pageable pageable = PageRequest.of(0, 10);

        while (true) {
            Page<ArtistDTO> page = artistClient.findAll(name, pageable.getPageNumber(), pageable.getPageSize());
            allArtists.addAll(page.getContent());
            if (!page.hasNext()) break;
            pageable = page.nextPageable();
        }

        return allArtists;
    }

}
