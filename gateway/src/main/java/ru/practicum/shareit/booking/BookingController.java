package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.common.Create;
import ru.practicum.shareit.common.StartDateBeforeEndDateValidator;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Validated
public class BookingController {

    private final BookingClient bookingClient;

    @PostMapping
    ResponseEntity<Object> createBooking(@RequestHeader("X-Sharer-User-Id") Long userId,
                                         @Validated(Create.class) @RequestBody BookingCreateDto bookingCreateDto) {
        StartDateBeforeEndDateValidator.validate(bookingCreateDto.getStart(), bookingCreateDto.getEnd());
        return bookingClient.createBooking(bookingCreateDto, userId);
    }

    @PatchMapping("/{bookingId}")
    ResponseEntity<Object> resolveBooking(@RequestHeader("X-Sharer-User-Id") Long userId,
                                       @PathVariable Long bookingId,
                                       @RequestParam Boolean approved) {
        return bookingClient.resolveBooking(bookingId, userId, approved);
    }

    @GetMapping("/{bookingId}")
    ResponseEntity<Object> getBookingDetailInfoById(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                @PathVariable Long bookingId) {
        return bookingClient.getBookingDetailInfoById(bookingId, userId);
    }

    @GetMapping
    ResponseEntity<Object> getAllBookingsByUserIdAndState(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                            @RequestParam(name = "state", defaultValue = "ALL") String stateStr,
                                                            @PositiveOrZero @RequestParam(name = "from", defaultValue = "0")  Integer from,
                                                            @Positive @RequestParam(name = "size", defaultValue = "10")  Integer size) {
        return bookingClient.getAllBookingsByUserIdAndState(userId, stateStr, from, size);
    }

    @GetMapping("/owner")
    ResponseEntity<Object> getAllBookingsByOwnerIdAndState(@RequestHeader("X-Sharer-User-Id") Long ownerId,
                                                             @RequestParam(name = "state", defaultValue = "ALL") String stateStr,
                                                             @PositiveOrZero @RequestParam(name = "from", defaultValue = "0")  Integer from,
                                                             @Positive @RequestParam(name = "size", defaultValue = "10")  Integer size) {
        return bookingClient.getAllBookingsByOwnerIdAndState(ownerId, stateStr, from, size);
    }
}
