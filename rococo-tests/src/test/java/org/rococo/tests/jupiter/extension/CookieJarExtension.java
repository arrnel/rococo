package org.rococo.tests.jupiter.extension;

import org.junit.jupiter.api.extension.AfterTestExecutionCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.rococo.tests.client.gateway.core.store.ThreadSafeCookieStore;

import javax.annotation.ParametersAreNonnullByDefault;

/**
 * EXTENSION HAS GLOBAL REGISTRATION TYPE
 */
@ParametersAreNonnullByDefault
public class CookieJarExtension implements AfterTestExecutionCallback {

    @Override
    public void afterTestExecution(ExtensionContext context) throws Exception {
        ThreadSafeCookieStore.INSTANCE.removeAll();
    }

}
