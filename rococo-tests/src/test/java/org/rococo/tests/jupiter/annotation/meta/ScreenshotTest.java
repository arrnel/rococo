package org.rococo.tests.jupiter.annotation.meta;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.rococo.tests.jupiter.extension.ScreenshotTestExtension;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Test
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@ExtendWith(ScreenshotTestExtension.class)
public @interface ScreenshotTest {

    String value();

    boolean rewriteExpected() default false;

}