package org.rococo.tests.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.qameta.allure.Allure;
import org.rococo.tests.model.allure.ScreenDiff;

public class AllureUtil {

    private static final ObjectMapper OM = new ObjectMapper();

    public static void attachScreenDiff(ScreenDiff screenDiff) {
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
