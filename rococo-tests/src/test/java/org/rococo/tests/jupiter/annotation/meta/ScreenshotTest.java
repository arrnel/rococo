package org.rococo.tests.jupiter.annotation.meta;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.rococo.tests.jupiter.extension.ScreenshotTestExtension;
import org.rococo.tests.model.allure.AllureTag;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Tag(AllureTag.SCREENSHOT_TEST)
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@ExtendWith(ScreenshotTestExtension.class)
public @interface ScreenshotTest {

    String value();

    boolean rewriteExpected() default false;

}