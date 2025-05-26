package org.rococo.logs.controller;

import org.rococo.logs.model.TestsStatDTO;
import org.springframework.http.ResponseEntity;

import java.util.UUID;

public interface TestsStatController {

    ResponseEntity<TestsStatDTO> addNewTestsStat(TestsStatDTO testsStat);

    ResponseEntity<TestsStatDTO> findTestsStatById(UUID id);

    ResponseEntity<TestsStatDTO> getLastTestsStat();

    void clearAllTestsStats();

}
