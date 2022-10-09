package ru.practicum.shareit.item.exceptions;

public class CommentForNotExistBookingException extends RuntimeException {
    public CommentForNotExistBookingException(String message) {
        super(message);
    }
}
