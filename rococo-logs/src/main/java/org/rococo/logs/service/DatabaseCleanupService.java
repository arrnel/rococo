package org.rococo.logs.service;

import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class DatabaseCleanupService {

    private final LogService logService;

    @PreDestroy
    public void cleanup() {
        log.info("Cleanup database before application shutdown");
        logService.truncateTable();
        log.info("Database cleanup completed");
    }

}
