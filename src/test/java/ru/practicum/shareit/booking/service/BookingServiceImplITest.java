package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingItemOwnerDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@Transactional
@SpringBootTest(
        properties = "spring.profiles.active=test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class BookingServiceImplITest {

    private final EntityManager em;
    private final UserService userService;
    private final ItemService itemService;
    private final BookingService bookingService;

    private UserDto userDto1;
    private UserDto userDto2;

    private ItemDto item1Dto;
    private ItemDto item2Dto;

    private BookingCreateDto bookingCreateDto;

    @BeforeEach
    void beforeEach() {
        userDto1 = new UserDto();
        userDto1.setName("User_name_1");
        userDto1.setEmail("User1@email.ru");

        userDto2 = new UserDto();
        userDto2.setName("User_name_2");
        userDto2.setEmail("User2@email.ru");

        item1Dto = new ItemDto();
        item1Dto.setName("item_name_1");
        item1Dto.setDescription("item_desc_1");
        item1Dto.setAvailable(Boolean.TRUE);

        item2Dto = new ItemDto();
        item2Dto.setName("item_name_2");
        item2Dto.setDescription("item_desc_2");
        item2Dto.setAvailable(Boolean.FALSE);

        bookingCreateDto = new BookingCreateDto();
        bookingCreateDto.setStart(LocalDateTime.now().plusDays(1));
        bookingCreateDto.setEnd(LocalDateTime.now().plusDays(3));
    }

    @Test
    void createBooking() {
        final UserDto createdUserDto1 = userService.createUser(userDto1);
        final UserDto createdUserDto2 = userService.createUser(userDto2);

        final ItemDto createdItemDto1 = itemService.createItem(item1Dto, createdUserDto1.getId());
        bookingCreateDto.setItemId(createdItemDto1.getId());

        final BookingCreateDto createdBookingForUser2 = bookingService.createBooking(bookingCreateDto, createdUserDto2.getId());

        TypedQuery<Booking> query = em.createQuery("Select b from Booking b where b.id = :id", Booking.class);
        Booking savedBookingFromDb = query.setParameter("id", createdBookingForUser2.getId()).getSingleResult();

        //проверить, что поля после сохранения и до сохранения совпадают
        assertNotNull(createdBookingForUser2);
        assertEquals(bookingCreateDto.getStart(), createdBookingForUser2.getStart());
        assertEquals(bookingCreateDto.getEnd(), createdBookingForUser2.getEnd());
        assertEquals(bookingCreateDto.getItemId(), createdBookingForUser2.getItemId());

        //проверить, как объект сохранен в БД
        assertNotNull(savedBookingFromDb);
        assertNotNull(savedBookingFromDb.getId());
        assertEquals(bookingCreateDto.getStart(), savedBookingFromDb.getStart());
        assertEquals(bookingCreateDto.getEnd(), savedBookingFromDb.getEnd());
        assertEquals(createdItemDto1.getId(), savedBookingFromDb.getItem().getId());
        assertEquals(createdUserDto2.getId(), savedBookingFromDb.getBooker().getId());
        assertEquals(BookingStatus.WAITING, savedBookingFromDb.getStatus());
    }

    @Test
    void resolveBooking() {
        final UserDto createdUserDto1 = userService.createUser(userDto1);
        final UserDto createdUserDto2 = userService.createUser(userDto2);

        final ItemDto createdItemDto1 = itemService.createItem(item1Dto, createdUserDto1.getId());
        bookingCreateDto.setItemId(createdItemDto1.getId());

        final BookingCreateDto createdBookingForUser2 = bookingService.createBooking(bookingCreateDto, createdUserDto2.getId());

        final BookingItemOwnerDto resolvedBookingByOwner = bookingService.resolveBooking(
                createdBookingForUser2.getId(), createdUserDto1.getId(), Boolean.TRUE
        );

        assertNotNull(resolvedBookingByOwner);
        assertEquals(createdBookingForUser2.getId(), resolvedBookingByOwner.getId());
        assertEquals(BookingStatus.APPROVED, resolvedBookingByOwner.getStatus());
        assertEquals(createdUserDto2.getId(), resolvedBookingByOwner.getBooker().getId());
        assertEquals(createdItemDto1.getId(), resolvedBookingByOwner.getItem().getId());

        //проверить, как сохранено в БД
        TypedQuery<Booking> query = em.createQuery("Select b from Booking b where b.id = :id", Booking.class);
        Booking savedBookingFromDb = query.setParameter("id", createdBookingForUser2.getId()).getSingleResult();

        assertNotNull(savedBookingFromDb);
        assertNotNull(savedBookingFromDb.getId());
        assertEquals(bookingCreateDto.getStart(), savedBookingFromDb.getStart());
        assertEquals(bookingCreateDto.getEnd(), savedBookingFromDb.getEnd());
        assertEquals(createdItemDto1.getId(), savedBookingFromDb.getItem().getId());
        assertEquals(createdUserDto2.getId(), savedBookingFromDb.getBooker().getId());
        assertEquals(BookingStatus.APPROVED, savedBookingFromDb.getStatus());
    }
}