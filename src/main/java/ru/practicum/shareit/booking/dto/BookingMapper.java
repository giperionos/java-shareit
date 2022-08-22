package ru.practicum.shareit.booking.dto;

import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UserMapper;

public class BookingMapper {

    public static BookingDto toBookingDto(Booking booking) {
        return new BookingDto(
            booking.getId(),
            booking.getStart(),
            booking.getEnd(),
            ItemMapper.toItemDto(booking.getItem()),
            UserMapper.toUserDto(booking.getBooker()),
            booking.getStatus()
        );
    }

    public static Booking toBooking(User user, BookingDto bookingDto) {
        return new Booking(
            bookingDto.getId(),
            bookingDto.getStart(),
            bookingDto.getEnd(),
            ItemMapper.toItem(bookingDto.getItem(), user),
            UserMapper.toUser(bookingDto.getBooker()),
            bookingDto.getStatus()
        );
    }
}
