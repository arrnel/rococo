package org.rococo.auth.model;

import jakarta.validation.constraints.NotBlank;
import org.rococo.auth.validation.EqualPasswords;
import org.rococo.auth.validation.StrongPassword;
import org.rococo.auth.validation.Username;

@EqualPasswords
public record RegistrationModel(

        @NotBlank(message = "Username can not be blank")
        @Username
        String username,

        @NotBlank(message = "Password can not be blank")
        @StrongPassword
        String password,

        String passwordSubmit) {
}
