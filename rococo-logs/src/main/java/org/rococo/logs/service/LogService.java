package org.rococo.logs.service;

import org.rococo.logs.data.log.LogEntity;
import org.rococo.logs.model.ServiceName;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

@Service
public interface LogService {

    void save(LogEntity entity);

    Resource findLogsByserviceName(ServiceName serviceName);

    Resource getAllServicesLogs();

    void truncateTable();

}
