package org.rococo.gateway.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.rococo.gateway.client.CountriesGrpcClient;
import org.rococo.gateway.client.FilesGrpcClient;
import org.rococo.gateway.client.MuseumsGrpcClient;
import org.rococo.gateway.ex.CountryNotFoundException;
import org.rococo.gateway.ex.MuseumNotFoundException;
import org.rococo.gateway.mapper.MuseumMapper;
import org.rococo.gateway.model.countries.CountryDTO;
import org.rococo.gateway.model.countries.LocationResponseDTO;
import org.rococo.gateway.model.files.ImageDTO;
import org.rococo.gateway.model.museums.AddMuseumRequestDTO;
import org.rococo.gateway.model.museums.MuseumDTO;
import org.rococo.gateway.model.museums.UpdateMuseumRequestDTO;
import org.rococo.gateway.service.ValidationService;
import org.rococo.gateway.util.PageableUtil;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.rococo.gateway.model.EntityType.MUSEUM;
import static org.springframework.data.domain.Sort.Direction.ASC;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping({"/api/museum", "/api/museum/"})
public class MuseumsController {

    private final MuseumsGrpcClient museumsClient;
    private final CountriesGrpcClient countriesGrpcClient;
    private final FilesGrpcClient filesGrpcClient;
    private final ValidationService validationService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public MuseumDTO add(@RequestBody AddMuseumRequestDTO requestDTO,
                         BindingResult bindingResult) {

        log.info("Add new museum: {}", requestDTO);

        validationService.throwBadRequestExceptionIfErrorsExist(bindingResult);

        var country = countriesGrpcClient.findById(requestDTO.location().country().id())
                .orElseThrow(() -> new CountryNotFoundException(requestDTO.location().country().id()));
        var museum = museumsClient.add(requestDTO);
        filesGrpcClient.add(MUSEUM, museum.getId(), requestDTO.photo());
        return museum.setPhoto(requestDTO.photo())
                .setLocation(LocationResponseDTO.builder()
                        .city(museum.getLocation().city())
                        .country(country)
                        .build());
    }

    @GetMapping
    public Page<MuseumDTO> findAll(@RequestParam(name = "title", required = false) String name,
                                   @PageableDefault(size = 9, sort = {"title"}, direction = ASC) Pageable pageable,
                                   @RequestParam Map<String, String> requestParams
    ) {

        log.info("Find all museums by params: {}", PageableUtil.getLogText(pageable, requestParams));

        validationService.validateObject(
                MuseumMapper.toRequestParamObj(requestParams, pageable),
                "MuseumsFindAllParamsValidationObject");


        var museums = museumsClient.findAll(name, pageable);
        var imagesMap = filesGrpcClient.findAll(MUSEUM, museums.map(MuseumDTO::getId).toList()).stream()
                .collect(Collectors.toMap(ImageDTO::entityId, ImageDTO::content));
        var countriesId = museums.stream()
                .map(museum -> museum.getLocation().country().id())
                .distinct()
                .toList();

        Map<UUID, CountryDTO> countriesMap = countriesGrpcClient.findAllByIds(countriesId).stream()
                .collect(Collectors.toMap(CountryDTO::id, c -> c));

        museums.getContent()
                .forEach(museum -> museum
                        .setLocation(LocationResponseDTO.builder()
                                .city(museum.getLocation().city())
                                .country(countriesMap.get(museum.getLocation().country().id()))
                                .build())
                        .setPhoto(imagesMap.get(museum.getId())));

        return museums;

    }

    @PatchMapping
    public MuseumDTO update(@RequestBody UpdateMuseumRequestDTO requestDTO,
                            BindingResult bindingResult) {

        log.info("Update museum: {}", requestDTO);

        validationService.throwBadRequestExceptionIfErrorsExist(bindingResult);

        var museum = museumsClient.findById(requestDTO.id())
                .orElseThrow(() -> new MuseumNotFoundException(requestDTO.id()));
        var country = countriesGrpcClient.findById(requestDTO.location().country().id())
                .orElseThrow(() -> new CountryNotFoundException(requestDTO.location().country().id()));

        // If photo exists in request -> Update photo if exists in rococo-files service, else add new photo
        // If photo not exists in request -> remove photo from rococo-files service
        Optional.ofNullable(requestDTO.photo())
                .ifPresentOrElse(
                        photo -> filesGrpcClient.findImage(MUSEUM, museum.getId())
                                .ifPresentOrElse(image -> filesGrpcClient.update(MUSEUM, museum.getId(), photo),
                                        () -> filesGrpcClient.add(MUSEUM, museum.getId(), photo)),
                        () -> filesGrpcClient.delete(MUSEUM, museum.getId()));

        return museumsClient.update(requestDTO)
                .setPhoto(requestDTO.photo())
                .setLocation(LocationResponseDTO.builder()
                        .city(museum.getLocation().city())
                        .country(country)
                        .build());
    }


}
