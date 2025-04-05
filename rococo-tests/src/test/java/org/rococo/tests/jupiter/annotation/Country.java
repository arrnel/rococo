package org.rococo.tests.jupiter.annotation;

import org.rococo.tests.enums.CountryCode;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.PARAMETER})
public @interface Country {

    /**
     * ISO 3166-1 alpha-2 code. Example: AB, AD, AE, ..., ZW
     */
    CountryCode code() default CountryCode.EMPTY;

}
