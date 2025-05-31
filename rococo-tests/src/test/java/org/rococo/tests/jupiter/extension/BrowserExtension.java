package org.rococo.tests.jupiter.extension;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.WebDriverRunner;
import com.codeborne.selenide.logevents.SelenideLogger;
import io.qameta.allure.Allure;
import io.qameta.allure.selenide.AllureSelenide;
import org.junit.jupiter.api.extension.*;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.rococo.tests.config.Config;

import java.io.ByteArrayInputStream;
import java.util.HashMap;
import java.util.Map;

public class BrowserExtension implements BeforeEachCallback, AfterEachCallback, TestExecutionExceptionHandler, LifecycleMethodExecutionExceptionHandler {

    private static final Config CFG = Config.getInstance();

    static {

        var env = System.getProperty("test.env");
        Configuration.browser = "chrome";
        Configuration.browserVersion = "127.0";
        Configuration.timeout = 15000;
        Configuration.pageLoadTimeout = 15000;
        Configuration.pageLoadStrategy = "eager";
        Configuration.browserSize = "1920x1080";
        Configuration.baseUrl = CFG.frontUrl();
        switch (env) {
            case "local": {
                Configuration.browserCapabilities = new ChromeOptions()
                        .setBrowserVersion("127.0")
                        .addArguments("--no-sandbox", "--lang=ru-RU", "--browser-locale=ru-RU", "--accept-lang=ru-RU");
                break;
            }
            case "docker": {
                String browser = System.getenv("SELENOID_BROWSER");
                Configuration.remote = "http://selenoid:4444/wd/hub";
                Map<String, Boolean> selenoidOptions = new HashMap<>();
                selenoidOptions.put("enableVNC", true);
                selenoidOptions.put("enableVideo", false);
                selenoidOptions.put("enableLog", true);
                var args = new String[]{"--no-sandbox", "--lang=ru-RU", "--browser-locale=ru-RU", "--accept-lang=ru-RU"};
                if ("chrome".equals(browser)) {
                    Configuration.browserVersion = "127.0";
                    ChromeOptions options = new ChromeOptions();
                    options.addArguments(args)
                            .setCapability("selenoid:options", selenoidOptions);
                    Configuration.browserCapabilities = options;
                } else if ("firefox".equals(browser)) {
                    Configuration.browserVersion = "125.0";
                    FirefoxOptions options = new FirefoxOptions();
                    options.addArguments(args)
                            .setCapability("selenoid:options", selenoidOptions);
                    Configuration.browserCapabilities = options;
                }
                break;
            }
            default:
                throw new IllegalArgumentException("Unknown test environment: %s".formatted(env));
        }
    }

    public static final ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace.create(BrowserExtension.class);

    @Override
    public void beforeEach(ExtensionContext context) throws Exception {
        SelenideLogger.addListener("Allure-selenide", new AllureSelenide()
                .savePageSource(false)
                .screenshots(false)
                .includeSelenideSteps(false)
        );
    }

    @Override
    public void afterEach(ExtensionContext context) throws Exception {
        if (WebDriverRunner.hasWebDriverStarted())
            WebDriverRunner.closeWebDriver();
    }

    @Override
    public void handleTestExecutionException(ExtensionContext context, Throwable throwable) throws Throwable {
        savePage();
        doScreenshot();
        throw throwable;
    }

    @Override
    public void handleBeforeEachMethodExecutionException(ExtensionContext context, Throwable throwable) throws Throwable {
        savePage();
        doScreenshot();
        throw throwable;
    }

    @Override
    public void handleAfterEachMethodExecutionException(ExtensionContext context, Throwable throwable) throws Throwable {
        savePage();
        doScreenshot();
        throw throwable;
    }

    private static void doScreenshot() {
        if (WebDriverRunner.hasWebDriverStarted()) {
            Allure.addAttachment(
                    "Screen on fail",
                    new ByteArrayInputStream(
                            ((TakesScreenshot) WebDriverRunner.getWebDriver()).getScreenshotAs(OutputType.BYTES)
                    )
            );
        }
    }

    private static void savePage() {
        if (WebDriverRunner.hasWebDriverStarted()) {
            Allure.addAttachment(
                    "Page html",
                    "text/html",
                    WebDriverRunner.getWebDriver().getPageSource(),
                    "html"
            );
        }
    }

}
