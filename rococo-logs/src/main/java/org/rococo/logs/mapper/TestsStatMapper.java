package org.rococo.logs.mapper;

import org.rococo.logs.data.stat.TestsStatEntity;
import org.rococo.logs.model.TestsStatDTO;

import java.time.LocalDateTime;

public class TestsStatMapper {

    private TestsStatMapper() {
    }

    public static TestsStatEntity toEntity(TestsStatDTO dto) {
        return TestsStatEntity.builder()
                .failed(dto.failed())
                .broken(dto.broken())
                .skipped(dto.skipped())
                .passed(dto.passed())
                .unknown(dto.unknown())
                .total(dto.total())
                .dateTime(LocalDateTime.now())
                .build();
    }

    public static TestsStatDTO toDTO(TestsStatEntity entity) {
        return TestsStatDTO.builder()
                .id(entity.getId())
                .failed(entity.getFailed())
                .broken(entity.getBroken())
                .skipped(entity.getSkipped())
                .passed(entity.getPassed())
                .unknown(entity.getUnknown())
                .total(entity.getTotal())
                .isPassed(entity.getIsPassed())
                .passedPercentage(entity.getPassedPercentage())
                .build();
    }

}
