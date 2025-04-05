package org.rococo.tests.jupiter.extension;

import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

import javax.annotation.ParametersAreNonnullByDefault;

/**
 * EXTENSION HAS GLOBAL REGISTRATION TYPE
 */
@ParametersAreNonnullByDefault
public class TestMethodContextExtension implements BeforeEachCallback, AfterEachCallback {

    @Override
    public void beforeEach(ExtensionContext context) throws Exception {
        Holder.INSTANCE.set(context);
    }

    @Override
    public void afterEach(ExtensionContext context) throws Exception {
        Holder.INSTANCE.remove();
    }

    private enum Holder {

        INSTANCE;

        private final ThreadLocal<ExtensionContext> store = new ThreadLocal<>();

        public void set(ExtensionContext context) {
            store.set(context);
        }

        public ExtensionContext get() {
            return store.get();
        }

        public void remove() {
            store.remove();
        }

    }

    public static ExtensionContext context() {
        return Holder.INSTANCE.get();
    }

}
