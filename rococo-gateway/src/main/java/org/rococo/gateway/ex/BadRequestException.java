package org.rococo.gateway.ex;

import lombok.Getter;
import org.springframework.validation.FieldError;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

@Getter
@ParametersAreNonnullByDefault
public class BadRequestException extends RuntimeException {

    private final List<FieldError> errors;

    public BadRequestException(final List<FieldError> fieldErrors) {
        super("Bad request. Contains errors: " + fieldErrors);
        this.errors = fieldErrors;
    }

}
