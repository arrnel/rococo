package org.rococo.gateway.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.rococo.gateway.client.ArtistsGrpcClient;
import org.rococo.gateway.ex.ArtistNotFoundException;
import org.rococo.gateway.mapper.ArtistMapper;
import org.rococo.gateway.model.artists.AddArtistRequestDTO;
import org.rococo.gateway.model.artists.ArtistDTO;
import org.rococo.gateway.model.artists.UpdateArtistRequestDTO;
import org.rococo.gateway.service.ValidationService;
import org.rococo.gateway.util.PageableUtil;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

import static org.springframework.data.domain.Sort.Direction.ASC;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping({"/api/artist", "/api/artist/"})

public class ArtistsController {

    private final ArtistsGrpcClient artistsClient;
    private final ValidationService validationService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ArtistDTO add(@Valid @RequestBody AddArtistRequestDTO requestDTO,
                         BindingResult bindingResult
    ) {
        log.info("Add new artist: {}", requestDTO);
        validationService.throwBadRequestExceptionIfErrorsExist(bindingResult);
        return artistsClient.add(requestDTO);
    }

    @GetMapping
    public Page<ArtistDTO> findAll(@RequestParam(name = "name", required = false) String name,
                                   @PageableDefault(size = 20, sort = {"name"}, direction = ASC) Pageable pageable,
                                   @RequestParam Map<String, String> requestParams
    ) {

        log.info("Find all artists by params: {}", PageableUtil.getLogText(pageable, requestParams));

        validationService.validateObject(
                ArtistMapper.toRequestParamObj(requestParams, pageable),
                "ArtistsFindAllParamsValidationObject");

        return artistsClient.findAll(name, false, pageable);
    }

    @PatchMapping
    public ArtistDTO update(@Valid @RequestBody UpdateArtistRequestDTO requestDTO,
                            BindingResult bindingResult) {
        log.info("Update artist: {}", requestDTO);

        validationService.throwBadRequestExceptionIfErrorsExist(bindingResult);

        artistsClient.findById(requestDTO.id())
                .orElseThrow(() -> new ArtistNotFoundException(requestDTO.id()));
        return artistsClient.update(requestDTO);
    }

}
