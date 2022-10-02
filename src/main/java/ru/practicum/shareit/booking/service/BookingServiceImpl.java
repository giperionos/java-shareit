package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingState;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.dto.BookingFullInfoDto;
import ru.practicum.shareit.booking.dto.BookingItemOwnerDto;
import ru.practicum.shareit.booking.exceptions.*;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.exceptions.ItemUnavailableException;
import ru.practicum.shareit.item.exceptions.ItemUnknownException;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.exceptions.UserUnknownException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Override
    public BookingCreateDto createBooking(BookingCreateDto bookingCreateDto, Long userId) {
        //сначала проверить, что такой пользователь есть
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserUnknownException(String.format("Пользователь с %d не найден.", userId)));

        //также проверить, что такая вещь есть
        //сначала проверить, что такая вещь вообще есть
        Item foundedItem = itemRepository.findById(bookingCreateDto.getItemId())
                .orElseThrow(() -> new ItemUnknownException(
                        String.format("Не найдена вещь с id = %d", bookingCreateDto.getItemId()))
                );

        //и отдельно проверить, что она доступна - по мотивам postman-тестов
        if (!foundedItem.getAvailable()) {
            throw new ItemUnavailableException(String.format("Вещь с id = %d не доступна!", bookingCreateDto.getItemId()));
        }

        //Проверить, что пользователь, который бронирует не является владельцем этой вещи,
        //иначе получается, что он бронирует сам у себя
        if (foundedItem.getOwner().getId().longValue() == userId.longValue()) {
            throw new BookingHimSelfException(String.format("Пользователь с id = %s пытается забронировать сам у себя.", userId));
        }

        //само бронирование
        Booking booking = BookingMapper.toBooking(bookingCreateDto, foundedItem, user);

        return BookingMapper.toBookingDto(bookingRepository.save(booking));
    }

    @Override
    public BookingItemOwnerDto resolveBooking(Long bookingId, Long userId, Boolean approved) {
        //проверить, что бронирование с таким id есть
        Booking currentBooking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new BookingUnknownException(
                        String.format("Не найдена бронь с id = %d", bookingId))
                );

        //проверить, что такой пользователь есть
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserUnknownException(String.format("Пользователь с %d не найден.", userId)));

        //проверить, что эта вещь принадлежит этому пользователю
        if (currentBooking.getItem().getOwner().getId().longValue() != user.getId().longValue()) {
            throw new BookingSecurityException(String.format("Пользователь с id = %d не может работать с вещью с id = %d",
                    user.getId(), currentBooking.getItem().getId()));
        }

        BookingStatus newStatus = approved == Boolean.TRUE ? BookingStatus.APPROVED : BookingStatus.REJECTED;

        if (currentBooking.getStatus().equals(newStatus)) {
            throw new BookingTryToUpdateSameStatusException(
                String.format("Бронирование с id = %s уже имеет статус %s", bookingId, newStatus)
            );
        }

        currentBooking.setStatus(newStatus);
        bookingRepository.save(currentBooking);
        return BookingMapper.toItemOwnerBookingDto(currentBooking);
    }

    @Override
    public BookingFullInfoDto getBookingDetailInfoById(Long bookingId, Long userId) {

        //проверить, что бронирование с таким id есть
        Booking currentBooking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new BookingUnknownException(
                        String.format("Не найдена бронь с id = %d", bookingId))
                );

        //проверить, что такой пользователь есть
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserUnknownException(String.format("Пользователь с %d не найден.", userId)));

        //ошибка если текущий пользователя не является ни владельцем вещи, ни автором бронирования
        if (currentBooking.getItem().getOwner().getId().longValue() != user.getId().longValue()
            && currentBooking.getBooker().getId().longValue() != user.getId().longValue()) {

            throw new BookingSecurityException(String.format("Пользователь с id = %d не может работать с вещью с id = %d",
                    user.getId(), currentBooking.getItem().getId()));
        }

        return BookingMapper.toFullInfoBookingDto(currentBooking);
    }

    @Override
    public List<BookingFullInfoDto> getAllBookingsByUserIdAndState(Long userId, String stateStr, Integer from, Integer size) {

        BookingState state = BookingState.from(stateStr)
                .orElseThrow(() -> new BookingUnknownStateException(String.format("Unknown state: %s", stateStr)));

        int page = from / size;
        final PageRequest pageRequest = PageRequest.of(page, size, Sort.by("start").descending());

        //проверить, что такой пользователь есть
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserUnknownException(String.format("Пользователь с %d не найден.", userId)));

        List<Booking> userBookings;

        switch (state) {
            case ALL:
                userBookings = bookingRepository.findAllByBooker_Id(userId, pageRequest);
                break;

            case CURRENT:
                userBookings = bookingRepository
                        .findAllByBooker_IdAndStartBeforeAndEndAfter(
                                userId, LocalDateTime.now(), LocalDateTime.now(), pageRequest);
                break;

            case PAST:
                userBookings = bookingRepository
                        .findAllByBooker_IdAndStatusInAndEndBefore(
                                userId, List.of(BookingStatus.CANCELED, BookingStatus.APPROVED),
                                LocalDateTime.now(), pageRequest);
                break;

            case FUTURE:
                userBookings = bookingRepository
                        .findAllByBooker_IdAndStartAfter(userId, LocalDateTime.now(), pageRequest);
                break;

            case WAITING:
                userBookings = bookingRepository
                        .findAllByBooker_IdAndStatus(userId, BookingStatus.WAITING, pageRequest);
                break;

            case REJECTED:
                userBookings = bookingRepository
                        .findAllByBooker_IdAndStatus(userId, BookingStatus.REJECTED, pageRequest);
                break;

            default:
                throw new BookingUnknownStateException(String.format("Unknown state: %s", state));
        }

        return userBookings.stream().map(BookingMapper::toFullInfoBookingDto).collect(Collectors.toList());
    }

    @Override
    public List<BookingFullInfoDto> getAllBookingsByOwnerIdAndState(Long ownerId, String stateStr, Integer from, Integer size) {

        BookingState state = BookingState.from(stateStr)
                .orElseThrow(() -> new BookingUnknownStateException(String.format("Unknown state: %s", stateStr)));

        int page = from / size;
        final PageRequest pageRequest = PageRequest.of(page, size, Sort.by("start").descending());

        //проверить, что такой пользователь есть
        User user = userRepository.findById(ownerId)
                .orElseThrow(() -> new UserUnknownException(String.format("Пользователь с %d не найден.", ownerId)));

        //проверить, что у этого пользователя есть вещи, в которых он является владельцем
        List<Item> ownerItems = itemRepository.findALlItemsByOwnerId(ownerId);

        //Если в данного пользователя нет вещей во владении
        if (ownerItems == null || ownerItems.isEmpty()) {

            //значит искать ничего не нужно, но это и не ошибка,
            //поэтому вернуть пустой список
            return new ArrayList<>();
        }

        //получит список id вещей
        List<Long> itemIds = ownerItems.stream().map(Item::getId).collect(Collectors.toList());

        //Поиск броней по идентификаторам вещей, владельцем который является текущий пользователь
        List<Booking> userBookings;

        switch (state) {
            case ALL:
                userBookings = bookingRepository.findAllByItem_IdIn(itemIds, pageRequest);
                break;

            case CURRENT:
                userBookings = bookingRepository.findAllByItem_IdInAndStartBeforeAndEndAfter(
                        itemIds, LocalDateTime.now(), LocalDateTime.now(), pageRequest);
                break;

            case PAST:
                userBookings = bookingRepository.findAllByItem_IdInAndStatusInAndEndBefore(
                        itemIds, List.of(BookingStatus.CANCELED, BookingStatus.APPROVED), LocalDateTime.now(), pageRequest);
                break;

            case FUTURE:
                userBookings = bookingRepository.findAllByItem_IdInAndStartAfter(
                        itemIds, LocalDateTime.now(), pageRequest);
                break;

            case WAITING:
                userBookings = bookingRepository.findAllByItem_IdInAndStatusIn(
                        itemIds, List.of(BookingStatus.WAITING), pageRequest);
                break;

            case REJECTED:
                userBookings = bookingRepository.findAllByItem_IdInAndStatusIn(
                        itemIds, List.of(BookingStatus.REJECTED), pageRequest);
                break;

            default:
                throw new BookingUnknownStateException(String.format("Unknown state: %s", state));
        }


        return userBookings.stream().map(BookingMapper::toFullInfoBookingDto).collect(Collectors.toList());
    }
}
