package org.rococo.gateway.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.rococo.gateway.client.ArtistsGrpcClient;
import org.rococo.gateway.client.FilesGrpcClient;
import org.rococo.gateway.ex.ArtistNotFoundException;
import org.rococo.gateway.model.artists.ArtistDTO;
import org.rococo.gateway.model.artists.ArtistIdDTO;
import org.rococo.gateway.model.files.ImageDTO;
import org.rococo.gateway.service.ValidationService;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

import static org.rococo.gateway.model.EntityType.ARTIST;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping({"/api/artist/{id}", "/api/artist/{id}/"})
public class ArtistController {

    private final ArtistsGrpcClient artistsClient;
    private final FilesGrpcClient filesGrpcClient;
    private final ValidationService validationService;

    @ModelAttribute(name = "artist", binding = false)
    public ArtistDTO artist(@PathVariable("id") UUID id) {

        var artist = artistsClient.findById(id)
                .orElseThrow(() -> new ArtistNotFoundException(id));

        var image = filesGrpcClient.findImage(ARTIST, artist.getId())
                .orElse(ImageDTO.empty());

        return artist.setPhoto(image.content());
    }

    @GetMapping
    public ArtistDTO findById(@ModelAttribute("artist") ArtistDTO artist) {
        log.info("Find artist by id: {}", artist.getId());
        filesGrpcClient.findImage(ARTIST, artist.getId())
                .ifPresent(image-> artist.setPhoto(image.content()));
        return artist;
    }

    @DeleteMapping
    public void delete(@ModelAttribute("artist") ArtistDTO artist) {
        log.info("Delete artist: {}", artist);
        filesGrpcClient.delete(ARTIST, artist.getId());
        artistsClient.delete(artist.getId());
    }

}
