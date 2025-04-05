package org.rococo.gateway.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import org.rococo.gateway.validation.validator.ColumnsValidator;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER, TYPE_USE})
@Constraint(validatedBy = ColumnsValidator.class)
public @interface Columns {

    String message() default "Invalid columns";

    boolean empty() default true;

    String[] value() default {};

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}
