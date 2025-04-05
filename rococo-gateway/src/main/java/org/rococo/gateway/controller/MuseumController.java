package org.rococo.gateway.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.rococo.gateway.client.CountriesGrpcClient;
import org.rococo.gateway.client.FilesGrpcClient;
import org.rococo.gateway.client.MuseumsGrpcClient;
import org.rococo.gateway.ex.ArtistNotFoundException;
import org.rococo.gateway.ex.MuseumNotFoundException;
import org.rococo.gateway.model.countries.LocationResponseDTO;
import org.rococo.gateway.model.files.ImageDTO;
import org.rococo.gateway.model.museums.MuseumDTO;
import org.rococo.gateway.service.ValidationService;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

import static org.rococo.gateway.model.EntityType.MUSEUM;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping({"/api/museum/{id}", "/api/museum/{id}/"})
public class MuseumController {

    private final MuseumsGrpcClient museumsClient;
    private final FilesGrpcClient filesGrpcClient;
    private final CountriesGrpcClient countriesGrpcClient;
    private final ValidationService validationService;

    @ModelAttribute(name = "museum", binding = false)
    public MuseumDTO museum(@PathVariable UUID id) {
        return museumsClient.findById(id)
                .orElseThrow(() -> new MuseumNotFoundException(id));
    }

    @GetMapping
    public MuseumDTO findById(@ModelAttribute("museum") MuseumDTO museum) {
        log.info("Find museum by id: {}", museum.getId());

        var imageContent = filesGrpcClient.findImage(MUSEUM, museum.getId())
                .map(ImageDTO::content)
                .orElse(null);

        var country = countriesGrpcClient.findById(museum.getLocation().country().id())
                .orElse(null);

        return museum.setPhoto(imageContent)
                .setLocation(LocationResponseDTO.builder()
                        .city(museum.getLocation().city())
                        .country(country)
                        .build());

    }

    @DeleteMapping
    public void delete(@ModelAttribute("museum") MuseumDTO museum) {
        log.info("Delete museum by id: {}", museum.getId());
        filesGrpcClient.delete(MUSEUM, museum.getId());
        museumsClient.delete(museum.getId());
    }

}
