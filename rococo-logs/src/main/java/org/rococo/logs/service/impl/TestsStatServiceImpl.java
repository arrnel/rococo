package org.rococo.logs.service.impl;

import lombok.RequiredArgsConstructor;
import org.rococo.logs.data.stat.TestsStatEntity;
import org.rococo.logs.data.stat.TestsStatRepository;
import org.rococo.logs.service.TestsStatService;
import org.springframework.stereotype.Service;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@ParametersAreNonnullByDefault
public class TestsStatServiceImpl implements TestsStatService {

    private final TestsStatRepository testsStatRepository;

    @Nonnull
    @Override
    public TestsStatEntity addNewTestsStat(TestsStatEntity testsStat) {
        return testsStatRepository.save(testsStat);
    }

    @Nonnull
    @Override
    public Optional<TestsStatEntity> findTestsStatById(UUID id) {
        return testsStatRepository.findById(id);
    }

    @Nonnull
    @Override
    public Optional<TestsStatEntity> getLastTestsStat() {
        return testsStatRepository.getLast();
    }

    @Override
    public void clearTable() {
        testsStatRepository.deleteAllTestsStat();
    }

}
