package org.rococo.logs.data.log;

import org.rococo.logs.model.ServiceName;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface LogRepository extends JpaRepository<LogEntity, UUID> {

    List<LogEntity> findAllByServiceNameOrderByTimeAsc(ServiceName serviceName);

    @Modifying
    @Query(nativeQuery = true, value = "TRUNCATE TABLE rococo.logs RESTART IDENTITY CASCADE;")
    void deleteAllLogs();

}
