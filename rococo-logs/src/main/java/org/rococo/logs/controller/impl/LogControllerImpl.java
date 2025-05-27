package org.rococo.logs.controller.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.rococo.logs.controller.LogController;
import org.rococo.logs.model.ServiceName;
import org.rococo.logs.service.LogService;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping({"/api/logs/service", "/api/logs/service/"})
@RequiredArgsConstructor
public class LogControllerImpl implements LogController {

    private final LogService logService;

    @Override
    @GetMapping({"/{service}"})
    public ResponseEntity<Resource> getServiceLogs(@PathVariable("service") String serviceName) {
        var name = ServiceName.findByServiceName(serviceName);
        return returnResource(logService.findLogsByserviceName(name), "%s.log".formatted(name.getServiceName()));
    }

    @Override
    @GetMapping
    public ResponseEntity<Resource> getAllServicesLogs() {
        return returnResource(logService.getAllServicesLogs(), "rococo-logs.zip");
    }

    @Override
    @DeleteMapping
    public void clearLogs() {
        logService.truncateTable();
    }

    private ResponseEntity<Resource> returnResource(Resource resource, String fileName) {
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"%s\"".formatted(fileName))
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(resource);
    }

}
