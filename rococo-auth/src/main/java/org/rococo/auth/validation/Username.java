package org.rococo.auth.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import org.rococo.auth.validation.validator.UsernameValidator;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = {UsernameValidator.class})
public @interface Username {

    String message() default "Username: 6-30 chars, lowercase letters, digits, [\"_\", \"-\", \".\"]. Must start/end with a letter or digit and no consecutive special chars.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}
