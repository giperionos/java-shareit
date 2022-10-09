package ru.practicum.shareit.requests.exceptions;

public class ItemRequestUnknownException extends RuntimeException {
    public ItemRequestUnknownException(String message) {
        super(message);
    }
}
