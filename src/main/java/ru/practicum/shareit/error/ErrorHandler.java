package ru.practicum.shareit.error;

import jakarta.validation.ValidationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.shareit.exceptions.*;

@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorInfo handleNotFound(final NotFoundException e) {
        return new ErrorInfo(e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    public ErrorInfo handleBadConditions(final ConditionsNotMetException e) {
        return new ErrorInfo(e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorInfo handleConflictData(final ConflictDataException e) {
        return new ErrorInfo(e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ErrorInfo handleUserNotOwner(final UserNotOwnerException e) {
        return new ErrorInfo(e.getMessage());
    }

//    @ExceptionHandler
//    @ResponseStatus(HttpStatus.BAD_REQUEST)
//    public ErrorInfo handleIncorrectParameter(final IncorrectParameterException e) {
//        return new ErrorInfo(String.format("Параметр %s имеет недопустимое значение: %s", e.getParameterName(), e.getParameterValue()));
//    }
//
//    @ExceptionHandler
//    @ResponseStatus(HttpStatus.NOT_FOUND)
//    public ErrorInfo handleFilmLike(final FilmLikeException e) {
//        return new ErrorInfo(String.format("Для фильма с id %d не удается поставить/убрать лайк от пользователя с id %d", e.getFilmId(), e.getUserId()));
//    }
//
//    @ExceptionHandler
//    @ResponseStatus(HttpStatus.OK) // WTF?
//    public ErrorInfo handleFriendship(final FriendshipException e) {
//        return new ErrorInfo(e.getMessage());
//    }

//    @ExceptionHandler
//    @ResponseStatus(HttpStatus.BAD_REQUEST)
//    public ErrorInfo handleValidation(final ValidationException e) {
//        return new ErrorInfo(e.getMessage());
//    }

}
