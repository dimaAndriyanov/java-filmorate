package ru.yandex.practicum.filmorate.valid;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDate;

public class NotTooOldValidator implements ConstraintValidator<NotTooOld, LocalDate> {
    @Override
    public boolean isValid(LocalDate value, ConstraintValidatorContext context) {
        if (value != null) {
            return !value.isBefore(LocalDate.of(1895, 12, 28));
        }
        return true;
    }
}