package org.rococo.tests.browser;

import com.codeborne.selenide.Configuration;
import org.jetbrains.annotations.NotNull;
import org.openqa.selenium.chrome.ChromeOptions;
import org.rococo.tests.config.Config;
import org.rococo.tests.util.ThreadSafeTestNameStore;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RemoteChromeStrategy implements BaseStrategy, ChromeStrategyMixin {


    @Override
    public void initDriver() {
        initChromeDriver(new ChromeOptions());
        Configuration.remote = Config.getInstance().remoteUrl();
    }

    @NotNull
    @Override
    public List<String> chromeArgs() {
        return List.of(
                "--no-sandbox",
                "--lang=ru-RU",
                "--browser-locale=ru-RU",
                "--accept-lang=ru-RU",
                "--disable-dev-shm-usage",
                "--disable-gpu"
        );
    }

    @NotNull
    @Override
    public List<String> chromeExtensions() {
        return List.of();
    }

    @NotNull
    @Override
    public Map<String, String> experimentalOptions() {
        var testName = ThreadSafeTestNameStore.INSTANCE.getCurrentTestTitle();
        var downloadDir = "%s/%s".formatted(CFG.browserDownloadDir(), testName);

        Map<String, String> experimentalOptions = new HashMap<>();
        experimentalOptions.put("download.default_directory", downloadDir);
        experimentalOptions.put("savefile.default_directory", downloadDir);
        experimentalOptions.put("download.prompt_for_download", "false");
        experimentalOptions.put("download.directory_upgrade", "true");
        experimentalOptions.put("credentials_enable_service", "false");
        experimentalOptions.put("autofill.profile_enabled", "false");
        experimentalOptions.put("autofill.credit_card_enabled", "false");
        experimentalOptions.put("profile.default_content_setting_values.automatic_downloads", "true");
        experimentalOptions.put("profile.default_content_setting_values.notifications", "2");
        experimentalOptions.put("profile.password_manager_enabled", "false");
        experimentalOptions.put("safebrowsing_for_trusted_sources_enabled", "false");
        experimentalOptions.put("safebrowsing.enabled", "false");

        return experimentalOptions;
    }

    @NotNull
    @Override
    public Map<String, Object> capabilities() {
        return new CapabilitiesBuilder().selenoidCapabilities();
    }
}
