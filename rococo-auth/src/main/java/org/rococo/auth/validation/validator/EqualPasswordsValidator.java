package org.rococo.auth.validation.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.rococo.auth.model.RegistrationModel;
import org.rococo.auth.validation.EqualPasswords;

public class EqualPasswordsValidator implements ConstraintValidator<EqualPasswords, RegistrationModel> {
    @Override
    public boolean isValid(RegistrationModel form, ConstraintValidatorContext context) {
        boolean isValid = form.password().equals(form.passwordSubmit());
        if (!isValid) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(context.getDefaultConstraintMessageTemplate())
                    .addPropertyNode("passwordSubmit")
                    .addConstraintViolation();
        }
        return isValid;
    }
}
