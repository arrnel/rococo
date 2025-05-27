package org.rococo.logs.service;

import org.rococo.logs.data.stat.TestsStatEntity;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Optional;
import java.util.UUID;

@ParametersAreNonnullByDefault
public interface TestsStatService {

    @Nonnull
    TestsStatEntity addNewTestsStat(TestsStatEntity testsStat);

    @Nonnull
    Optional<TestsStatEntity> findTestsStatById(UUID id);

    @Nonnull
    Optional<TestsStatEntity> getLastTestsStat();

    void clearTable();

}
