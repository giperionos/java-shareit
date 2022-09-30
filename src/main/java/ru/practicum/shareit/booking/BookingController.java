package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingFullInfoDto;
import ru.practicum.shareit.booking.dto.BookingItemOwnerDto;
import ru.practicum.shareit.booking.exceptions.BookingUnknownStateException;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.common.Create;
import ru.practicum.shareit.common.StartDateBeforeEndDateValidator;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Validated
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
                                                            @RequestParam(name = "state", defaultValue = "ALL") String stateStr,
                                                            @PositiveOrZero @RequestParam(name = "from", defaultValue = "0")  Integer from,
                                                            @Positive @RequestParam(name = "size", defaultValue = "10")  Integer size) {
        BookingState state = BookingState.from(stateStr)
                .orElseThrow(() -> new BookingUnknownStateException(String.format("Unknown state: %s", stateStr)));

        int page = from / size;
        final PageRequest pageRequest = PageRequest.of(page, size, Sort.by("start").descending());

        return bookingService.getAllBookingsByUserIdAndState(userId, state, pageRequest);
    }

    @GetMapping("/owner")
    List<BookingFullInfoDto> getAllBookingsByOwnerIdAndState(@RequestHeader("X-Sharer-User-Id") Long ownerId,
                                                             @RequestParam(name = "state", defaultValue = "ALL") String stateStr,
                                                             @PositiveOrZero @RequestParam(name = "from", defaultValue = "0")  Integer from,
                                                             @Positive @RequestParam(name = "size", defaultValue = "10")  Integer size) {

        BookingState state = BookingState.from(stateStr)
                .orElseThrow(() -> new BookingUnknownStateException(String.format("Unknown state: %s", stateStr)));

        int page = from / size;
        final PageRequest pageRequest = PageRequest.of(page, size, Sort.by("start").descending());

        return bookingService.getAllBookingsByOwnerIdAndState(ownerId, state, pageRequest);
    }
}
