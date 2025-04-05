package org.rococo.gateway.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.rococo.gateway.client.CountriesGrpcClient;
import org.rococo.gateway.ex.CountryNotFoundException;
import org.rococo.gateway.mapper.CountryMapper;
import org.rococo.gateway.model.countries.CountryDTO;
import org.rococo.gateway.service.ValidationService;
import org.rococo.gateway.util.PageableUtil;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

import static org.springframework.data.domain.Sort.Direction.ASC;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping({"/api/country", "/api/country/"})
public class CountriesController {

    private final CountriesGrpcClient countriesClient;
    private final ValidationService validationService;

    @GetMapping("/{id}")
    public CountryDTO findById(@PathVariable("id") UUID id) {
        log.info("Find country by id: {}", id);
        return countriesClient.findById(id)
                .orElseThrow(() -> new CountryNotFoundException(id));
    }

    @GetMapping("/code/{code}")
    public CountryDTO findById(@PathVariable("code") String countryCode) {
        log.info("Find country by id: {}", countryCode);
        return countriesClient.findByCode(countryCode)
                .orElseThrow(() -> new CountryNotFoundException(countryCode));
    }

    @GetMapping
    public Page<CountryDTO> findAll(@PageableDefault(size = 20, sort = {"name"}, direction = ASC) Pageable pageable) {
        log.info("Find all countries by params: {}", PageableUtil.getPageableLogText(pageable));
        var requestParamsObj = CountryMapper.toRequestParamObj(pageable);
        validationService.validateObject(requestParamsObj, "CountriesFindAllParamsValidationObject");
        return countriesClient.findAll(pageable);
    }

}
