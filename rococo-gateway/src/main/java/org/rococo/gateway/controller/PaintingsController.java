package org.rococo.gateway.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.rococo.gateway.client.PaintingsGrpcClient;
import org.rococo.gateway.ex.PaintingNotFoundException;
import org.rococo.gateway.mapper.PaintingMapper;
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
import java.util.UUID;

import static org.springframework.data.domain.Sort.Direction.ASC;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping({"/api/painting", "/api/painting/"})
public class PaintingsController {

    private final PaintingsGrpcClient paintingsClient;
    private final ValidationService validationService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PaintingDTO add(@RequestBody AddPaintingRequestDTO requestDTO,
                           BindingResult bindingResult
    ) {
        log.info("Add new painting: {}", requestDTO);
        validationService.throwBadRequestExceptionIfErrorsExist(bindingResult);
        return paintingsClient.add(requestDTO);
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
        return paintingsClient.findAll(title, artistId, pageable);
    }

    @PatchMapping
    public PaintingDTO update(@RequestBody UpdatePaintingRequestDTO requestDTO,
                              BindingResult bindingResult
    ) {
        log.info("Update painting: {}", requestDTO);
        validationService.throwBadRequestExceptionIfErrorsExist(bindingResult);
        paintingsClient.findById(requestDTO.id())
                .orElseThrow(() -> new PaintingNotFoundException(requestDTO.id()));
        return paintingsClient.update(requestDTO);
    }

}
