package org.rococo.auth.service;

import jakarta.validation.ConstraintValidatorContext;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.rococo.auth.model.RegistrationModel;
import org.rococo.auth.validation.validator.EqualPasswordsValidator;

import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
class EqualPasswordsValidatorTest {

    private final EqualPasswordsValidator equalPasswordsValidator = new EqualPasswordsValidator();

    @Test
    void isValidTest(@Mock ConstraintValidatorContext context) {
        RegistrationModel rm = new RegistrationModel(
                "test",
                "qwerty",
                "qwerty"
        );

        assertTrue(equalPasswordsValidator.isValid(rm, context));
    }

}