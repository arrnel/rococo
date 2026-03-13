package org.rococo.tests.browser;

import com.codeborne.selenide.Configuration;
import org.openqa.selenium.chrome.ChromeOptions;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Map;

interface ChromeStrategyMixin {

    @Nonnull
    List<String> chromeArgs();

    @Nonnull
    List<String> chromeExtensions();

    @Nonnull
    Map<String, String> experimentalOptions();

    @Nonnull
    default Map<String, Object> capabilities() {
        return new CapabilitiesBuilder().capabilities();
    }

    default void setChromeArgs(ChromeOptions options) {
        options.addArguments(chromeArgs());
    }

    default void setChromeExtensions(ChromeOptions options) {
        // NOT IMPLEMENTED
    }

    default void setExperimentalOptions(ChromeOptions options) {
        options.setExperimentalOption("prefs", experimentalOptions());
    }

    default void setCapabilities(ChromeOptions options) {
        capabilities().forEach(options::setCapability);
    }

    default void initChromeDriver(ChromeOptions options) {
        setChromeArgs(options);
        setExperimentalOptions(options);
        setCapabilities(options);
        Configuration.browserCapabilities = options;
    }

}
