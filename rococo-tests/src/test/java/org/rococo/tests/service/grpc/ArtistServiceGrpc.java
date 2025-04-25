package org.rococo.tests.service.grpc;

import io.qameta.allure.Step;
import lombok.extern.slf4j.Slf4j;
import org.rococo.tests.client.grpc.ArtistsGrpcClient;
import org.rococo.tests.model.ArtistDTO;
import org.rococo.tests.service.ArtistService;
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
public class ArtistServiceGrpc implements ArtistService {

    @Nonnull
    @Override
    @Step("Add new artist: [{artist.name}]")
    public ArtistDTO add(ArtistDTO artist) {
        log.info("Add new artist: {}", artist);
        return withClient(artistsClient ->
                artistsClient.add(artist));
    }

    @Nonnull
    @Override
    @Step("Find artist by id: [{id}]")
    public Optional<ArtistDTO> findById(UUID id) {
        log.info("Find artist with id: {}", id);
        return withClient(artistsClient ->
                artistsClient.findById(id));
    }

    @Nonnull
    @Override
    @Step("Find artist by name: [{name}]")
    public Optional<ArtistDTO> findByName(String name) {
        log.info("Find artist with name: {}", name);
        return withClient(artistsClient ->
                artistsClient.findByName(name));
    }

    @Nonnull
    @Override
    @Step("Find all artists by partial name: [{partialName}]")
    public List<ArtistDTO> findAllByPartialName(String partialName) {
        log.info("Find all artists by partial name: {}", partialName);
        return withClient(artistsClient ->
                findAllArtists(artistsClient, partialName));
    }

    @Nonnull
    @Override
    @Step("Find all artists")
    public List<ArtistDTO> findAll() {
        log.info("Find all artists");
        return withClient(artistsClient ->
                findAllArtists(artistsClient, null));
    }

    @Nonnull
    @Override
    @Step("Update artist with id: [{artist.id}]")
    public ArtistDTO update(ArtistDTO artist) {
        log.info("Update artist: {}", artist);
        return withClient(artistsClient ->
                artistsClient.update(artist));
    }

    @Override
    @Step("Delete artist with id: [{id}]")
    public void delete(UUID id) {
        log.info("Delete artist with id: {}", id);
        withClient(artistsClient -> {
            artistsClient.delete(id);
            return null;
        });
    }

    @Override
    @Step("Clear table \"rococo-artists\" and remove all files with entity_type ARTIST from \"rococo-files\"")
    public void clearAll() {
        log.info("Truncate table \"rococo-artists\" and remove all files with entity_type ARTIST from \"rococo-files\"");
        withClient(artistsClient -> {
            findAllArtists(artistsClient, null).stream()
                    .map(ArtistDTO::getId)
                    .forEach(artistsClient::delete);
            return null;
        });
    }

    @Nonnull
    private List<ArtistDTO> findAllArtists(ArtistsGrpcClient artistsClient, @Nullable String name) {
        // DON'T remove sort. Help to get all artists in parallel test execution
        Pageable pageable = PageRequest.of(
                0,
                10,
                Sort.by(
                        Sort.Order.asc("createdDate"),
                        Sort.Order.asc("id")
                ));
        List<ArtistDTO> allArtists = new ArrayList<>();
        while (true) {
            Page<ArtistDTO> page = artistsClient.findAll(name, pageable);
            allArtists.addAll(page.getContent());
            if (!page.hasNext()) break;
            pageable = pageable.next();
        }
        return allArtists;
    }

    private <T> T withClient(Function<ArtistsGrpcClient, T> operation) {
        try (ArtistsGrpcClient client = new ArtistsGrpcClient()) {
            return operation.apply(client);
        }
    }

}
