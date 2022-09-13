package ru.practicum.shareit.booking.exceptions;

public class BookingUnknownStateException extends RuntimeException {
    public BookingUnknownStateException(String message) {
        super(message);
    }
}
