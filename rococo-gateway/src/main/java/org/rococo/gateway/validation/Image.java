package org.rococo.gateway.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import org.rococo.gateway.validation.validator.ImageValidator;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER, TYPE_USE})
@Constraint(validatedBy = ImageValidator.class)
public @interface Image {

    String message() default "Invalid image";

    /**
     * Size in MB
     */
    int maxSize() default 5;

    String[] formats() default {"jpg", "jpeg", "png"};

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}
