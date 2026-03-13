package org.rococo.tests.browser;

import org.openqa.selenium.firefox.FirefoxOptions;
import org.rococo.tests.util.ThreadSafeTestNameStore;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FirefoxStrategy implements BaseStrategy, FirefoxStrategyMixin {

    @Override
    public void initDriver() {
        initFirefoxDriver(new FirefoxOptions());
    }

    @Nonnull
    @Override
    public List<String> firefoxArgs() {
        return List.of(
                "--no-sandbox",
                "--lang=ru-RU",
                "--browser-locale=ru-RU",
                "--accept-lang=ru-RU",
                "--disable-dev-shm-usage",
                "--disable-gpu"
        );
    }

    @Nonnull
    @Override
    public List<String> firefoxExtensions() {
        return List.of();
    }

    @Nonnull
    @Override
    public Map<String, String> firefoxPrefs() {
        var testName = ThreadSafeTestNameStore.INSTANCE.getCurrentTestTitle();
        var downloadDir = "%s/%s".formatted(CFG.browserDownloadDir(), testName);
        var prefs = new HashMap<String, String>();
        prefs.put("pdfjs.disabled", "true");
        prefs.put("browser.download.folderList", "2");
        prefs.put("browser.download.dir", downloadDir);
        prefs.put("browser.download.manager.showWhenStarting", "false");
        prefs.put("browser.download.panel.shown", "false");
        prefs.put("dom.webdriver.enabled", "false");
        prefs.put("useAutomationExtension", "false");
        prefs.put("media.webspeech.synth.enabled", "false");
        prefs.put("media.webspeech.recognition.enable", "false");
        return prefs;
    }

}
