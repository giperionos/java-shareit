package ru.practicum.shareit.user.exceptions;

public class UserUnknownException extends RuntimeException {
    public UserUnknownException(String message) {
        super(message);
    }
}
