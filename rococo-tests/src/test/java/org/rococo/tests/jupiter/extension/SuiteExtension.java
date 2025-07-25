package org.rococo.tests.jupiter.extension;

import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public interface SuiteExtension extends BeforeAllCallback {

    @Override
    default void beforeAll(ExtensionContext context) throws Exception {

        final ExtensionContext rootContext = context.getRoot();

        rootContext
                .getStore(ExtensionContext.Namespace.GLOBAL)
                .getOrComputeIfAbsent(
                        this.getClass(),
                        key -> {
                            beforeSuite(context);
                            return (ExtensionContext.Store.CloseableResource) this::afterSuite;
                        }
                );
    }

    default void beforeSuite(ExtensionContext context) {
    }

    default void afterSuite() {
    }

}
