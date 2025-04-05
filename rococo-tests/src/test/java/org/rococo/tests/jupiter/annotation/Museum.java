package org.rococo.tests.jupiter.annotation;

import org.rococo.tests.enums.CountryCode;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.PARAMETER})
public @interface Museum {

    String title() default "";

    String description() default "";

    boolean descriptionEmpty() default false;

    String city() default "";

    CountryCode countryCode() default CountryCode.EMPTY;

    String pathToPhoto() default "";

}
