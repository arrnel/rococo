package org.rococo.tests.jupiter.extension;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.qameta.allure.Allure;
import lombok.SneakyThrows;
import org.junit.jupiter.api.extension.*;
import org.junit.platform.commons.support.AnnotationSupport;
import org.rococo.tests.config.Config;
import org.rococo.tests.ex.ScreenshotException;
import org.rococo.tests.jupiter.annotation.meta.ScreenshotTest;
import org.rococo.tests.model.allure.ScreenDiff;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Base64;

public class ScreenshotTestExtension implements ParameterResolver, TestExecutionExceptionHandler {

    public static final ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace.create(ScreenshotTestExtension.class);
    private static final Config CFG = Config.getInstance();
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final Base64.Encoder encoder = Base64.getEncoder();

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return AnnotationSupport.isAnnotated(extensionContext.getRequiredTestMethod(), ScreenshotTest.class) &&
                parameterContext.getParameter().getType().isAssignableFrom(BufferedImage.class);
    }

    @SneakyThrows
    @Override
    public BufferedImage resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        ScreenshotTest screenshotAnno = extensionContext.getRequiredTestMethod().getAnnotation(ScreenshotTest.class);
        String imagePath = CFG.screenshotBaseDir() + screenshotAnno.value();
        try (FileInputStream fis = new FileInputStream(imagePath)) {
            return ImageIO.read(fis);
        } catch (IOException e) {
            throw new ScreenshotException("Unable to parse expected file from: %s. Message: %s".formatted(imagePath, e.getMessage()), e);
        }
    }

    @Override
    public void handleTestExecutionException(ExtensionContext context, Throwable throwable) throws Throwable {
        ScreenshotTest screenShotTest = context.getRequiredTestMethod().getAnnotation(ScreenshotTest.class);
        if (throwable.getMessage().contains("Screen comparison failure")) {

            if (CFG.rewriteAllImages() || screenShotTest.rewriteExpected()) {
                BufferedImage actual = getActual();
                if (actual != null) {
                    ImageIO.write(actual, "png", new File(CFG.screenshotBaseDir() + screenShotTest.value()));
                }
            }

            ScreenDiff screenDif = ScreenDiff.builder()
                    .expected("data:image/png;base64," + encoder.encodeToString(imageToBytes(getExpected())))
                    .actual("data:image/png;base64," + encoder.encodeToString(imageToBytes(getActual())))
                    .diff("data:image/png;base64," + encoder.encodeToString(imageToBytes(getDiff())))
                    .build();

            Allure.addAttachment(
                    "Screenshot diff",
                    "application/vnd.allure.image.diff",
                    objectMapper.writeValueAsString(screenDif)
            );
        }

        throw throwable;
    }

    public static void setExpected(BufferedImage expected) {
        TestMethodContextExtension.context().getStore(NAMESPACE).put("expected", expected);
    }

    public static BufferedImage getExpected() {
        return TestMethodContextExtension.context().getStore(NAMESPACE).get("expected", BufferedImage.class);
    }

    public static void setActual(BufferedImage actual) {
        TestMethodContextExtension.context().getStore(NAMESPACE).put("actual", actual);
    }

    public static BufferedImage getActual() {
        return TestMethodContextExtension.context().getStore(NAMESPACE).get("actual", BufferedImage.class);
    }

    public static void setDiff(BufferedImage diff) {
        TestMethodContextExtension.context().getStore(NAMESPACE).put("diff", diff);
    }

    public static BufferedImage getDiff() {
        return TestMethodContextExtension.context().getStore(NAMESPACE).get("diff", BufferedImage.class);
    }

    private static byte[] imageToBytes(BufferedImage image) {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            ImageIO.write(image, "png", outputStream);
            return outputStream.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
