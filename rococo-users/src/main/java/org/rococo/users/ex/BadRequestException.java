package org.rococo.users.ex;

import lombok.Getter;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;

import java.util.List;

@Getter
public class BadRequestException extends RuntimeException {

    private final List<ObjectError> errors;

    public BadRequestException(BindingResult bindingResult) {
        super("Bad request" +
                (bindingResult.getAllErrors().size() > 1
                        ? " . Contains multiple errors"
                        : ""));
        this.errors = bindingResult.getAllErrors();
    }

    public BadRequestException(String message, BindingResult bindingResult) {
        super(message);
        this.errors = bindingResult.getAllErrors();
    }

}
