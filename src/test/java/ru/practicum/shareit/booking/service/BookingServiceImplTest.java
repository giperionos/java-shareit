package ru.practicum.shareit.booking.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingFullInfoDto;
import ru.practicum.shareit.booking.dto.BookingItemOwnerDto;
import ru.practicum.shareit.booking.exceptions.BookingHimSelfException;
import ru.practicum.shareit.booking.exceptions.BookingSecurityException;
import ru.practicum.shareit.booking.exceptions.BookingTryToUpdateSameStatusException;
import ru.practicum.shareit.booking.exceptions.BookingUnknownException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.exceptions.ItemUnavailableException;
import ru.practicum.shareit.item.exceptions.ItemUnknownException;
import ru.practicum.shareit.requests.ItemRequest;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.exceptions.UserUnknownException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class BookingServiceImplTest {

    BookingService bookingService;

    BookingRepository bookingRepository;
    ItemRepository itemRepository;
    UserRepository userRepository;

    private Boolean available = Boolean.TRUE;
    private Boolean unavailable = Boolean.FALSE;
    private ItemRequest nullRequest = null;
    private Long nullRequestId = null;
    private Boolean approved = Boolean.TRUE;
    private PageRequest pageRequest = PageRequest.of(0, 10);
    private Integer from = 0;
    private Integer size = 10;
    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss");

    private User user1;
    private User user2;
    private User user3;

    private Item item1;
    private Item item2;

    private ItemDto item1Dto;
    private ItemDto item2Dto;

    private Booking booking1;
    private BookingCreateDto bookingCreateDto1;
    private BookingItemOwnerDto bookingItemOwnerDto1;
    private BookingFullInfoDto bookingFullInfoDto1;

    @BeforeEach
    void beforeEach() {
        bookingRepository = mock(BookingRepository.class);
        itemRepository = mock(ItemRepository.class);
        userRepository = mock(UserRepository.class);

        bookingService = new BookingServiceImpl(
                bookingRepository,
                itemRepository,
                userRepository
        );

        user1 = new User(1L,"User_name_1", "User1@email.ru");
        user2 = new User(2L,"User_name_2", "User2@email.ru");
        user3 = new User(3L,"User_name_3", "User3@email.ru");

        item1 = new Item(
                1L,
                "item_name_1",
                "item_desc_1",
                available,
                user1,
                nullRequest
        );

        item1Dto = new ItemDto(
                1L,
                "item_name_1",
                "item_desc_1",
                available,
                nullRequestId
        );

        item2 = new Item(
                2L,
                "item_name_2",
                "item_desc_2",
                unavailable,
                user1,
                nullRequest
        );

        item2Dto = new ItemDto(
                2L,
                "item_name_2",
                "item_desc_2",
                unavailable,
                nullRequestId
        );

        booking1 = new Booking(
                1L,
                LocalDateTime.parse(LocalDateTime.now().plusDays(2L).format(formatter), formatter),
                LocalDateTime.parse(LocalDateTime.now().plusDays(3L).format(formatter), formatter),
                item1,
                user2,
                BookingStatus.WAITING
        );

        bookingCreateDto1 = new BookingCreateDto(
                1L,
                LocalDateTime.parse(LocalDateTime.now().plusDays(2L).format(formatter), formatter),
                LocalDateTime.parse(LocalDateTime.now().plusDays(3L).format(formatter), formatter),
                item1.getId(),
                user2.getId()
        );

        bookingItemOwnerDto1 = new BookingItemOwnerDto(
                1L,
                BookingStatus.APPROVED,
                UserMapper.toUserDto(user2),
                ItemMapper.toItemDto(item1)
        );

        bookingFullInfoDto1 = new BookingFullInfoDto(
                1L,
                LocalDateTime.parse(LocalDateTime.now().plusDays(2L).format(formatter), formatter),
                LocalDateTime.parse(LocalDateTime.now().plusDays(3L).format(formatter), formatter),
                BookingStatus.WAITING,
                UserMapper.toUserDto(user2),
                ItemMapper.toItemDto(item1)
        );
    }

    @Test
    void testSuccessCreateBooking() {
        when(userRepository.findById(any(Long.class)))
                .thenReturn(Optional.of(user2));

        when(itemRepository.findById(any(Long.class)))
                .thenReturn(Optional.of(item1));

        when(bookingRepository.save(any(Booking.class)))
                .thenReturn(booking1);

        final BookingCreateDto bookingCreateDtoResult = bookingService.createBooking(bookingCreateDto1, user2.getId());

        assertNotNull(bookingCreateDtoResult);
        assertEquals(bookingCreateDto1.getId(), bookingCreateDtoResult.getId());
        assertEquals(bookingCreateDto1.getStart(), bookingCreateDtoResult.getStart());
        assertEquals(bookingCreateDto1.getEnd(), bookingCreateDtoResult.getEnd());
        assertEquals(bookingCreateDto1.getItemId(), bookingCreateDtoResult.getItemId());
        assertEquals(bookingCreateDto1.getUserId(), bookingCreateDtoResult.getUserId());

        verify(userRepository, times(1)).findById(any(Long.class));
        verify(itemRepository, times(1)).findById(any(Long.class));
        verify(bookingRepository, times(1)).save(any(Booking.class));
    }

    @Test
    void testGetExceptionOnCreateBookingWithUnknownUserId() {
        when(userRepository.findById(any(Long.class)))
                .thenReturn(Optional.empty());

        final UserUnknownException exception = assertThrows(
                UserUnknownException.class,
                () -> bookingService.createBooking(bookingCreateDto1, user2.getId())
        );

        final String expectedMessage = String.format("Пользователь с %d не найден.", user2.getId());

        assertEquals(expectedMessage, exception.getMessage());

        verify(userRepository, times(1)).findById(any(Long.class));
        verify(itemRepository, times(0)).findById(any(Long.class));
        verify(bookingRepository, times(0)).save(any(Booking.class));
    }

    @Test
    void testGetExceptionOnCreateBookingWithUnknownItemId() {
        when(userRepository.findById(any(Long.class)))
                .thenReturn(Optional.of(user2));

        when(itemRepository.findById(any(Long.class)))
                .thenReturn(Optional.empty());

        final ItemUnknownException exception = assertThrows(
                ItemUnknownException.class,
                () -> bookingService.createBooking(bookingCreateDto1, user2.getId())
        );

        final String expectedMessage = String.format("Не найдена вещь с id = %d", item1.getId());

        assertEquals(expectedMessage, exception.getMessage());

        verify(userRepository, times(1)).findById(any(Long.class));
        verify(itemRepository, times(1)).findById(any(Long.class));
        verify(bookingRepository, times(0)).save(any(Booking.class));
    }

    @Test
    void testGetExceptionOnCreateBookingWithUnavailableItem() {
        when(userRepository.findById(any(Long.class)))
                .thenReturn(Optional.of(user2));

        when(itemRepository.findById(any(Long.class)))
                .thenReturn(Optional.of(item2));

        final ItemUnavailableException exception = assertThrows(
                ItemUnavailableException.class,
                () -> bookingService.createBooking(bookingCreateDto1, user2.getId())
        );

        final String expectedMessage = String.format("Вещь с id = %d не доступна!", item1.getId());

        assertEquals(expectedMessage, exception.getMessage());

        verify(userRepository, times(1)).findById(any(Long.class));
        verify(itemRepository, times(1)).findById(any(Long.class));
        verify(bookingRepository, times(0)).save(any(Booking.class));
    }

    @Test
    void testGetExceptionWhenUserHimSelfCreateBooking() {
        when(userRepository.findById(any(Long.class)))
                .thenReturn(Optional.of(user1));

        when(itemRepository.findById(any(Long.class)))
                .thenReturn(Optional.of(item1));

        final BookingHimSelfException exception = assertThrows(
                BookingHimSelfException.class,
                () -> bookingService.createBooking(bookingCreateDto1, user1.getId())
        );

        final String expectedMessage = String.format("Пользователь с id = %s пытается забронировать сам у себя.", user1.getId());

        assertEquals(expectedMessage, exception.getMessage());

        verify(userRepository, times(1)).findById(any(Long.class));
        verify(itemRepository, times(1)).findById(any(Long.class));
        verify(bookingRepository, times(0)).save(any(Booking.class));
    }

    @Test
    void testSuccessResolveBooking() {
        when(userRepository.findById(any(Long.class)))
                .thenReturn(Optional.of(user1));

        when(bookingRepository.findById(any(Long.class)))
                .thenReturn(Optional.of(booking1));

        when(bookingRepository.save(any(Booking.class)))
                .thenReturn(booking1);

        final BookingItemOwnerDto bookingItemOwnerDtoResult = bookingService.resolveBooking(booking1.getId(), user1.getId(), approved);

        assertNotNull(bookingItemOwnerDtoResult);
        assertEquals(bookingItemOwnerDto1.getId(), bookingItemOwnerDtoResult.getId());
        assertEquals(bookingItemOwnerDto1.getStatus(), bookingItemOwnerDtoResult.getStatus());
        assertEquals(bookingItemOwnerDto1.getBooker().getId(), bookingItemOwnerDtoResult.getBooker().getId());
        assertEquals(bookingItemOwnerDto1.getItem().getId(), bookingItemOwnerDtoResult.getItem().getId());

        verify(userRepository, times(1)).findById(any(Long.class));
        verify(bookingRepository, times(1)).findById(any(Long.class));
        verify(bookingRepository, times(1)).save(any(Booking.class));
    }

    @Test
    void testGetExceptionOnResolveBookingByUnknownUserId() {
        when(userRepository.findById(any(Long.class)))
                .thenReturn(Optional.empty());

        when(bookingRepository.findById(any(Long.class)))
                .thenReturn(Optional.of(booking1));

        final UserUnknownException exception = assertThrows(
                UserUnknownException.class,
                () -> bookingService.resolveBooking(booking1.getId(), user1.getId(), approved)
        );

        final String expectedMessage = String.format("Пользователь с %d не найден.", user1.getId());

        assertEquals(expectedMessage, exception.getMessage());

        verify(userRepository, times(1)).findById(any(Long.class));
        verify(bookingRepository, times(1)).findById(any(Long.class));
        verify(bookingRepository, times(0)).save(any(Booking.class));
    }

    @Test
    void testGetExceptionOnResolveBookingByUnknownBookingId() {
        when(bookingRepository.findById(any(Long.class)))
                .thenReturn(Optional.empty());

        final BookingUnknownException exception = assertThrows(
                BookingUnknownException.class,
                () -> bookingService.resolveBooking(booking1.getId(), user1.getId(), approved)
        );

        final String expectedMessage = String.format("Не найдена бронь с id = %d", booking1.getId());

        assertEquals(expectedMessage, exception.getMessage());

        verify(userRepository, times(0)).findById(any(Long.class));
        verify(bookingRepository, times(1)).findById(any(Long.class));
        verify(bookingRepository, times(0)).save(any(Booking.class));
    }

    @Test
    void testGetExceptionOnResolveBookingByNotOwnerBooking() {
        when(userRepository.findById(any(Long.class)))
                .thenReturn(Optional.of(user2));

        when(bookingRepository.findById(any(Long.class)))
                .thenReturn(Optional.of(booking1));

        final BookingSecurityException exception = assertThrows(
                BookingSecurityException.class,
                () -> bookingService.resolveBooking(booking1.getId(), user2.getId(), approved)
        );

        final String expectedMessage = String.format("Пользователь с id = %d не может работать с вещью с id = %d",
                user2.getId(), booking1.getItem().getId());

        assertEquals(expectedMessage, exception.getMessage());

        verify(userRepository, times(1)).findById(any(Long.class));
        verify(bookingRepository, times(1)).findById(any(Long.class));
        verify(bookingRepository, times(0)).save(any(Booking.class));
    }

    @Test
    void testGetExceptionOnResolveBookingWithSameStatus() {
        booking1.setStatus(BookingStatus.APPROVED);

        when(userRepository.findById(any(Long.class)))
                .thenReturn(Optional.of(user1));

        when(bookingRepository.findById(any(Long.class)))
                .thenReturn(Optional.of(booking1));

        final BookingTryToUpdateSameStatusException exception = assertThrows(
                BookingTryToUpdateSameStatusException.class,
                () -> bookingService.resolveBooking(booking1.getId(), user1.getId(), approved)
        );

        final String expectedMessage = String.format("Бронирование с id = %s уже имеет статус %s", booking1.getId(), BookingStatus.APPROVED);

        assertEquals(expectedMessage, exception.getMessage());

        verify(userRepository, times(1)).findById(any(Long.class));
        verify(bookingRepository, times(1)).findById(any(Long.class));
        verify(bookingRepository, times(0)).save(any(Booking.class));
    }

    @Test
    void testSuccessGetBookingDetailInfoById() {
        when(bookingRepository.findById(any(Long.class)))
                .thenReturn(Optional.of(booking1));

        when(userRepository.findById(any(Long.class)))
                .thenReturn(Optional.of(user1));

        final BookingFullInfoDto bookingFullInfoDtoResult = bookingService.getBookingDetailInfoById(booking1.getId(), user1.getId());

        assertNotNull(bookingFullInfoDtoResult);
        assertEquals(bookingFullInfoDto1.getId(), bookingFullInfoDtoResult.getId());
        assertEquals(bookingFullInfoDto1.getStart(), bookingFullInfoDtoResult.getStart());
        assertEquals(bookingFullInfoDto1.getEnd(), bookingFullInfoDtoResult.getEnd());
        assertEquals(bookingFullInfoDto1.getStatus(), bookingFullInfoDtoResult.getStatus());
        assertEquals(bookingFullInfoDto1.getBooker().getId(), bookingFullInfoDtoResult.getBooker().getId());
        assertEquals(bookingFullInfoDto1.getItem().getId(), bookingFullInfoDtoResult.getItem().getId());

        verify(bookingRepository, times(1)).findById(any(Long.class));
        verify(userRepository, times(1)).findById(any(Long.class));
    }

    @Test
    void testGetExceptionOnGetBookingDetailInfoByUnknownId() {
        when(bookingRepository.findById(any(Long.class)))
                .thenReturn(Optional.empty());

        final BookingUnknownException exception = assertThrows(
                BookingUnknownException.class,
                () -> bookingService.getBookingDetailInfoById(booking1.getId(), user1.getId())
        );

        final String expectedMessage = String.format("Не найдена бронь с id = %d", booking1.getId());

        assertEquals(expectedMessage, exception.getMessage());

        verify(bookingRepository, times(1)).findById(any(Long.class));
        verify(userRepository, times(0)).findById(any(Long.class));
    }

    @Test
    void testGetExceptionOnGetBookingDetailInfoWithUnknownUserId() {
        when(bookingRepository.findById(any(Long.class)))
                .thenReturn(Optional.of(booking1));

        when(userRepository.findById(any(Long.class)))
                .thenReturn(Optional.empty());

        final UserUnknownException exception = assertThrows(
                UserUnknownException.class,
                () -> bookingService.getBookingDetailInfoById(booking1.getId(), user1.getId())
        );

        final String expectedMessage = String.format("Пользователь с %d не найден.", user1.getId());

        assertEquals(expectedMessage, exception.getMessage());

        verify(bookingRepository, times(1)).findById(any(Long.class));
        verify(userRepository, times(1)).findById(any(Long.class));
    }

    @Test
    void testGetExceptionOnGetBookingDetailInfoByNotOwnerUserAndNotBookerUser() {
        when(bookingRepository.findById(any(Long.class)))
                .thenReturn(Optional.of(booking1));

        when(userRepository.findById(any(Long.class)))
                .thenReturn(Optional.of(user3));

        final BookingSecurityException exception = assertThrows(
                BookingSecurityException.class,
                () -> bookingService.getBookingDetailInfoById(booking1.getId(), user3.getId())
        );

        final String expectedMessage = String.format("Пользователь с id = %d не может работать с вещью с id = %d",
                user3.getId(), booking1.getItem().getId());

        assertEquals(expectedMessage, exception.getMessage());

        verify(bookingRepository, times(1)).findById(any(Long.class));
        verify(userRepository, times(1)).findById(any(Long.class));
    }

    @Test
    void testSuccessGetAllBookingsByUserId() {
        when(userRepository.findById(any(Long.class)))
                .thenReturn(Optional.of(user2));

        when(bookingRepository.findAllByBooker_Id(any(Long.class), any(PageRequest.class)))
                .thenReturn(List.of(booking1));

        final List<BookingFullInfoDto> result = bookingService.getAllBookingsByUserIdAndState(user2.getId(), "ALL", from, size);

        assertNotNull(result);
        assertEquals(1, result.size());

        assertEquals(bookingFullInfoDto1.getId(), result.get(0).getId());
        assertEquals(bookingFullInfoDto1.getStart(), result.get(0).getStart());
        assertEquals(bookingFullInfoDto1.getEnd(), result.get(0).getEnd());
        assertEquals(bookingFullInfoDto1.getStatus(), result.get(0).getStatus());
        assertEquals(bookingFullInfoDto1.getBooker().getId(), result.get(0).getBooker().getId());
        assertEquals(bookingFullInfoDto1.getItem().getId(), result.get(0).getItem().getId());

        verify(userRepository, times(1)).findById(any(Long.class));
        verify(bookingRepository, times(1)).findAllByBooker_Id(any(Long.class), any(PageRequest.class));
        verify(bookingRepository, times(0)).findAllByBooker_IdAndStartBeforeAndEndAfter(any(Long.class), any(LocalDateTime.class), any(LocalDateTime.class), any(PageRequest.class));
        verify(bookingRepository, times(0)).findAllByBooker_IdAndStatusInAndEndBefore(any(Long.class),anyList(), any(LocalDateTime.class), any(PageRequest.class));
        verify(bookingRepository, times(0)).findAllByBooker_IdAndStartAfter(any(Long.class), any(LocalDateTime.class), any(PageRequest.class));
        verify(bookingRepository, times(0)).findAllByBooker_IdAndStatus(any(Long.class), any(BookingStatus.class), any(PageRequest.class));
    }

    @Test
    void testSuccessGetCurrentBookingsByUserId() {
        when(userRepository.findById(any(Long.class)))
                .thenReturn(Optional.of(user2));

        when(bookingRepository.findAllByBooker_IdAndStartBeforeAndEndAfter(any(Long.class), any(LocalDateTime.class), any(LocalDateTime.class), any(PageRequest.class)))
                .thenReturn(List.of(booking1));

        final List<BookingFullInfoDto> result = bookingService.getAllBookingsByUserIdAndState(user2.getId(),"CURRENT", from, size);

        assertNotNull(result);
        assertEquals(1, result.size());

        assertEquals(bookingFullInfoDto1.getId(), result.get(0).getId());
        assertEquals(bookingFullInfoDto1.getStart(), result.get(0).getStart());
        assertEquals(bookingFullInfoDto1.getEnd(), result.get(0).getEnd());
        assertEquals(bookingFullInfoDto1.getStatus(), result.get(0).getStatus());
        assertEquals(bookingFullInfoDto1.getBooker().getId(), result.get(0).getBooker().getId());
        assertEquals(bookingFullInfoDto1.getItem().getId(), result.get(0).getItem().getId());

        verify(userRepository, times(1)).findById(any(Long.class));
        verify(bookingRepository, times(0)).findAllByBooker_Id(any(Long.class), any(PageRequest.class));
        verify(bookingRepository, times(1)).findAllByBooker_IdAndStartBeforeAndEndAfter(any(Long.class), any(LocalDateTime.class), any(LocalDateTime.class), any(PageRequest.class));
        verify(bookingRepository, times(0)).findAllByBooker_IdAndStatusInAndEndBefore(any(Long.class),anyList(), any(LocalDateTime.class), any(PageRequest.class));
        verify(bookingRepository, times(0)).findAllByBooker_IdAndStartAfter(any(Long.class), any(LocalDateTime.class), any(PageRequest.class));
        verify(bookingRepository, times(0)).findAllByBooker_IdAndStatus(any(Long.class), any(BookingStatus.class), any(PageRequest.class));
    }

    @Test
    void testSuccessGetPastBookingsByUserId() {
        when(userRepository.findById(any(Long.class)))
                .thenReturn(Optional.of(user2));

        when(bookingRepository.findAllByBooker_IdAndStatusInAndEndBefore(any(Long.class),anyList(), any(LocalDateTime.class), any(PageRequest.class)))
                .thenReturn(List.of(booking1));

        final List<BookingFullInfoDto> result = bookingService.getAllBookingsByUserIdAndState(user2.getId(),"PAST", from, size);

        assertNotNull(result);
        assertEquals(1, result.size());

        assertEquals(bookingFullInfoDto1.getId(), result.get(0).getId());
        assertEquals(bookingFullInfoDto1.getStart(), result.get(0).getStart());
        assertEquals(bookingFullInfoDto1.getEnd(), result.get(0).getEnd());
        assertEquals(bookingFullInfoDto1.getStatus(), result.get(0).getStatus());
        assertEquals(bookingFullInfoDto1.getBooker().getId(), result.get(0).getBooker().getId());
        assertEquals(bookingFullInfoDto1.getItem().getId(), result.get(0).getItem().getId());

        verify(userRepository, times(1)).findById(any(Long.class));
        verify(bookingRepository, times(0)).findAllByBooker_Id(any(Long.class), any(PageRequest.class));
        verify(bookingRepository, times(0)).findAllByBooker_IdAndStartBeforeAndEndAfter(any(Long.class), any(LocalDateTime.class), any(LocalDateTime.class), any(PageRequest.class));
        verify(bookingRepository, times(1)).findAllByBooker_IdAndStatusInAndEndBefore(any(Long.class),anyList(), any(LocalDateTime.class), any(PageRequest.class));
        verify(bookingRepository, times(0)).findAllByBooker_IdAndStartAfter(any(Long.class), any(LocalDateTime.class), any(PageRequest.class));
        verify(bookingRepository, times(0)).findAllByBooker_IdAndStatus(any(Long.class), any(BookingStatus.class), any(PageRequest.class));
    }

    @Test
    void testSuccessGetFutureBookingsByUserId() {
        when(userRepository.findById(any(Long.class)))
                .thenReturn(Optional.of(user2));

        when(bookingRepository.findAllByBooker_IdAndStartAfter(any(Long.class), any(LocalDateTime.class), any(PageRequest.class)))
                .thenReturn(List.of(booking1));

        final List<BookingFullInfoDto> result = bookingService.getAllBookingsByUserIdAndState(user2.getId(),"FUTURE", from, size);

        assertNotNull(result);
        assertEquals(1, result.size());

        assertEquals(bookingFullInfoDto1.getId(), result.get(0).getId());
        assertEquals(bookingFullInfoDto1.getStart(), result.get(0).getStart());
        assertEquals(bookingFullInfoDto1.getEnd(), result.get(0).getEnd());
        assertEquals(bookingFullInfoDto1.getStatus(), result.get(0).getStatus());
        assertEquals(bookingFullInfoDto1.getBooker().getId(), result.get(0).getBooker().getId());
        assertEquals(bookingFullInfoDto1.getItem().getId(), result.get(0).getItem().getId());

        verify(userRepository, times(1)).findById(any(Long.class));
        verify(bookingRepository, times(0)).findAllByBooker_Id(any(Long.class), any(PageRequest.class));
        verify(bookingRepository, times(0)).findAllByBooker_IdAndStartBeforeAndEndAfter(any(Long.class), any(LocalDateTime.class), any(LocalDateTime.class), any(PageRequest.class));
        verify(bookingRepository, times(0)).findAllByBooker_IdAndStatusInAndEndBefore(any(Long.class),anyList(), any(LocalDateTime.class), any(PageRequest.class));
        verify(bookingRepository, times(1)).findAllByBooker_IdAndStartAfter(any(Long.class), any(LocalDateTime.class), any(PageRequest.class));
        verify(bookingRepository, times(0)).findAllByBooker_IdAndStatus(any(Long.class), any(BookingStatus.class), any(PageRequest.class));
    }

    @Test
    void testSuccessGetWaitingBookingsByUserId() {
        when(userRepository.findById(any(Long.class)))
                .thenReturn(Optional.of(user2));

        when(bookingRepository.findAllByBooker_IdAndStatus(any(Long.class), any(BookingStatus.class), any(PageRequest.class)))
                .thenReturn(List.of(booking1));

        final List<BookingFullInfoDto> result = bookingService.getAllBookingsByUserIdAndState(user2.getId(),"WAITING", from, size);

        assertNotNull(result);
        assertEquals(1, result.size());

        assertEquals(bookingFullInfoDto1.getId(), result.get(0).getId());
        assertEquals(bookingFullInfoDto1.getStart(), result.get(0).getStart());
        assertEquals(bookingFullInfoDto1.getEnd(), result.get(0).getEnd());
        assertEquals(bookingFullInfoDto1.getStatus(), result.get(0).getStatus());
        assertEquals(bookingFullInfoDto1.getBooker().getId(), result.get(0).getBooker().getId());
        assertEquals(bookingFullInfoDto1.getItem().getId(), result.get(0).getItem().getId());

        verify(userRepository, times(1)).findById(any(Long.class));
        verify(bookingRepository, times(0)).findAllByBooker_Id(any(Long.class), any(PageRequest.class));
        verify(bookingRepository, times(0)).findAllByBooker_IdAndStartBeforeAndEndAfter(any(Long.class), any(LocalDateTime.class), any(LocalDateTime.class), any(PageRequest.class));
        verify(bookingRepository, times(0)).findAllByBooker_IdAndStatusInAndEndBefore(any(Long.class),anyList(), any(LocalDateTime.class), any(PageRequest.class));
        verify(bookingRepository, times(0)).findAllByBooker_IdAndStartAfter(any(Long.class), any(LocalDateTime.class), any(PageRequest.class));
        verify(bookingRepository, times(1)).findAllByBooker_IdAndStatus(any(Long.class), any(BookingStatus.class), any(PageRequest.class));
    }

    @Test
    void testSuccessGetRejectedBookingsByUserId() {
        when(userRepository.findById(any(Long.class)))
                .thenReturn(Optional.of(user2));

        when(bookingRepository.findAllByBooker_IdAndStatus(any(Long.class), any(BookingStatus.class), any(PageRequest.class)))
                .thenReturn(List.of(booking1));

        final List<BookingFullInfoDto> result = bookingService.getAllBookingsByUserIdAndState(user2.getId(),"REJECTED", from, size);

        assertNotNull(result);
        assertEquals(1, result.size());

        assertEquals(bookingFullInfoDto1.getId(), result.get(0).getId());
        assertEquals(bookingFullInfoDto1.getStart(), result.get(0).getStart());
        assertEquals(bookingFullInfoDto1.getEnd(), result.get(0).getEnd());
        assertEquals(bookingFullInfoDto1.getStatus(), result.get(0).getStatus());
        assertEquals(bookingFullInfoDto1.getBooker().getId(), result.get(0).getBooker().getId());
        assertEquals(bookingFullInfoDto1.getItem().getId(), result.get(0).getItem().getId());

        verify(userRepository, times(1)).findById(any(Long.class));
        verify(bookingRepository, times(0)).findAllByBooker_Id(any(Long.class), any(PageRequest.class));
        verify(bookingRepository, times(0)).findAllByBooker_IdAndStartBeforeAndEndAfter(any(Long.class), any(LocalDateTime.class), any(LocalDateTime.class), any(PageRequest.class));
        verify(bookingRepository, times(0)).findAllByBooker_IdAndStatusInAndEndBefore(any(Long.class),anyList(), any(LocalDateTime.class), any(PageRequest.class));
        verify(bookingRepository, times(0)).findAllByBooker_IdAndStartAfter(any(Long.class), any(LocalDateTime.class), any(PageRequest.class));
        verify(bookingRepository, times(1)).findAllByBooker_IdAndStatus(any(Long.class), any(BookingStatus.class), any(PageRequest.class));
    }

    @Test
    void getExceptionOnGetBookingsByUnknownUserId() {
        when(userRepository.findById(any(Long.class)))
                .thenReturn(Optional.empty());

        final UserUnknownException exception = assertThrows(
                UserUnknownException.class,
                () -> bookingService.getAllBookingsByUserIdAndState(user2.getId(),"ALL", from, size)
        );

        final String expectedMessage = String.format("Пользователь с %d не найден.", user2.getId());

        assertEquals(expectedMessage, exception.getMessage());

        verify(userRepository, times(1)).findById(any(Long.class));
        verify(bookingRepository, times(0)).findAllByBooker_Id(any(Long.class), any(PageRequest.class));
        verify(bookingRepository, times(0)).findAllByBooker_IdAndStartBeforeAndEndAfter(any(Long.class), any(LocalDateTime.class), any(LocalDateTime.class), any(PageRequest.class));
        verify(bookingRepository, times(0)).findAllByBooker_IdAndStatusInAndEndBefore(any(Long.class),anyList(), any(LocalDateTime.class), any(PageRequest.class));
        verify(bookingRepository, times(0)).findAllByBooker_IdAndStartAfter(any(Long.class), any(LocalDateTime.class), any(PageRequest.class));
        verify(bookingRepository, times(0)).findAllByBooker_IdAndStatus(any(Long.class), any(BookingStatus.class), any(PageRequest.class));
    }

    @Test
    void testSuccessGetAllBookingsByOwner() {
        when(userRepository.findById(any(Long.class)))
                .thenReturn(Optional.of(user1));

        when(itemRepository.findALlItemsByOwnerId(any(Long.class)))
                .thenReturn(List.of(item1, item2));

        when(bookingRepository.findAllByItem_IdIn(anyList(), any(PageRequest.class)))
                .thenReturn(List.of(booking1));

        final List<BookingFullInfoDto> result = bookingService.getAllBookingsByOwnerIdAndState(user1.getId(),"ALL", from, size);

        assertNotNull(result);
        assertEquals(1, result.size());

        assertEquals(bookingFullInfoDto1.getId(), result.get(0).getId());
        assertEquals(bookingFullInfoDto1.getStart(), result.get(0).getStart());
        assertEquals(bookingFullInfoDto1.getEnd(), result.get(0).getEnd());
        assertEquals(bookingFullInfoDto1.getStatus(), result.get(0).getStatus());
        assertEquals(bookingFullInfoDto1.getBooker().getId(), result.get(0).getBooker().getId());
        assertEquals(bookingFullInfoDto1.getItem().getId(), result.get(0).getItem().getId());

        verify(userRepository, times(1)).findById(any(Long.class));
        verify(itemRepository, times(1)).findALlItemsByOwnerId(any(Long.class));
        verify(bookingRepository, times(1)).findAllByItem_IdIn(anyList(), any(PageRequest.class));
        verify(bookingRepository, times(0)).findAllByItem_IdInAndStartBeforeAndEndAfter(anyList(), any(LocalDateTime.class), any(LocalDateTime.class), any(PageRequest.class));
        verify(bookingRepository, times(0)).findAllByItem_IdInAndStatusInAndEndBefore(anyList(),anyList(), any(LocalDateTime.class), any(PageRequest.class));
        verify(bookingRepository, times(0)).findAllByItem_IdInAndStartAfter(anyList(), any(LocalDateTime.class), any(PageRequest.class));
        verify(bookingRepository, times(0)).findAllByItem_IdInAndStatusIn(anyList(), anyList(), any(PageRequest.class));
    }

    @Test
    void testSuccessGetCurrentBookingsByOwner() {
        when(userRepository.findById(any(Long.class)))
                .thenReturn(Optional.of(user1));

        when(itemRepository.findALlItemsByOwnerId(any(Long.class)))
                .thenReturn(List.of(item1, item2));

        when(bookingRepository.findAllByItem_IdInAndStartBeforeAndEndAfter(anyList(), any(LocalDateTime.class), any(LocalDateTime.class), any(PageRequest.class)))
                .thenReturn(List.of(booking1));

        final List<BookingFullInfoDto> result = bookingService.getAllBookingsByOwnerIdAndState(user1.getId(),"CURRENT", from, size);

        assertNotNull(result);
        assertEquals(1, result.size());

        assertEquals(bookingFullInfoDto1.getId(), result.get(0).getId());
        assertEquals(bookingFullInfoDto1.getStart(), result.get(0).getStart());
        assertEquals(bookingFullInfoDto1.getEnd(), result.get(0).getEnd());
        assertEquals(bookingFullInfoDto1.getStatus(), result.get(0).getStatus());
        assertEquals(bookingFullInfoDto1.getBooker().getId(), result.get(0).getBooker().getId());
        assertEquals(bookingFullInfoDto1.getItem().getId(), result.get(0).getItem().getId());

        verify(userRepository, times(1)).findById(any(Long.class));
        verify(itemRepository, times(1)).findALlItemsByOwnerId(any(Long.class));
        verify(bookingRepository, times(0)).findAllByItem_IdIn(anyList(), any(PageRequest.class));
        verify(bookingRepository, times(1)).findAllByItem_IdInAndStartBeforeAndEndAfter(anyList(), any(LocalDateTime.class), any(LocalDateTime.class), any(PageRequest.class));
        verify(bookingRepository, times(0)).findAllByItem_IdInAndStatusInAndEndBefore(anyList(),anyList(), any(LocalDateTime.class), any(PageRequest.class));
        verify(bookingRepository, times(0)).findAllByItem_IdInAndStartAfter(anyList(), any(LocalDateTime.class), any(PageRequest.class));
        verify(bookingRepository, times(0)).findAllByItem_IdInAndStatusIn(anyList(), anyList(), any(PageRequest.class));
    }

    @Test
    void testSuccessGetPastBookingsByOwner() {
        when(userRepository.findById(any(Long.class)))
                .thenReturn(Optional.of(user1));

        when(itemRepository.findALlItemsByOwnerId(any(Long.class)))
                .thenReturn(List.of(item1, item2));

        when(bookingRepository.findAllByItem_IdInAndStatusInAndEndBefore(anyList(),anyList(), any(LocalDateTime.class), any(PageRequest.class)))
                .thenReturn(List.of(booking1));

        final List<BookingFullInfoDto> result = bookingService.getAllBookingsByOwnerIdAndState(user1.getId(),"PAST", from, size);

        assertNotNull(result);
        assertEquals(1, result.size());

        assertEquals(bookingFullInfoDto1.getId(), result.get(0).getId());
        assertEquals(bookingFullInfoDto1.getStart(), result.get(0).getStart());
        assertEquals(bookingFullInfoDto1.getEnd(), result.get(0).getEnd());
        assertEquals(bookingFullInfoDto1.getStatus(), result.get(0).getStatus());
        assertEquals(bookingFullInfoDto1.getBooker().getId(), result.get(0).getBooker().getId());
        assertEquals(bookingFullInfoDto1.getItem().getId(), result.get(0).getItem().getId());

        verify(userRepository, times(1)).findById(any(Long.class));
        verify(itemRepository, times(1)).findALlItemsByOwnerId(any(Long.class));
        verify(bookingRepository, times(0)).findAllByItem_IdIn(anyList(), any(PageRequest.class));
        verify(bookingRepository, times(0)).findAllByItem_IdInAndStartBeforeAndEndAfter(anyList(), any(LocalDateTime.class), any(LocalDateTime.class), any(PageRequest.class));
        verify(bookingRepository, times(1)).findAllByItem_IdInAndStatusInAndEndBefore(anyList(),anyList(), any(LocalDateTime.class), any(PageRequest.class));
        verify(bookingRepository, times(0)).findAllByItem_IdInAndStartAfter(anyList(), any(LocalDateTime.class), any(PageRequest.class));
        verify(bookingRepository, times(0)).findAllByItem_IdInAndStatusIn(anyList(), anyList(), any(PageRequest.class));
    }

    @Test
    void testSuccessGetFutureBookingsByOwner() {
        when(userRepository.findById(any(Long.class)))
                .thenReturn(Optional.of(user1));

        when(itemRepository.findALlItemsByOwnerId(any(Long.class)))
                .thenReturn(List.of(item1, item2));

        when(bookingRepository.findAllByItem_IdInAndStartAfter(anyList(), any(LocalDateTime.class), any(PageRequest.class)))
                .thenReturn(List.of(booking1));

        final List<BookingFullInfoDto> result = bookingService.getAllBookingsByOwnerIdAndState(user1.getId(),"FUTURE", from, size);

        assertNotNull(result);
        assertEquals(1, result.size());

        assertEquals(bookingFullInfoDto1.getId(), result.get(0).getId());
        assertEquals(bookingFullInfoDto1.getStart(), result.get(0).getStart());
        assertEquals(bookingFullInfoDto1.getEnd(), result.get(0).getEnd());
        assertEquals(bookingFullInfoDto1.getStatus(), result.get(0).getStatus());
        assertEquals(bookingFullInfoDto1.getBooker().getId(), result.get(0).getBooker().getId());
        assertEquals(bookingFullInfoDto1.getItem().getId(), result.get(0).getItem().getId());

        verify(userRepository, times(1)).findById(any(Long.class));
        verify(itemRepository, times(1)).findALlItemsByOwnerId(any(Long.class));
        verify(bookingRepository, times(0)).findAllByItem_IdIn(anyList(), any(PageRequest.class));
        verify(bookingRepository, times(0)).findAllByItem_IdInAndStartBeforeAndEndAfter(anyList(), any(LocalDateTime.class), any(LocalDateTime.class), any(PageRequest.class));
        verify(bookingRepository, times(0)).findAllByItem_IdInAndStatusInAndEndBefore(anyList(),anyList(), any(LocalDateTime.class), any(PageRequest.class));
        verify(bookingRepository, times(1)).findAllByItem_IdInAndStartAfter(anyList(), any(LocalDateTime.class), any(PageRequest.class));
        verify(bookingRepository, times(0)).findAllByItem_IdInAndStatusIn(anyList(), anyList(), any(PageRequest.class));
    }

    @Test
    void testSuccessGetWaitingBookingsByOwner() {
        when(userRepository.findById(any(Long.class)))
                .thenReturn(Optional.of(user1));

        when(itemRepository.findALlItemsByOwnerId(any(Long.class)))
                .thenReturn(List.of(item1, item2));

        when(bookingRepository.findAllByItem_IdInAndStatusIn(anyList(), anyList(), any(PageRequest.class)))
                .thenReturn(List.of(booking1));

        final List<BookingFullInfoDto> result = bookingService.getAllBookingsByOwnerIdAndState(user1.getId(),"WAITING", from, size);

        assertNotNull(result);
        assertEquals(1, result.size());

        assertEquals(bookingFullInfoDto1.getId(), result.get(0).getId());
        assertEquals(bookingFullInfoDto1.getStart(), result.get(0).getStart());
        assertEquals(bookingFullInfoDto1.getEnd(), result.get(0).getEnd());
        assertEquals(bookingFullInfoDto1.getStatus(), result.get(0).getStatus());
        assertEquals(bookingFullInfoDto1.getBooker().getId(), result.get(0).getBooker().getId());
        assertEquals(bookingFullInfoDto1.getItem().getId(), result.get(0).getItem().getId());

        verify(userRepository, times(1)).findById(any(Long.class));
        verify(itemRepository, times(1)).findALlItemsByOwnerId(any(Long.class));
        verify(bookingRepository, times(0)).findAllByItem_IdIn(anyList(), any(PageRequest.class));
        verify(bookingRepository, times(0)).findAllByItem_IdInAndStartBeforeAndEndAfter(anyList(), any(LocalDateTime.class), any(LocalDateTime.class), any(PageRequest.class));
        verify(bookingRepository, times(0)).findAllByItem_IdInAndStatusInAndEndBefore(anyList(),anyList(), any(LocalDateTime.class), any(PageRequest.class));
        verify(bookingRepository, times(0)).findAllByItem_IdInAndStartAfter(anyList(), any(LocalDateTime.class), any(PageRequest.class));
        verify(bookingRepository, times(1)).findAllByItem_IdInAndStatusIn(anyList(), anyList(), any(PageRequest.class));
    }

    @Test
    void testSuccessGetRejectedBookingsByOwner() {
        when(userRepository.findById(any(Long.class)))
                .thenReturn(Optional.of(user1));

        when(itemRepository.findALlItemsByOwnerId(any(Long.class)))
                .thenReturn(List.of(item1, item2));

        when(bookingRepository.findAllByItem_IdInAndStatusIn(anyList(), anyList(), any(PageRequest.class)))
                .thenReturn(List.of(booking1));

        final List<BookingFullInfoDto> result = bookingService.getAllBookingsByOwnerIdAndState(user1.getId(),"REJECTED", from, size);

        assertNotNull(result);
        assertEquals(1, result.size());

        assertEquals(bookingFullInfoDto1.getId(), result.get(0).getId());
        assertEquals(bookingFullInfoDto1.getStart(), result.get(0).getStart());
        assertEquals(bookingFullInfoDto1.getEnd(), result.get(0).getEnd());
        assertEquals(bookingFullInfoDto1.getStatus(), result.get(0).getStatus());
        assertEquals(bookingFullInfoDto1.getBooker().getId(), result.get(0).getBooker().getId());
        assertEquals(bookingFullInfoDto1.getItem().getId(), result.get(0).getItem().getId());

        verify(userRepository, times(1)).findById(any(Long.class));
        verify(itemRepository, times(1)).findALlItemsByOwnerId(any(Long.class));
        verify(bookingRepository, times(0)).findAllByItem_IdIn(anyList(), any(PageRequest.class));
        verify(bookingRepository, times(0)).findAllByItem_IdInAndStartBeforeAndEndAfter(anyList(), any(LocalDateTime.class), any(LocalDateTime.class), any(PageRequest.class));
        verify(bookingRepository, times(0)).findAllByItem_IdInAndStatusInAndEndBefore(anyList(),anyList(), any(LocalDateTime.class), any(PageRequest.class));
        verify(bookingRepository, times(0)).findAllByItem_IdInAndStartAfter(anyList(), any(LocalDateTime.class), any(PageRequest.class));
        verify(bookingRepository, times(1)).findAllByItem_IdInAndStatusIn(anyList(), anyList(), any(PageRequest.class));
    }

    @Test
    void testSuccessGetEmptyListBookingsByOwner() {
        when(userRepository.findById(any(Long.class)))
                .thenReturn(Optional.of(user1));

        when(itemRepository.findALlItemsByOwnerId(any(Long.class)))
                .thenReturn(Collections.emptyList());

        final List<BookingFullInfoDto> result = bookingService.getAllBookingsByOwnerIdAndState(user1.getId(),"ALL", from, size);

        assertEquals(0, result.size());

        verify(userRepository, times(1)).findById(any(Long.class));
        verify(itemRepository, times(1)).findALlItemsByOwnerId(any(Long.class));
        verify(bookingRepository, times(0)).findAllByItem_IdIn(anyList(), any(PageRequest.class));
        verify(bookingRepository, times(0)).findAllByItem_IdInAndStartBeforeAndEndAfter(anyList(), any(LocalDateTime.class), any(LocalDateTime.class), any(PageRequest.class));
        verify(bookingRepository, times(0)).findAllByItem_IdInAndStatusInAndEndBefore(anyList(),anyList(), any(LocalDateTime.class), any(PageRequest.class));
        verify(bookingRepository, times(0)).findAllByItem_IdInAndStartAfter(anyList(), any(LocalDateTime.class), any(PageRequest.class));
        verify(bookingRepository, times(0)).findAllByItem_IdInAndStatusIn(anyList(), anyList(), any(PageRequest.class));
    }

    @Test
    void testGetExceptionOnGetBookingsByUnknownOwner() {
        when(userRepository.findById(any(Long.class)))
                .thenReturn(Optional.empty());

        final UserUnknownException exception = assertThrows(
                UserUnknownException.class,
                () -> bookingService.getAllBookingsByOwnerIdAndState(user1.getId(),"ALL", from, size)
        );

        final String expectedMessage = String.format("Пользователь с %d не найден.", user1.getId());

        assertEquals(expectedMessage, exception.getMessage());

        verify(userRepository, times(1)).findById(any(Long.class));
        verify(itemRepository, times(0)).findALlItemsByOwnerId(any(Long.class));
        verify(bookingRepository, times(0)).findAllByItem_IdIn(anyList(), any(PageRequest.class));
        verify(bookingRepository, times(0)).findAllByItem_IdInAndStartBeforeAndEndAfter(anyList(), any(LocalDateTime.class), any(LocalDateTime.class), any(PageRequest.class));
        verify(bookingRepository, times(0)).findAllByItem_IdInAndStatusInAndEndBefore(anyList(),anyList(), any(LocalDateTime.class), any(PageRequest.class));
        verify(bookingRepository, times(0)).findAllByItem_IdInAndStartAfter(anyList(), any(LocalDateTime.class), any(PageRequest.class));
        verify(bookingRepository, times(0)).findAllByItem_IdInAndStatusIn(anyList(), anyList(), any(PageRequest.class));

    }
}