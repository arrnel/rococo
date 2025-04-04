package org.rococo.auth.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import org.rococo.auth.validation.validator.StrongPasswordValidator;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = {StrongPasswordValidator.class})
public @interface StrongPassword {

    String message() default "Password: 5-20 chars, at least one uppercase, one lowercase, and one special char required.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}
