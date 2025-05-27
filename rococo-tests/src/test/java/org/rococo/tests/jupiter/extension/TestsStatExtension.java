package org.rococo.tests.jupiter.extension;

import lombok.extern.slf4j.Slf4j;
import org.rococo.tests.client.gateway.LogsApiClient;
import org.rococo.tests.config.Config;
import org.rococo.tests.model.allure.logService.TestsStatDTO;

@Slf4j
public class TestsStatExtension implements SuiteExtension {

    public static final Config CFG = Config.getInstance();

    @Override
    public void afterSuite() {

        log.info("Sending tests statistic");

        var testsStat = TestsStatDTO.builder()
                .failed(JUnitTestStatsAggregator.getFailedTestsCount())
                .broken(JUnitTestStatsAggregator.getBrokenTestCount())
                .skipped(JUnitTestStatsAggregator.getSkippedTestCount())
                .unknown(CFG.addServicesLogsToAllure()
                        ? 1
                        : 0)
                .passed(JUnitTestStatsAggregator.getPassedTestCount())
                .total(CFG.addServicesLogsToAllure()
                        ? JUnitTestStatsAggregator.getTotalTestCount() + 1
                        : JUnitTestStatsAggregator.getTotalTestCount())
                .build();

        new LogsApiClient().addNewTestsStat(testsStat);

        log.info("Tests statistic successfully added");

    }

}
