package ru.practicum.shareit.booking.dto;

import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UserMapper;

public class BookingMapper {

    public static BookingCreateDto toBookingDto(Booking booking) {
        return new BookingCreateDto(
            booking.getId(),
            booking.getStart(),
            booking.getEnd(),
            booking.getItem().getId(),
            booking.getBooker().getId()
        );
    }

    public static Booking toBooking(BookingCreateDto bookingCreateDto, Item item, User user) {
        return new Booking(
            bookingCreateDto.getId(),
            bookingCreateDto.getStart(),
            bookingCreateDto.getEnd(),
            item,
            user,
            BookingStatus.WAITING
        );
    }

    public static BookingItemOwnerDto toItemOwnerBookingDto(Booking booking) {
        return new BookingItemOwnerDto(
            booking.getId(),
            booking.getStatus(),
            UserMapper.toUserDto(booking.getBooker()),
            ItemMapper.toItemDto(booking.getItem())
        );
    }

    public static BookingFullInfoDto toFullInfoBookingDto(Booking booking) {
        return new BookingFullInfoDto(
            booking.getId(),
            booking.getStart(),
            booking.getEnd(),
            booking.getStatus(),
            UserMapper.toUserDto(booking.getBooker()),
            ItemMapper.toItemDto(booking.getItem())
        );
    }
}
