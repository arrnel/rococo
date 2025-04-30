package org.rococo.tests.conditions;

import com.codeborne.selenide.CheckResult;
import com.codeborne.selenide.Driver;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.WebElementCondition;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.qameta.allure.Allure;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.WebElement;
import org.rococo.tests.config.Config;
import org.rococo.tests.ex.ExpectedImageNotFoundException;
import org.rococo.tests.ex.ScreenshotException;
import org.rococo.tests.model.allure.ScreenDiff;
import org.rococo.tests.util.ScreenDiffResult;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;

import static com.codeborne.selenide.CheckResult.accepted;

@Slf4j
@ParametersAreNonnullByDefault
public final class ScreenshotCondition {

    private static final Config CFG = Config.getInstance();
    private static final ObjectMapper OM = new ObjectMapper();
    private static final Base64.Encoder encoder = Base64.getEncoder();
    private static final double DEFAULT_PERCENTAGE_TOLERANCE = 0.02;

    /**
     * @param urlToScreenshot    Path to expected screenshot
     * @param percentOfTolerance Allowed percent of difference [0; 0.2].
     * @param millis             Wait before making screenshot
     * @param rewriteExpected    Create and save new expected screenshot
     */
    @Nonnull
    public static WebElementCondition screenshot(
            String urlToScreenshot,
            double percentOfTolerance,
            long millis,
            boolean rewriteExpected
    ) {

        Selenide.sleep(millis);
        var relativeUrl = urlToScreenshot.charAt(0) == '/'
                ? urlToScreenshot.substring(0, urlToScreenshot.length() - 1)
                : urlToScreenshot;
        var expectedScreenshotUrl = Path.of(CFG.screenshotBaseDir() + relativeUrl);

        return new WebElementCondition("expected screenshot: [%s]".formatted(expectedScreenshotUrl.toString())) {

            @NotNull
            @Override
            public CheckResult check(Driver driver, WebElement element) {

                final BufferedImage expectedScreenshot = getExpectedScreenshot(expectedScreenshotUrl);
                BufferedImage actualScreenshot = takeElementScreenshot(element);

                ScreenDiffResult diff = new ScreenDiffResult(
                        expectedScreenshot,
                        actualScreenshot,
                        percentOfTolerance
                );

                if (diff.getAsBoolean()) {

                    addAttachment(
                            ScreenDiff.builder()
                                    .expected("data:image/png;base64," + encoder.encodeToString(imageToBytes(expectedScreenshot)))
                                    .actual("data:image/png;base64," + encoder.encodeToString(imageToBytes(actualScreenshot)))
                                    .diff("data:image/png;base64," + encoder.encodeToString(imageToBytes(diff.getDiff().getMarkedImage())))
                                    .build()
                    );
                    String message = percentOfTolerance == 0
                            ? "Expected and actual screenshots not identical"
                            : "Expected and actual screenshots has difference greater then: " + percentOfTolerance;
                    throw new ScreenshotException(message);
                }

                if (CFG.rewriteAllImages() || rewriteExpected)
                    saveNewExpectedScreenshot(actualScreenshot, expectedScreenshotUrl);

                return accepted();

            }
        };
    }

    /**
     * @param urlToScreenshot    Path to expected screenshot
     * @param percentOfTolerance Allowed percent of difference [0; 0.2].
     * @param millis             Wait before making screenshot
     */
    @Nonnull
    public static WebElementCondition screenshot(
            String urlToScreenshot,
            double percentOfTolerance,
            long millis
    ) {
        return (screenshot(urlToScreenshot, percentOfTolerance, millis, false));
    }

    /**
     * @param urlToScreenshot Path to expected screenshot
     * @param millis          Wait before making screenshot
     * @apiNote * percentOfTolerance = DEFAULT_PERCENTAGE_TOLERANCE.
     */
    @Nonnull
    public static WebElementCondition screenshot(
            String urlToScreenshot,
            long millis
    ) {
        return (screenshot(urlToScreenshot, DEFAULT_PERCENTAGE_TOLERANCE, millis, false));
    }

    /**
     * @param urlToScreenshot Path to expected screenshot;
     * @param millis          Wait before making screenshot;
     * @param rewriteExpected Create and save new expected screenshot;
     * @apiNote * percentOfTolerance = DEFAULT_PERCENTAGE_TOLERANCE.
     */
    @Nonnull
    public static WebElementCondition screenshot(
            String urlToScreenshot,
            long millis,
            boolean rewriteExpected
    ) {
        return (screenshot(urlToScreenshot, DEFAULT_PERCENTAGE_TOLERANCE, millis, rewriteExpected));
    }

    /**
     * @param urlToScreenshot Path to expected screenshot
     * @param rewriteExpected Create and save new expected screenshot
     * @apiNote * percentOfTolerance = DEFAULT_PERCENTAGE_TOLERANCE.
     * <br>
     * * millis = 0
     */
    @Nonnull
    public static WebElementCondition screenshot(
            String urlToScreenshot,
            boolean rewriteExpected
    ) {
        return (screenshot(urlToScreenshot, DEFAULT_PERCENTAGE_TOLERANCE, 0, rewriteExpected));
    }

    /**
     * @param urlToScreenshot Path to expected screenshot
     * @apiNote * percentOfTolerance = DEFAULT_PERCENTAGE_TOLERANCE.
     * <br>
     * * millis = 0
     */
    @Nonnull
    public static WebElementCondition screenshot(String urlToScreenshot) {
        return (screenshot(urlToScreenshot, DEFAULT_PERCENTAGE_TOLERANCE, 0, false));
    }

    private static BufferedImage getExpectedScreenshot(Path expectedScreenshotUrl) {

        if (Files.notExists(expectedScreenshotUrl))
            throw new ExpectedImageNotFoundException("File not found by path: " + expectedScreenshotUrl.toAbsolutePath());

        if (Files.isDirectory(expectedScreenshotUrl))
            throw new IllegalStateException(expectedScreenshotUrl.toAbsolutePath() + " (Is a directory)");

        log.info("Expected screenshot path: {} \n absolute path: {}", expectedScreenshotUrl, expectedScreenshotUrl.toAbsolutePath());

        try (FileInputStream fis = new FileInputStream(expectedScreenshotUrl.toFile().getAbsolutePath())) {
            return ImageIO.read(fis);
        } catch (IOException e) {
            throw new ScreenshotException("Unable to parse expected file from: %s. message: %s".formatted(expectedScreenshotUrl, e.getMessage()), e);
        }

    }

    private static void saveNewExpectedScreenshot(BufferedImage img, Path expectedScreenshotUrl) {
        try {
            Files.write(
                    expectedScreenshotUrl.toAbsolutePath(),
                    imageToBytes(img));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static byte[] imageToBytes(BufferedImage image) {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            ImageIO.write(image, "png", outputStream);
            return outputStream.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static BufferedImage takeElementScreenshot(WebElement element) {
        try {
            File screenshot = element.getScreenshotAs(OutputType.FILE);
            return ImageIO.read(screenshot);
        } catch (IOException e) {
            throw new ScreenshotException("Unable to capture screenshot for element", e);
        }
    }

    private static void addAttachment(ScreenDiff screenDiff) {
        try {
            Allure.addAttachment(
                    "Screenshot diff",
                    "application/vnd.allure.image.diff",
                    OM.writeValueAsString(screenDiff)
            );
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

}
