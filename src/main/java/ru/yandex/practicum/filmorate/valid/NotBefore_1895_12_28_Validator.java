package ru.yandex.practicum.filmorate.valid;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDate;

public class NotBefore_1895_12_28_Validator implements ConstraintValidator<NotBefore_1895_12_28, LocalDate> {
    @Override
    public boolean isValid(LocalDate value, ConstraintValidatorContext context) {
        if (value != null) {
            return !value.isBefore(LocalDate.of(1895, 12, 28));
        }
        return true;
    }
}