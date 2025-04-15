package org.rococo.gateway.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.rococo.gateway.client.ArtistsGrpcClient;
import org.rococo.gateway.ex.ArtistNotFoundException;
import org.rococo.gateway.model.artists.ArtistDTO;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping({"/api/artist/{id}", "/api/artist/{id}/"})
public class ArtistController {

    private final ArtistsGrpcClient artistsClient;

    @ModelAttribute(name = "artist", binding = false)
    public ArtistDTO artist(@PathVariable("id") UUID id) {
        return artistsClient.findById(id)
                .orElseThrow(() -> new ArtistNotFoundException(id));
    }

    @GetMapping
    public ArtistDTO findById(@ModelAttribute("artist") ArtistDTO artist) {
        log.info("Find artist by id: {}", artist.getId());
        return artist;
    }

    @DeleteMapping
    public void delete(@ModelAttribute("artist") ArtistDTO artist) {
        log.info("Delete artist: {}", artist);
        artistsClient.delete(artist.getId());
    }

}
