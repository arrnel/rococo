package org.rococo.tests.util;

import com.google.common.net.MediaType;
import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;
import org.apache.commons.io.FilenameUtils;
import org.openqa.selenium.InvalidArgumentException;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.Random;

@Slf4j
@ParametersAreNonnullByDefault
public class ImageUtil {

    private ImageUtil() {
    }

    private static final Random RANDOM = new Random();

    @Nonnull
    public static byte[] resizeImage(String originalPhoto,
                                     String format,
                                     int targetWidth,
                                     int targetHeight,
                                     double quality
    ) {

        if (!originalPhoto.isEmpty()) {

            try {
                String base64Image = originalPhoto.split(",")[1];

                try (ByteArrayInputStream is = new ByteArrayInputStream(Base64.getDecoder().decode(base64Image));
                     ByteArrayOutputStream os = new ByteArrayOutputStream()) {

                    Thumbnails.of(ImageIO.read(is))
                            .width(targetWidth)
                            .height(targetHeight)
                            .outputQuality(quality)
                            .outputFormat(format)
                            .toOutputStream(os);

                    return concatArrays(
                            "data:image/%s;base64,".formatted(format)
                                    .getBytes(StandardCharsets.UTF_8),
                            Base64.getEncoder().encode(os.toByteArray())
                    );

                }
            } catch (Exception e) {
                log.error("Error while resizing image");
                throw new RuntimeException(e);
            }
        }

        return new byte[0];

    }

    public static String generateImage() {
        var width = RANDOM.nextInt(800) + 400;
        var height = RANDOM.nextInt(800) + 400;
        return generateImage(width, height, "png");
    }

    public static String generateImage(int width, int height, String format) {

        validateFormat(format);

        var color1 = randomColor();
        var color2 = randomColor();

        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = image.createGraphics();

        boolean isVertical = new Random().nextBoolean();
        float delta = (new Random().nextFloat(4) + 3) / 10;
        log.debug("META: width = [{}], height = [{}], delta = [{}]", width, height, delta);

        GradientPaint gradient = new GradientPaint(
                0,
                0,
                color1,
                isVertical
                        ? width
                        : width * delta,
                isVertical
                        ? height * delta
                        : height,
                color2);

        g2d.setPaint(gradient);
        g2d.fillRect(0, 0, width, height);

        g2d.dispose();
        return "data:image/%s;base64,".formatted(format) + convertBufferedImageToBase64String(image, format);

    }

    @Nonnull
    public static String imageFileToBase64(Path path) {

        var extension = FilenameUtils.getExtension(path.getFileName().toString()).toLowerCase();
        MediaType mediaType = MediaType.parse("image/" + extension);

        try {
            byte[] fileBytes = Files.readAllBytes(path);
            String base64String = Base64.getEncoder().encodeToString(fileBytes);
            return "data:%s;base64,%s".formatted(mediaType, base64String);
        } catch (IOException e) {
            throw new RuntimeException("Failed to read file: " + path, e);
        }
    }

    @Nonnull
    private static byte[] concatArrays(byte[] first,
                                       byte[] second
    ) {
        byte[] result = Arrays.copyOf(first, first.length + second.length);
        System.arraycopy(second, 0, result, first.length, second.length);
        return result;
    }

    @Nonnull
    public static String getFormatName(final String imageData) {
        return imageData.substring(0, imageData.indexOf(";"))
                .substring(imageData.indexOf("/") + 1);
    }

    private static void validateFormat(String format) {
        var availableFormats = List.of("jpg", "jpeg", "png");
        if (!availableFormats.contains(format))
            throw new InvalidArgumentException("Unsupported format: %s. Available formats: %s".formatted(format, availableFormats.toString()));
    }

    private static String convertBufferedImageToBase64String(BufferedImage image, String format) {
        try (ByteArrayOutputStream os = new ByteArrayOutputStream()) {
            ImageIO.write(image, format, os);
            return Base64.getEncoder().encodeToString(os.toByteArray());
        } catch (IOException ioe) {
            throw new UncheckedIOException(ioe);
        }
    }

    private static Color randomColor() {

        return new Color(
                RANDOM.nextInt(255),
                RANDOM.nextInt(255),
                RANDOM.nextInt(255)
        );
    }

}
