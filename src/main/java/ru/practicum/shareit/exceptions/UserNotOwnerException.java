package ru.practicum.shareit.exceptions;

public class UserNotOwnerException extends RuntimeException {
    public UserNotOwnerException(String message) {
        super(message);
    }
}
