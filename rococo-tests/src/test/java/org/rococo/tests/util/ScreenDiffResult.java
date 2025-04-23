package org.rococo.tests.util;

import io.qameta.allure.Allure;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import ru.yandex.qatools.ashot.comparison.ImageDiff;
import ru.yandex.qatools.ashot.comparison.ImageDiffer;

import javax.annotation.ParametersAreNonnullByDefault;
import java.awt.image.BufferedImage;
import java.util.function.BooleanSupplier;

@Slf4j
@ParametersAreNonnullByDefault
public class ScreenDiffResult implements BooleanSupplier {

    private static final double MAX_PERCENT = 0.2;

    private final BufferedImage actual;
    private final double percent;
    private final boolean hasDiff;

    @Getter
    private final ImageDiff diff;


    public ScreenDiffResult(BufferedImage expected, BufferedImage actual, double percent) {

        if (percent < 0.0 || percent > MAX_PERCENT)
            throw new IllegalArgumentException("Illegal percent value. Allowed between [0, %f]".formatted(MAX_PERCENT));

        this.actual = actual;
        this.percent = percent;
        this.diff = new ImageDiffer().makeDiff(expected, actual);
        this.hasDiff = hasDiff();

    }

    private boolean hasDiff() {

        int totalPixels = actual.getWidth() * actual.getHeight();
        long expectedDiffSize = Math.round(totalPixels * percent);

        int maxDiffPixels = (int) Math.round(totalPixels * percent);
        var hasDiff = diff.getDiffSize() > maxDiffPixels;

        if (hasDiff) {
            double diffPercent = (double) diff.getDiffSize() / totalPixels;

            String table = buildDiffTable(expectedDiffSize, percent, diff.getDiffSize(), diffPercent);
            log.warn("\n{}", table);
            Allure.addAttachment("screen diff data", table);
        }
        return hasDiff;
    }

    private String buildDiffTable(long expectedDiffSize, double expectedDiffPercent, long actualDiffSize, double actualDiffPercent) {
        String separator = "+" + "-".repeat(16) + "+" + "-".repeat(14) + "+" + "-".repeat(14) + "+";
        String format = "|%-" + 16 + "s|%-" + 14 + "s|%-" + 14 + "s|\n";

        StringBuilder table = new StringBuilder();

        // Header
        table.append(separator).append("\n")
                .append("|         Image difference detected             |\n")
                .append(separator).append("\n")
                .append("|                 |   Expected   |    Actual    |\n")
                .append(separator).append("\n");
        // Data rows
        appendRow(table, format, "diff_size", String.valueOf(expectedDiffSize), actualDiffSize);
        appendRow(table, format, "diff_percent", expectedDiffPercent, String.format("%.3f", actualDiffPercent));

        table.append(separator);
        return table.toString();
    }

    private <T> void appendRow(StringBuilder table, String format, String label, T expected, T actual) {
        table.append(String.format(format, label, expected, actual));
    }

    @Override
    public boolean getAsBoolean() {
        return hasDiff;
    }

}