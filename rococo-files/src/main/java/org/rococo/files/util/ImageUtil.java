package org.rococo.files.util;

import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.imageio.ImageIO;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Base64;
import java.util.Optional;

@Slf4j
@ParametersAreNonnullByDefault
public class ImageUtil {

    private ImageUtil() {
    }

    @Nonnull
    public static Optional<byte[]> resizeImage(String originalPhoto,
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

                    return Optional.of(
                            concatArrays(
                                    "data:image/%s;base64,".formatted(format)
                                            .getBytes(StandardCharsets.UTF_8),
                                    Base64.getEncoder().encode(os.toByteArray())
                            ));
                }
            } catch (Exception e) {
                log.error("Error while resizing image");
                throw new RuntimeException(e);
            }
        }

        return Optional.empty();

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

}
