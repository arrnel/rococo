package org.rococo.tests.browser;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.Selenide;
import org.rococo.tests.config.Config;

import java.util.Map;

public class DriverManager {

    private static final String REMOTE_PREFIX = "remote-";
    private static final Config CFG = Config.getInstance();
    private static final Map<String, BaseStrategy> STRATEGIES = Map.of(
            "chrome", new ChromeStrategy(),
            "firefox", new ChromeStrategy(),
            REMOTE_PREFIX + "chrome", new RemoteChromeStrategy(),
            REMOTE_PREFIX + "firefox", new RemoteFirefoxStrategy()
    );

    public static void initDriver() {
        var strategyName = CFG.isLocal()
                ? CFG.browserName()
                : REMOTE_PREFIX + CFG.browserName();
        STRATEGIES.get(strategyName).initDriver();
        Configuration.baseUrl = CFG.frontUrl();
        Configuration.savePageSource = true;
        Configuration.screenshots = true;
    }

    public static void quit() {
        Selenide.closeWebDriver();
    }


}
