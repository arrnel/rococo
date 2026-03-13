package org.rococo.tests.browser;

import org.rococo.tests.config.Config;
import org.rococo.tests.util.DurationUtil;
import org.rococo.tests.util.ThreadSafeTestNameStore;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;

class CapabilitiesBuilder {

    private static final String SELENOID_OPTIONS_TITLE = "selenoid:options";
    private static final Config CFG = Config.getInstance();

    @Nonnull
    public Map<String, Object> capabilities() {
        var caps = CFG.isLocal()
                ? localCapabilities()
                : selenoidCapabilities();
        Map<String, Object> merged = new HashMap<>(caps);
        merged.putAll(timeoutCapabilities());
        return merged;
    }

    @Nonnull
    public Map<String, Object> localCapabilities() {
        return Map.of(
                "browserName", CFG.browserName(),
                "browserVersion", CFG.browserVersion(),
                "pageLoadStrategy", CFG.pageLoadStrategy(),
                "unhandledPromptBehavior", "dismiss"
        );
    }

    @Nonnull
    public Map<String, Object> selenoidCapabilities() {

        var testName = ThreadSafeTestNameStore.INSTANCE.getCurrentTestTitle();

        Map<String, Object> labels = Map.of(
                "env", Config.ENV,
                "project", Config.PROJECT_NAME,
                "test_name", testName
        );

        Map<String, Object> selenoidOptions = new HashMap<>();
        selenoidOptions.put("enableVNC", CFG.enableVNC());
        selenoidOptions.put("enableVideo", CFG.enableVideo());
        selenoidOptions.put("enableLog", CFG.enableLogs());
        selenoidOptions.put("timeZone", "Europe/Moscow");
        selenoidOptions.put("labels", labels);
        selenoidOptions.put("sessionTimeout", DurationUtil.milliToGolangDuration(CFG.browserRemoteSessionTimeout()));


        if (CFG.browser_remote_attach_id_type().equalsIgnoreCase("test_name")) {
            selenoidOptions.put("name", testName);
            selenoidOptions.put("logName", "%s.log".formatted(testName));
            selenoidOptions.put("videoName", "%s.mp4".formatted(testName));
        }

        return Map.of(
                "browserName", CFG.browserName(),
                "browserVersion", CFG.browserVersion(),
                "pageLoadStrategy", CFG.pageLoadStrategy(),
                "unhandledPromptBehavior", "dismiss",
                SELENOID_OPTIONS_TITLE, selenoidOptions
        );
    }

    @Nonnull
    private Map<String, Object> timeoutCapabilities() {
        Map<String, Integer> timeouts = Map.of(
                "implicit", CFG.timeout(),
                "pageLoad", CFG.pageLoadTimeout()
        );
        return Map.of("timeouts", timeouts);
    }

}
