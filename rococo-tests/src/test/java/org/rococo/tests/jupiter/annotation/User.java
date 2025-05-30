package org.rococo.tests.jupiter.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface User {

    String username() default "";

    String password() default "";

    String firstName() default "";

    String lastName() default "";

    String pathToPhoto() default "";

}
