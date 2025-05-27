package org.rococo.logs.service.impl;

import lombok.RequiredArgsConstructor;
import org.rococo.logs.data.log.LogEntity;
import org.rococo.logs.data.log.LogRepository;
import org.rococo.logs.model.ServiceName;
import org.rococo.logs.service.LogService;
import org.rococo.logs.util.FileUtils;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LogServiceImpl implements LogService {

    private final LogRepository logRepository;

    @Override
    @Transactional
    public void save(LogEntity log) {
        logRepository.save(log);
    }

    @Override
    @Transactional(readOnly = true)
    public Resource findLogsByserviceName(ServiceName serviceName) {
        var serviceLogs = logRepository.findAllByServiceNameOrderByTimeAsc(serviceName);
        try {
            return FileUtils.createLogFile(serviceName, serviceLogs);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Resource getAllServicesLogs() {
        var map = new HashMap<ServiceName, List<LogEntity>>();
        Arrays.stream(ServiceName.values())
                .forEach(serviceName -> map.put(
                        serviceName,
                        logRepository.findAllByServiceNameOrderByTimeAsc(serviceName)
                ));
        try {
            return FileUtils.packFilesToArchive(map);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    @Transactional
    public void truncateTable() {
        logRepository.deleteAllLogs();
    }

}
