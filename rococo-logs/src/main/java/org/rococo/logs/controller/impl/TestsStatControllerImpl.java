package org.rococo.logs.controller.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.rococo.logs.controller.TestsStatController;
import org.rococo.logs.mapper.TestsStatMapper;
import org.rococo.logs.model.TestsStatDTO;
import org.rococo.logs.service.TestsStatService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping({"/api/stat/tests", "/api/stat/tests"})
@RequiredArgsConstructor
public class TestsStatControllerImpl implements TestsStatController {

    private final TestsStatService testsStatService;

    @Value("${tests.quality_gate.min_passed_percentage:90.0}")
    private Double minPassedPercentage;

    @Override
    @PostMapping
    public ResponseEntity<TestsStatDTO> addNewTestsStat(@RequestBody TestsStatDTO testsStats) {

        var validTests = testsStats.passed() + testsStats.skipped() + testsStats.unknown();
        var passedPercentage = Math.round((double) validTests / testsStats.total() * 100 * 1000) / 1000.0;
        var isPassed = passedPercentage >= minPassedPercentage;

        var entity = TestsStatMapper.toEntity(testsStats)
                .setIsPassed(isPassed)
                .setPassedPercentage(passedPercentage);

        var dto = TestsStatMapper.toDTO(testsStatService.addNewTestsStat(entity));
        log.info("Saved new tests stat: {}", dto.toString());

        return new ResponseEntity<>(dto, HttpStatus.CREATED);
    }

    @Override
    @GetMapping("/id/{id}")
    public ResponseEntity<TestsStatDTO> findTestsStatById(@PathVariable("id") UUID id) {
        return testsStatService.findTestsStatById(id)
                .map(TestsStatMapper::toDTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Override
    @GetMapping("/last")
    public ResponseEntity<TestsStatDTO> getLastTestsStat() {
        return testsStatService.getLastTestsStat()
                .map(TestsStatMapper::toDTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Override
    @DeleteMapping("/all")
    public void clearAllTestsStats() {
        testsStatService.clearTable();
    }

}
