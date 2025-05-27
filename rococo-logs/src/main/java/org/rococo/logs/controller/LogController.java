package org.rococo.logs.controller;

import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;

public interface LogController {

    ResponseEntity<Resource> getServiceLogs(String serviceName);

    ResponseEntity<Resource> getAllServicesLogs();

    void clearLogs();

}
