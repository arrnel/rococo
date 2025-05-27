package org.rococo.tests.jupiter.extension;

import io.qameta.allure.Allure;
import io.qameta.allure.AllureLifecycle;
import io.qameta.allure.model.Label;
import io.qameta.allure.model.TestResult;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.rococo.tests.client.gateway.LogsApiClient;
import org.rococo.tests.config.Config;
import org.rococo.tests.model.ServiceName;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Slf4j
public class AllureBackendLogsExtension implements SuiteExtension {

    private static final Config CFG = Config.getInstance();
    private static final String CASE_NAME = "Backend logs";
    private static final AllureLifecycle ALLURE_LIFECYCLE = Allure.getLifecycle();

    @Override
    public void afterSuite() {

        if (!CFG.addServicesLogsToAllure()) {
            log.info("Skip add backend logs to allure");
            return;
        }

        final String caseId = UUID.randomUUID().toString();

        List<Label> labels = List.of(
                new Label().setName("story").setValue(CASE_NAME));

        ALLURE_LIFECYCLE.scheduleTestCase(
                new TestResult()
                        .setUuid(caseId)
                        .setName(CASE_NAME)
                        .setLabels(labels));

        ALLURE_LIFECYCLE.startTestCase(caseId);

        addLogsToAllure();

        ALLURE_LIFECYCLE.stopTestCase(caseId);
        ALLURE_LIFECYCLE.writeTestCase(caseId);

    }

    private static void addLogsToAllure() {

        log.info("Adding backend logs to allure. Case name: {}", CASE_NAME);
        LogsApiClient logsApiClient = new LogsApiClient();

        // Added services logs
        Arrays.stream(ServiceName.values())
                .forEach(serviceName -> addLogToAllure(
                        serviceName.getServiceName(),
                        logsApiClient.downloadServiceLogs(serviceName)
                ));

        // Added all services archived logs
        addLogToAllure(
                "rococo_all_services_logs",
                logsApiClient.downloadAllServicesLogs()
        );

        log.info("Successfully added backend logs to allure");

    }

    private static void addLogToAllure(String serviceName, File file) {
        try (InputStream is = new FileInputStream(file)) {
            ALLURE_LIFECYCLE.addAttachment(
                    serviceName,
                    "text/html",
                    ".%s".formatted(FilenameUtils.getExtension(file.getName())),
                    is
            );
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Getter
    @RequiredArgsConstructor
    private enum LogExtension {
        LOG(".log"), ARCHIVE(".zip");
        private final String extension;
    }

}
