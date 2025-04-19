package org.rococo.gateway.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.rococo.gateway.client.MuseumsGrpcClient;
import org.rococo.gateway.mapper.MuseumMapper;
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

import static org.springframework.data.domain.Sort.Direction.ASC;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping({"/api/museum", "/api/museum/"})
public class MuseumsController {

    private final MuseumsGrpcClient museumsClient;
    private final ValidationService validationService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public MuseumDTO add(@Valid @RequestBody AddMuseumRequestDTO requestDTO,
                         BindingResult bindingResult
    ) {
        log.info("Add new museum: {}", requestDTO);
        validationService.throwBadRequestExceptionIfErrorsExist(bindingResult);
        return museumsClient.add(requestDTO);
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

        return museumsClient.findAll(name, false, pageable);
    }

    @PatchMapping
    public MuseumDTO update(@Valid @RequestBody UpdateMuseumRequestDTO requestDTO,
                            BindingResult bindingResult
    ) {
        log.info("Update museum: {}", requestDTO);
        validationService.throwBadRequestExceptionIfErrorsExist(bindingResult);
        return museumsClient.update(requestDTO);
    }

}
