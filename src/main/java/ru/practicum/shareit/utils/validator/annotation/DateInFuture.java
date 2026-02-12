package ru.practicum.shareit.utils.validator.annotation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import ru.practicum.shareit.utils.validator.DateInFutureValidator;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = DateInFutureValidator.class)
public @interface DateInFuture {
    String message() default "Release date must be after {minDate}";
    Class<?>[] groups() default{};
    Class<? extends Payload>[] payload() default {};
}
