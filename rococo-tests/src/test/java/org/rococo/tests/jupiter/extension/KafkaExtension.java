package org.rococo.tests.jupiter.extension;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.rococo.tests.service.kafka.KafkaConsumerService;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@ParametersAreNonnullByDefault
public class KafkaExtension implements SuiteExtension {

    private static final KafkaConsumerService kafkaService = new KafkaConsumerService();
    private static final ExecutorService executor = Executors.newSingleThreadExecutor();

    @Override
    public void beforeSuite(ExtensionContext context) {
        executor.execute(kafkaService);
        executor.shutdown();
    }

    @Override
    public void afterSuite() {
        kafkaService.shutdown();
    }
}