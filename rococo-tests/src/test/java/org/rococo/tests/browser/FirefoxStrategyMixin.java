package org.rococo.tests.browser;

import com.codeborne.selenide.Configuration;
import org.openqa.selenium.firefox.FirefoxOptions;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.Map;

@ParametersAreNonnullByDefault
interface FirefoxStrategyMixin {

    @Nonnull
    abstract List<String> firefoxArgs();

    @Nonnull
    abstract List<String> firefoxExtensions();

    @Nonnull
    abstract Map<String, String> firefoxPrefs();

    @Nonnull
    abstract Map<String, Object> capabilities();

    default void setFirefoxArgs(FirefoxOptions options) {
        options.addArguments(firefoxArgs());
    }

    default void setFirefoxPrefs(FirefoxOptions options) {
        firefoxPrefs().forEach(options::addPreference);
    }

    default void setFirefoxExtensions(FirefoxOptions options) {
        // NOT IMPLEMENTED
    }

    default void setCapabilities(FirefoxOptions options) {
        firefoxPrefs().forEach(options::addPreference);
    }

    default void initFirefoxDriver(FirefoxOptions options) {
        setFirefoxArgs(options);
        setFirefoxPrefs(options);
        setFirefoxExtensions(options);
        setCapabilities(options);
        Configuration.browserCapabilities = options;
    }


}
