package org.rococo.tests.jupiter.extension;

import io.qameta.allure.Allure;
import io.qameta.allure.AllureLifecycle;
import io.qameta.allure.model.Label;
import io.qameta.allure.model.TestResult;
import lombok.SneakyThrows;
import org.rococo.tests.config.Config;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.UUID;

public class AllureBackendLogsExtension implements SuiteExtension {

    public static final String CASE_NAME = "rococo backend logs";

    private static void addLogsToAllure(AllureLifecycle allureLifecycle) throws IOException {

        if (!Config.getInstance().addServicesLogsToAllure()) return;

        addLogToAllure(
                allureLifecycle,
                "rococo-artists log",
                Path.of("./logs/rococo-artists/app.log"));

        addLogToAllure(
                allureLifecycle,
                "rococo-auth log",
                Path.of("./logs/rococo-auth/app.log"));

        addLogToAllure(
                allureLifecycle,
                "rococo-countries log",
                Path.of("./logs/rococo-countries/app.log"));

        addLogToAllure(
                allureLifecycle,
                "rococo-files log",
                Path.of("./logs/rococo-files/app.log"));

        addLogToAllure(allureLifecycle,
                "rococo-museums log",
                Path.of("./logs/rococo-museums/app.log"));

        addLogToAllure(
                allureLifecycle,
                "rococo-paintings log",
                Path.of("./logs/rococo-paintings/app.log"));

        addLogToAllure(
                allureLifecycle,
                "rococo-users log",
                Path.of("./logs/rococo-users/app.log"));

    }

    private static void addLogToAllure(AllureLifecycle allureLifecycle, String name, Path pathToLog) throws IOException {
        allureLifecycle.addAttachment(
                name,
                "text/html",
                ".log",
                Files.newInputStream(pathToLog)
        );
    }

    @SneakyThrows
    @Override
    public void afterSuite() {

        final AllureLifecycle allureLifecycle = Allure.getLifecycle();
        final String caseId = UUID.randomUUID().toString();

        List<Label> labels = List.of(
                new Label().setName("story").setValue("rococo backend logs"));

        allureLifecycle.scheduleTestCase(
                new TestResult()
                        .setUuid(caseId)
                        .setName(CASE_NAME)
                        .setLabels(labels));

        allureLifecycle.startTestCase(caseId);

        addLogsToAllure(allureLifecycle);

        allureLifecycle.stopTestCase(caseId);
        allureLifecycle.writeTestCase(caseId);
    }

}
