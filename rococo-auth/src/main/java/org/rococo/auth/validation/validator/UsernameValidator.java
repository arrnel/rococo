package org.rococo.auth.validation.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.rococo.auth.validation.Username;

public class UsernameValidator implements ConstraintValidator<Username, String> {

    private static final String USERNAME_PATTERN = "^(?=[a-z0-9._-]{6,30}$)(?=[a-z0-9])(?:[a-z0-9](?:[._-](?![._-])|[a-z0-9])*)?[a-z0-9]$";

    @Override
    public boolean isValid(String username, ConstraintValidatorContext context) {
        boolean isValid = username.matches(USERNAME_PATTERN);
        if (!isValid) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(context.getDefaultConstraintMessageTemplate())
//                    .addPropertyNode("username")
                    .addConstraintViolation();
        }
        return isValid;
    }

}
