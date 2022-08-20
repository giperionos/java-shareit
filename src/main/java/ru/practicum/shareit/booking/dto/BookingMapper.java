package ru.practicum.shareit.booking.dto;

import ru.practicum.shareit.booking.Booking;

public class BookingMapper {

    public static BookingDto toBookingDto(Booking booking) {
        return new BookingDto(
            booking.getId(),
            booking.getStart(),
            booking.getEnd(),
            booking.getItemId(),
            booking.getStatus()
        );
    }

    public static Booking toBooking(Long userId, BookingDto bookingDto) {
        return new Booking(
            bookingDto.getId(),
            bookingDto.getStart(),
            bookingDto.getEnd(),
            bookingDto.getItemId(),
            userId,
            bookingDto.getStatus()
        );
    }
}
