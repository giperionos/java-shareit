package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingFullInfoDto;
import ru.practicum.shareit.booking.dto.BookingItemOwnerDto;
import ru.practicum.shareit.booking.service.BookingService;
import java.util.List;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    BookingCreateDto createBooking(@RequestHeader("X-Sharer-User-Id") Long userId,
                                   @RequestBody BookingCreateDto bookingCreateDto) {
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
                                                            @RequestParam(name = "state", defaultValue = "ALL") String stateStr,
                                                            @RequestParam(name = "from", defaultValue = "0")  Integer from,
                                                            @RequestParam(name = "size", defaultValue = "10")  Integer size) {
        return bookingService.getAllBookingsByUserIdAndState(userId, stateStr, from, size);
    }

    @GetMapping("/owner")
    List<BookingFullInfoDto> getAllBookingsByOwnerIdAndState(@RequestHeader("X-Sharer-User-Id") Long ownerId,
                                                             @RequestParam(name = "state", defaultValue = "ALL") String stateStr,
                                                             @RequestParam(name = "from", defaultValue = "0")  Integer from,
                                                             @RequestParam(name = "size", defaultValue = "10")  Integer size) {
        return bookingService.getAllBookingsByOwnerIdAndState(ownerId, stateStr, from, size);
    }
}
