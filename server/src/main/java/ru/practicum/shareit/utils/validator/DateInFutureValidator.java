package ru.practicum.shareit.utils.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import ru.practicum.shareit.utils.validator.annotation.DateInFuture;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DateInFutureValidator implements ConstraintValidator<DateInFuture, String> {
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    @Override
    public boolean isValid(String dateTimeString, ConstraintValidatorContext context) {
        if (dateTimeString == null) {
            return true;
        }
        LocalDateTime dateTime = LocalDateTime.parse(dateTimeString, formatter);
        return !dateTime.isBefore(LocalDateTime.now());
    }
}
