package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingFullInfoDto;
import ru.practicum.shareit.booking.dto.BookingItemOwnerDto;
import ru.practicum.shareit.booking.exceptions.BookingUnknownStateException;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.common.Create;
import ru.practicum.shareit.common.StartDateBeforeEndDateValidator;

import java.util.List;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    BookingCreateDto createBooking(@RequestHeader("X-Sharer-User-Id") Long userId,
                                   @Validated(Create.class) @RequestBody BookingCreateDto bookingCreateDto) {
        StartDateBeforeEndDateValidator.validate(bookingCreateDto.getStart(), bookingCreateDto.getEnd());
        return bookingService.createBooking(bookingCreateDto, userId);
    }

    @PatchMapping("/{bookingId}")
    BookingItemOwnerDto resolveBooking(@RequestHeader("X-Sharer-User-Id") Long userId,
                                       @PathVariable Long bookingId,
                                       @RequestParam Boolean approved) {
        return bookingService.resolveBooking(bookingId, userId, approved);
    }

    @GetMapping("/{bookingId}")
    BookingFullInfoDto getBookingDetailInfoById(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                @PathVariable Long bookingId) {
        return bookingService.getBookingDetailInfoById(bookingId, userId);
    }

    @GetMapping
    List<BookingFullInfoDto> getAllBookingsByUserIdAndState(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                            @RequestParam(name = "state", defaultValue = "ALL") String stateStr) {
        BookingState state = BookingState.from(stateStr)
                .orElseThrow(() -> new BookingUnknownStateException(String.format("Unknown state: %s", stateStr)));
        return bookingService.getAllBookingsByUserIdAndState(userId, state);
    }

    @GetMapping("/owner")
    List<BookingFullInfoDto> getAllBookingsByOwnerIdAndState(@RequestHeader("X-Sharer-User-Id") Long ownerId,
                                                             @RequestParam(name = "state", defaultValue = "ALL") String stateStr) {
        BookingState state = BookingState.from(stateStr)
                .orElseThrow(() -> new BookingUnknownStateException(String.format("Unknown state: %s", stateStr)));
        return bookingService.getAllBookingsByOwnerIdAndState(ownerId, state);
    }
}
