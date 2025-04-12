package org.rococo.tests.service;

import lombok.extern.slf4j.Slf4j;
import org.rococo.tests.client.gateway.AuthApiClient;
import org.rococo.tests.config.Config;
import org.rococo.tests.model.Token;

import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
public enum DefaultUserTokenUpdater implements Runnable {

    INSTANCE;

    private static final Config CFG = Config.getInstance();
    private static final long UPDATE_INTERVAL_MS = CFG.updateTokenTimeoutMillis();
    private static final AtomicBoolean isRun = new AtomicBoolean(true);
    private static String token = getNewToken();

    public Token getToken() {
        return new Token(token);
    }

    private static String getNewToken() {
        return new AuthApiClient().signIn(CFG.testUserName(), CFG.testUserPassword());
    }

    @Override
    public void run() {
        isRun.set(true);
        while (isRun.get()) {
            try {
                Thread.sleep(UPDATE_INTERVAL_MS);
                token = getNewToken();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            } catch (Exception ex) {
                log.error("Failed to update token: {}", ex.getMessage());
            }
        }
    }

    public void shutdown() {
        isRun.set(false);
    }
}
