package org.rococo.tests.util;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public enum ThreadSafeTestNameStore {

    INSTANCE;
    private static final ThreadLocal<String> threadSafeTestNameStore = ThreadLocal.withInitial(String::new);

    @Nullable
    public String getCurrentTestTitle() {
        return threadSafeTestNameStore.get();
    }

    public void setCurrentTestTitle(@Nonnull String token) {
        threadSafeTestNameStore.set(token);
    }

}
