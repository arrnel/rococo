package org.rococo.tests.jupiter.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.PARAMETER})
public @interface Painting {

    String title() default "";

    String description() default "";

    boolean descriptionEmpty() default false;

    Artist artist() default @Artist();

    Museum museum() default @Museum();

    String pathToPhoto() default "";

}
