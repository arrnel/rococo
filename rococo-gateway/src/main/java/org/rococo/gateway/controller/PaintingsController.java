package org.rococo.gateway.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.rococo.gateway.client.ArtistsGrpcClient;
import org.rococo.gateway.client.FilesGrpcClient;
import org.rococo.gateway.client.MuseumsGrpcClient;
import org.rococo.gateway.client.PaintingsGrpcClient;
import org.rococo.gateway.ex.ArtistNotFoundException;
import org.rococo.gateway.ex.MuseumNotFoundException;
import org.rococo.gateway.ex.PaintingNotFoundException;
import org.rococo.gateway.mapper.PaintingMapper;
import org.rococo.gateway.model.files.ImageDTO;
import org.rococo.gateway.model.paintings.AddPaintingRequestDTO;
import org.rococo.gateway.model.paintings.PaintingDTO;
import org.rococo.gateway.model.paintings.UpdatePaintingRequestDTO;
import org.rococo.gateway.service.ValidationService;
import org.rococo.gateway.util.PageableUtil;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.rococo.gateway.model.EntityType.PAINTING;
import static org.springframework.data.domain.Sort.Direction.ASC;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping({"/api/painting", "/api/painting/"})
public class PaintingsController {

    private final PaintingsGrpcClient paintingsClient;
    private final ArtistsGrpcClient artistsClient;
    private final MuseumsGrpcClient museumsClient;
    private final FilesGrpcClient filesGrpcClient;
    private final ValidationService validationService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PaintingDTO add(@RequestBody AddPaintingRequestDTO requestDTO,
                           BindingResult bindingResult
    ) {

        log.info("Add new painting: {}", requestDTO);

        validationService.throwBadRequestExceptionIfErrorsExist(bindingResult);

        var artist = artistsClient.findById(requestDTO.artist().id())
                .orElseThrow(() -> new ArtistNotFoundException(requestDTO.artist().id()));

        var museum = museumsClient.findById(requestDTO.museum().id())
                .orElseThrow(() -> new MuseumNotFoundException(requestDTO.museum().id()));

        var painting = paintingsClient.add(requestDTO);
        filesGrpcClient.add(PAINTING, painting.getId(), requestDTO.photo());
        return painting.setPhoto(requestDTO.photo())
                .setArtist(artist)
                .setMuseum(museum);

    }

    @GetMapping
    public Page<PaintingDTO> findAll(@RequestParam(name = "title", required = false) String title,
                                     @RequestParam(value = "authorId", required = false) UUID artistId,
                                     @PageableDefault(size = 9, sort = "title", direction = ASC) Pageable pageable,
                                     @RequestParam Map<String, String> requestParams
    ) {

        log.info("Find all paintings by params: {}", PageableUtil.getLogText(pageable, requestParams));

        validationService.validateObject(PaintingMapper.toRequestParamObj(requestParams, pageable),
                "PaintingsFindAllParamsValidationObject");

        var paintings = paintingsClient.findAll(title, artistId, pageable);
        var paintingsIds = paintings.map(PaintingDTO::getId).toList();
        var imagesMap = filesGrpcClient.findAll(PAINTING, paintingsIds).stream()
                .collect(Collectors.toMap(ImageDTO::entityId, ImageDTO::content));

        paintings.getContent()
                .forEach(painting -> painting
                        .setPhoto(imagesMap.get(painting.getId())));

        return paintings;
    }

    @PatchMapping
    public PaintingDTO update(@RequestBody UpdatePaintingRequestDTO requestDTO,
                              BindingResult bindingResult
    ) {

        log.info("Update painting: {}", requestDTO);

        validationService.throwBadRequestExceptionIfErrorsExist(bindingResult);

        var painting = paintingsClient.findById(requestDTO.id())
                .orElseThrow(() -> new PaintingNotFoundException(requestDTO.id()));

        // If photo exists in request -> Update photo if exists in rococo-files service, else add new photo
        // If photo not exists in request -> remove photo from rococo-files service
        Optional.ofNullable(requestDTO.photo())
                .ifPresentOrElse(
                        photo -> filesGrpcClient.findImage(PAINTING, painting.getId())
                                .ifPresentOrElse(image -> filesGrpcClient.update(PAINTING, painting.getId(), photo),
                                        () -> filesGrpcClient.add(PAINTING, painting.getId(), photo)),
                        () -> filesGrpcClient.delete(PAINTING, painting.getId()));

        var newPainting = paintingsClient.update(requestDTO)
                .setPhoto(requestDTO.photo());
        artistsClient.findById(painting.getArtist().getId())
                .ifPresent(newPainting::setArtist);
        museumsClient.findById(painting.getMuseum().getId())
                .ifPresent(newPainting::setMuseum);

        return newPainting;

    }

}
