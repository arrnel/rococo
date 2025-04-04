package org.rococo.auth.validation.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.rococo.auth.validation.StrongPassword;
import org.springframework.stereotype.Component;

@Component
public class StrongPasswordValidator implements ConstraintValidator<StrongPassword, String> {

    private static final String STRONG_PASSWORD_PATTERN = "^(?=.*[a-z])(?=.*[A-Z])(?=.*[\\W_]).{5,20}$";

    @Override
    public boolean isValid(String password, ConstraintValidatorContext context) {
        boolean isValid = password.matches(STRONG_PASSWORD_PATTERN);
        if (!isValid) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(context.getDefaultConstraintMessageTemplate())
                    .addConstraintViolation();
        }
        return isValid;
    }

}
