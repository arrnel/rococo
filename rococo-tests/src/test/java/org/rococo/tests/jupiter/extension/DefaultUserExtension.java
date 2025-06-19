package org.rococo.tests.jupiter.extension;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.rococo.tests.service.DefaultUserTokenUpdater;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * EXTENSION HAS GLOBAL REGISTRATION TYPE
 */
@ParametersAreNonnullByDefault
public class DefaultUserExtension implements SuiteExtension {

    private static final ExecutorService executor = Executors.newSingleThreadExecutor();

    @Override
    public void beforeSuite(ExtensionContext context) {
        executor.execute(DefaultUserTokenUpdater.INSTANCE);
    }

    @Override
    public void afterSuite() {
        if (!executor.isShutdown())
            executor.shutdownNow();
    }

}
