package org.rococo.gateway.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.rococo.gateway.client.CountriesGrpcClient;
import org.rococo.gateway.client.FilesGrpcClient;
import org.rococo.gateway.client.MuseumsGrpcClient;
import org.rococo.gateway.ex.MuseumNotFoundException;
import org.rococo.gateway.model.museums.MuseumDTO;
import org.rococo.gateway.service.ValidationService;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

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
        return museum;
    }

    @DeleteMapping
    public void delete(@ModelAttribute("museum") MuseumDTO museum) {
        log.info("Delete museum by id: {}", museum.getId());
        museumsClient.delete(museum.getId());
    }

}
