package org.rococo.tests.client.gateway.core.store;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public enum AuthStore {

    INSTANCE;

    // Code
    private static final ThreadLocal<String> threadSafeAuthCodeStore = ThreadLocal.withInitial(String::new);

    // Users authorization token.
    private static final ThreadLocal<String> threadSafeAuthTokenStore = ThreadLocal.withInitial(String::new);

    @Nullable
    public String getCode() {
        return threadSafeAuthCodeStore.get();
    }

    public void setCode(String code) {
        threadSafeAuthCodeStore.set(code);
    }

    @Nullable
    public String getToken() {
        return threadSafeAuthTokenStore.get();
    }

    public void setToken(String token) {
        threadSafeAuthTokenStore.set(token);
    }

}
