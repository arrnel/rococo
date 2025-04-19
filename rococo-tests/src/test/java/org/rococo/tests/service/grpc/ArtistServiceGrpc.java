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

@Slf4j
@ParametersAreNonnullByDefault
public class ArtistServiceGrpc implements ArtistService {

    private final ArtistsGrpcClient artistClient = new ArtistsGrpcClient();

    @Nonnull
    @Override
    @Step("Add new artist: [{artist.name}]")
    public ArtistDTO add(ArtistDTO artist) {
        log.info("Add new artist: {}", artist);
        return artistClient.add(artist);
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
        return artistClient.findByName(name);
    }

    @Nonnull
    @Override
    @Step("Find all artists by partial name: [{partialName}]")
    public List<ArtistDTO> findAllByPartialName(String partialName) {
        log.info("Find all artists by partial name: {}", partialName);
        return findAllArtists(partialName);
    }

    @Nonnull
    @Override
    @Step("Find all artists")
    public List<ArtistDTO> findAll() {
        log.info("Find all artists");
        return findAllArtists(null);
    }

    @Nonnull
    @Override
    @Step("Update artist with id: [{artist.id}]")
    public ArtistDTO update(ArtistDTO artist) {
        log.info("Update artist: {}", artist);
        return artistClient.update(artist);

    }

    @Override
    @Step("Delete artist with id: [{id}]")
    public void delete(UUID id) {
        log.info("Delete artist with id: {}", id);
        artistClient.delete(id);
    }

    @Override
    @Step("Clear table \"rococo-artists\" and remove all files with entity_type ARTIST from \"rococo-files\"")
    public void clearAll() {
        log.info("Truncate table \"rococo-artists\" and remove all files with entity_type ARTIST from \"rococo-files\"");
        findAllArtists(null).stream()
                .map(ArtistDTO::getId)
                .forEach(this::delete);
    }

    @Nonnull
    private List<ArtistDTO> findAllArtists(@Nullable String name) {
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
            Page<ArtistDTO> page = artistClient.findAll(name, pageable);
            allArtists.addAll(page.getContent());
            if (!page.hasNext()) break;
            pageable = page.nextPageable();
        }

        return allArtists;
    }

}
