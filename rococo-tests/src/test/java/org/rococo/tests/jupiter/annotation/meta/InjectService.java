package org.rococo.tests.jupiter.annotation.meta;

import org.rococo.tests.enums.ServiceType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.CONSTRUCTOR, ElementType.FIELD})
public @interface InjectService {

    ServiceType value() default ServiceType.DEFAULT;

}
