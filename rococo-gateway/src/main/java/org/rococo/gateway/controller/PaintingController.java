package org.rococo.gateway.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.rococo.gateway.client.ArtistsGrpcClient;
import org.rococo.gateway.client.FilesGrpcClient;
import org.rococo.gateway.client.MuseumsGrpcClient;
import org.rococo.gateway.client.PaintingsGrpcClient;
import org.rococo.gateway.ex.PaintingNotFoundException;
import org.rococo.gateway.model.paintings.PaintingDTO;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

import static org.rococo.gateway.model.EntityType.PAINTING;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping({"/api/painting/{id}", "/api/painting/{id}/"})
public class PaintingController {

    private final PaintingsGrpcClient paintingsClient;
    private final ArtistsGrpcClient artistsClient;
    private final MuseumsGrpcClient museumsClient;
    private final FilesGrpcClient filesGrpcClient;

    @ModelAttribute(name = "painting")
    public PaintingDTO painting(@PathVariable("id") UUID id) {
        return paintingsClient.findById(id)
                .orElseThrow(() -> new PaintingNotFoundException(id));
    }

    @GetMapping
    public PaintingDTO findById(@ModelAttribute("painting") PaintingDTO painting) {
        log.info("Find painting by id: {}", painting.getId());
        filesGrpcClient.findImage(PAINTING, painting.getId())
                .ifPresent(image -> painting.setPhoto(image.content()));
        artistsClient.findById(painting.getArtist().getId())
                .ifPresent(painting::setArtist);
        museumsClient.findById(painting.getMuseum().getId())
                .ifPresent(painting::setMuseum);

        return painting;
    }


    @DeleteMapping
    public void delete(@ModelAttribute("painting") PaintingDTO painting) {
        log.info("Delete painting by id: {}", painting.getId());
        filesGrpcClient.delete(PAINTING, painting.getId());
        paintingsClient.delete(painting.getId());
    }

}
