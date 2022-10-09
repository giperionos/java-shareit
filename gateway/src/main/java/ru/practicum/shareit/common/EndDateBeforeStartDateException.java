package ru.practicum.shareit.common;

public class EndDateBeforeStartDateException extends RuntimeException {
    public EndDateBeforeStartDateException(String message) {
        super(message);
    }
}
