package org.rococo.tests.jupiter.extension;

import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.rococo.tests.util.ThreadSafeTestNameStore;

public class TestNameExtension implements BeforeEachCallback {

    @Override
    public void beforeEach(ExtensionContext context) throws Exception {
        String className = context.getRequiredTestClass().getSimpleName();
        String methodName = context.getRequiredTestMethod().getName();
        ThreadSafeTestNameStore.INSTANCE.setCurrentTestTitle("%s.%s".formatted(className, methodName));
    }
}