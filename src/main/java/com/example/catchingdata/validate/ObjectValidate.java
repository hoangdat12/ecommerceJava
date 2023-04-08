package com.example.catchingdata.validate;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;
@Component
public class ObjectValidate<T> {
    private final ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
    private final Validator validator = validatorFactory.getValidator();

    public Set<String> validate(T objectValidate) {
        Set<ConstraintViolation<T>> violations = validator.validate(objectValidate);
        Set<String> messages = new HashSet<>();
        for (ConstraintViolation<T> violation : violations) {
            messages.add(violation.getMessage());
        }
        return messages;
    }
}

