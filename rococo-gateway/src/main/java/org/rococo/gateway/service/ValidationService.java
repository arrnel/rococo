package org.rococo.gateway.service;

import lombok.RequiredArgsConstructor;
import org.rococo.gateway.ex.BadRequestException;
import org.springframework.stereotype.Service;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.Validator;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@ParametersAreNonnullByDefault
public class ValidationService {

    private final Validator validator;

    public void validateObject(Object object, String objectName) {

        final BindingResult bindingResult = new BeanPropertyBindingResult(object, objectName);
        validator.validate(object, bindingResult);

        if (bindingResult.hasErrors())
            throw new BadRequestException(bindingResult.getFieldErrors());

    }

    public void throwBadRequestExceptionIfErrorsExist(BindingResult... bindingResults) {

        List<FieldError> errors = new ArrayList<>();
        Stream.of(bindingResults)
                .forEach(bindingResult ->
                        errors.addAll(bindingResult.getFieldErrors()));

        if (!errors.isEmpty())
            throw new BadRequestException(errors);

    }

}
