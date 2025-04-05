package org.rococo.files.ex;

import lombok.Getter;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;

import java.util.ArrayList;
import java.util.List;

@Getter
public class BadRequestException extends RuntimeException {

    private final List<ObjectError> errors;

    public BadRequestException(BindingResult bindingResult) {
        super("Bad request" +
                (bindingResult.getAllErrors().isEmpty()
                        ? ""
                        : ". Errors: " + bindingResult.getAllErrors()
                ));
        this.errors = bindingResult.getAllErrors();
    }

    public BadRequestException(String message) {
        super(message);
        this.errors = new ArrayList<>();
    }

    public BadRequestException(String message, BindingResult bindingResult) {
        super(message + "\nErrors: " + bindingResult.getAllErrors());
        this.errors = bindingResult.getAllErrors();
    }

}
