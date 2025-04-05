package org.rococo.tests.service.grpc;

import io.qameta.allure.Step;
import lombok.extern.slf4j.Slf4j;
import org.rococo.tests.client.grpc.ArtistsGrpcClient;
import org.rococo.tests.client.grpc.FilesGrpcClient;
import org.rococo.tests.model.ArtistDTO;
import org.rococo.tests.model.ImageDTO;
import org.rococo.tests.service.ArtistService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.*;
import java.util.stream.Collectors;

import static org.rococo.tests.enums.EntityType.ARTIST;

@Slf4j
@ParametersAreNonnullByDefault
public class ArtistServiceGrpc implements ArtistService {

    private final ArtistsGrpcClient artistClient = new ArtistsGrpcClient();
    private final FilesGrpcClient filesClient = new FilesGrpcClient();

    @Nonnull
    @Override
    @Step("Add new artist: [{artist.name}]")
    public ArtistDTO add(ArtistDTO artist) {
        log.info("Add new artist: {}", artist);
        var newArtist = artistClient.add(artist);
        filesClient.addImage(ARTIST, newArtist.getId(), artist.getPhoto());
        return newArtist.setPhoto(artist.getPhoto());
    }

    @Nonnull
    @Override
    @Step("Find artist by id: [{id}]")
    public Optional<ArtistDTO> findById(UUID id) {
        log.info("Find artist with id: {}", id);
        return artistClient.findById(id)
                .map(artist -> artist.setPhoto(
                        filesClient.findImage(ARTIST, artist.getId())
                                .map(ImageDTO::getContent)
                                .orElse(null)));
    }

    @Nonnull
    @Override
    @Step("Find artist by name: [{name}]")
    public Optional<ArtistDTO> findByName(String name) {
        log.info("Find artist with name: {}", name);
        return artistClient.findByName(name)
                .map(artist -> artist.setPhoto(
                        filesClient.findImage(ARTIST, artist.getId())
                                .map(ImageDTO::getContent)
                                .orElse(null)));
    }

    @Nonnull
    @Override
    @Step("Find all artists by partial name: [{partialName}]")
    public List<ArtistDTO> findAllByPartialName(String partialName) {
        log.info("Find all artists by partial name: {}", partialName);
        return enrichAll(findAllArtists(partialName));
    }

    @Nonnull
    @Override
    @Step("Find all artists")
    public List<ArtistDTO> findAll() {
        log.info("Find all artists by names: names");
        return enrichAll(findAllArtists(null));
    }

    @Nonnull
    @Override
    @Step("Update artist with id: [{artist.id}]")
    public ArtistDTO update(ArtistDTO artist) {
        log.info("Update artist: {}", artist);
        var updatedArtist = artistClient.update(artist);
        filesClient.findImage(ARTIST, artist.getId())
                .ifPresentOrElse(
                        image -> filesClient.update(ARTIST, artist.getId(), artist.getPhoto()),
                        () -> filesClient.addImage(ARTIST, artist.getId(), artist.getPhoto()));

        return updatedArtist.setPhoto(artist.getPhoto());

    }

    @Override
    @Step("Delete artist with id: [{id}]")
    public void delete(UUID id) {
        log.info("Delete artist with id: {}", id);
        artistClient.delete(id);
        filesClient.delete(ARTIST, id);
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
        List<ArtistDTO> allArtists = new ArrayList<>();
        Pageable pageable = PageRequest.of(0, 10);

        while (true) {
            Page<ArtistDTO> page = artistClient.findAll(name, pageable);
            allArtists.addAll(page.getContent());
            if (!page.hasNext()) break;
            pageable = page.nextPageable();
        }

        return allArtists;
    }

    @Nonnull
    private List<ArtistDTO> enrichAll(List<ArtistDTO> artists) {
        var artistIds = artists.stream().map(ArtistDTO::getId).toList();

        Map<UUID, String> artistBase64ImageMap = filesClient.findAll(ARTIST, artistIds).stream()
                .collect(Collectors.toMap(
                        ImageDTO::getEntityId,
                        ImageDTO::getContent));

        return artists.stream()
                .map(artist -> artist.setPhoto(artistBase64ImageMap.get(artist.getId())))
                .toList();
    }

}
