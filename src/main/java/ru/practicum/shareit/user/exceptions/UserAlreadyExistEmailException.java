package ru.practicum.shareit.user.exceptions;

public class UserAlreadyExistEmailException extends RuntimeException {
    public UserAlreadyExistEmailException(String message) {
        super(message);
    }
}
