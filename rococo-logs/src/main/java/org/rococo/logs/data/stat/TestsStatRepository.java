package org.rococo.logs.data.stat;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Optional;
import java.util.UUID;

@ParametersAreNonnullByDefault
public interface TestsStatRepository extends JpaRepository<TestsStatEntity, UUID> {

    @Nonnull
    @Override
    Optional<TestsStatEntity> findById(UUID id);

    @Nonnull
    @Query(value = "SELECT h FROM TestsStatEntity h ORDER BY h.dateTime DESC LIMIT 1")
    Optional<TestsStatEntity> getLast();

    @Modifying
    @Query(nativeQuery = true, value = "TRUNCATE TABLE rococo.tests_stats RESTART IDENTITY CASCADE;")
    void deleteAllTestsStat();

}
