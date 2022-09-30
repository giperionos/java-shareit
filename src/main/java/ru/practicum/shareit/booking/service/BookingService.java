package ru.practicum.shareit.booking.service;

import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.booking.BookingState;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingFullInfoDto;
import ru.practicum.shareit.booking.dto.BookingItemOwnerDto;

import java.util.List;

public interface BookingService {
    BookingCreateDto createBooking(BookingCreateDto bookingCreateDto, Long userId);

    BookingItemOwnerDto resolveBooking(Long bookingId, Long userId, Boolean approved);

    BookingFullInfoDto getBookingDetailInfoById(Long bookingId, Long userId);

    List<BookingFullInfoDto> getAllBookingsByUserIdAndState(Long userId, BookingState state, PageRequest pageRequest);

    List<BookingFullInfoDto> getAllBookingsByOwnerIdAndState(Long ownerId, BookingState state, PageRequest pageRequest);
}
