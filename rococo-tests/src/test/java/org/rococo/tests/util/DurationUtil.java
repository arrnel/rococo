package org.rococo.tests.util;

public class DurationUtil {

    public static String milliToGolangDuration(int milliseconds) {
        int hours = milliseconds / 3_600_000;
        int minutes = (milliseconds - (hours * 3_600_000)) / 60_000;
        int seconds = (milliseconds - (hours * 3_600_000) - (minutes * 60_000)) / 1_000;
        return "%s%s%s".formatted(
                hours > 0
                        ? hours + "h"
                        : "",
                minutes > 0
                        ? minutes + "m"
                        : "",
                seconds > 0
                        ? seconds + "s"
                        : ""
        );

    }

}
