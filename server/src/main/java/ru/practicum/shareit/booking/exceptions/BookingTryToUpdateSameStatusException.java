package ru.practicum.shareit.booking.exceptions;

public class BookingTryToUpdateSameStatusException extends RuntimeException {
    public BookingTryToUpdateSameStatusException(String message) {
        super(message);
    }
}
