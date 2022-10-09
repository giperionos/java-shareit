package ru.practicum.shareit.booking;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class BookingRepositoryTest {

    @Autowired
    BookingRepository bookingRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    ItemRepository itemRepository;

    private User owner;
    private User booker;

    private Item item1;

    private Booking rejectedPastBooking;
    private Booking rejectedFutureBooking;
    private Booking waitingCurrentBooking;
    private Booking waitingFutureBooking;
    private Booking canceledPastBooking;
    private Booking canceledCurrentBooking;
    private Booking approvedCurrentBooking;
    private Booking approvedFutureBooking;

    private PageRequest tenElementsOnPageSortByIdRequest = PageRequest.of(0, 10, Sort.by("id").ascending());

    @BeforeEach
    void beforeEach() {
        owner = new User();
        owner.setName("User_name_owner");
        owner.setEmail("Userowner@email.ru");
        owner = userRepository.save(owner);

        booker = new User();
        booker.setName("User_name_booker");
        booker.setEmail("Userbooker@email.ru");
        booker = userRepository.save(booker);

        item1 = new Item();
        item1.setName("item_1_name");
        item1.setDescription("item_1_desc");
        item1.setAvailable(Boolean.TRUE);
        item1.setOwner(owner);
        item1 = itemRepository.save(item1);

        rejectedPastBooking = new Booking();
        rejectedPastBooking.setStart(LocalDateTime.now().minusDays(3));
        rejectedPastBooking.setEnd(LocalDateTime.now().minusDays(1));
        rejectedPastBooking.setItem(item1);
        rejectedPastBooking.setBooker(booker);
        rejectedPastBooking.setStatus(BookingStatus.REJECTED);
        rejectedPastBooking = bookingRepository.save(rejectedPastBooking);

        rejectedFutureBooking = new Booking();
        rejectedFutureBooking.setStart(LocalDateTime.now().plusDays(1));
        rejectedFutureBooking.setEnd(LocalDateTime.now().plusDays(3));
        rejectedFutureBooking.setItem(item1);
        rejectedFutureBooking.setBooker(booker);
        rejectedFutureBooking.setStatus(BookingStatus.REJECTED);
        rejectedFutureBooking = bookingRepository.save(rejectedFutureBooking);

        waitingCurrentBooking = new Booking();
        waitingCurrentBooking.setStart(LocalDateTime.now().minusDays(1));
        waitingCurrentBooking.setEnd(LocalDateTime.now().plusDays(3));
        waitingCurrentBooking.setItem(item1);
        waitingCurrentBooking.setBooker(booker);
        waitingCurrentBooking.setStatus(BookingStatus.WAITING);
        waitingCurrentBooking = bookingRepository.save(waitingCurrentBooking);

        waitingFutureBooking = new Booking();
        waitingFutureBooking.setStart(LocalDateTime.now().plusDays(1));
        waitingFutureBooking.setEnd(LocalDateTime.now().plusDays(3));
        waitingFutureBooking.setItem(item1);
        waitingFutureBooking.setBooker(booker);
        waitingFutureBooking.setStatus(BookingStatus.WAITING);
        waitingFutureBooking = bookingRepository.save(waitingFutureBooking);

        canceledPastBooking = new Booking();
        canceledPastBooking.setStart(LocalDateTime.now().minusDays(3));
        canceledPastBooking.setEnd(LocalDateTime.now().minusDays(1));
        canceledPastBooking.setItem(item1);
        canceledPastBooking.setBooker(booker);
        canceledPastBooking.setStatus(BookingStatus.CANCELED);
        canceledPastBooking = bookingRepository.save(canceledPastBooking);

        canceledCurrentBooking = new Booking();
        canceledCurrentBooking.setStart(LocalDateTime.now().minusDays(1));
        canceledCurrentBooking.setEnd(LocalDateTime.now().plusDays(3));
        canceledCurrentBooking.setItem(item1);
        canceledCurrentBooking.setBooker(booker);
        canceledCurrentBooking.setStatus(BookingStatus.CANCELED);
        canceledCurrentBooking = bookingRepository.save(canceledCurrentBooking);

        approvedCurrentBooking = new Booking();
        approvedCurrentBooking.setStart(LocalDateTime.now().minusDays(1));
        approvedCurrentBooking.setEnd(LocalDateTime.now().plusDays(3));
        approvedCurrentBooking.setItem(item1);
        approvedCurrentBooking.setBooker(booker);
        approvedCurrentBooking.setStatus(BookingStatus.APPROVED);
        approvedCurrentBooking = bookingRepository.save(approvedCurrentBooking);

        approvedFutureBooking = new Booking();
        approvedFutureBooking.setStart(LocalDateTime.now().plusDays(1));
        approvedFutureBooking.setEnd(LocalDateTime.now().plusDays(3));
        approvedFutureBooking.setItem(item1);
        approvedFutureBooking.setBooker(booker);
        approvedFutureBooking.setStatus(BookingStatus.APPROVED);
        approvedFutureBooking = bookingRepository.save(approvedFutureBooking);
    }

    @AfterEach
    void afterEach() {
        bookingRepository.deleteAll();
        itemRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void shouldGet8BookingsForBooker() {
        List<Booking> result = bookingRepository.findAllByBooker_Id(booker.getId(), tenElementsOnPageSortByIdRequest);

        assertNotNull(result);
        assertEquals(8, result.size());
        assertEquals(rejectedPastBooking.getId(), result.get(0).getId());
        assertEquals(rejectedFutureBooking.getId(), result.get(1).getId());
        assertEquals(waitingCurrentBooking.getId(), result.get(2).getId());
        assertEquals(waitingFutureBooking.getId(), result.get(3).getId());
        assertEquals(canceledPastBooking.getId(), result.get(4).getId());
        assertEquals(canceledCurrentBooking.getId(), result.get(5).getId());
        assertEquals(approvedCurrentBooking.getId(), result.get(6).getId());
        assertEquals(approvedFutureBooking.getId(), result.get(7).getId());
    }

    @Test
    void shouldGet3CurrentBookingsForBooker() {
        List<Booking> result = bookingRepository.findAllByBooker_IdAndStartBeforeAndEndAfter(
                booker.getId(), LocalDateTime.now(), LocalDateTime.now(), tenElementsOnPageSortByIdRequest
        );

        assertNotNull(result);
        assertEquals(3, result.size());
        assertEquals(waitingCurrentBooking.getId(), result.get(0).getId());
        assertEquals(canceledCurrentBooking.getId(), result.get(1).getId());
        assertEquals(approvedCurrentBooking.getId(), result.get(2).getId());
     }

    @Test
    void shouldGet3FutureBookingsForBooker() {
        List<Booking> result = bookingRepository.findAllByBooker_IdAndStartAfter(booker.getId(),
                LocalDateTime.now(), tenElementsOnPageSortByIdRequest);

        assertNotNull(result);
        assertEquals(3, result.size());
        assertEquals(rejectedFutureBooking.getId(), result.get(0).getId());
        assertEquals(waitingFutureBooking.getId(), result.get(1).getId());
        assertEquals(approvedFutureBooking.getId(), result.get(2).getId());
    }

    @Test
    void shouldGet2ApprovedBookingsForBooker() {
        List<Booking> result = bookingRepository.findAllByBooker_IdAndStatus(booker.getId(),
                BookingStatus.APPROVED, tenElementsOnPageSortByIdRequest);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(approvedCurrentBooking.getId(), result.get(0).getId());
        assertEquals(approvedFutureBooking.getId(), result.get(1).getId());
    }

    @Test
    void shouldGet1RejectedAnd1CanceledBookingsForBooker() {
        List<Booking> result = bookingRepository.findAllByBooker_IdAndStatusInAndEndBefore(booker.getId(),
                List.of(BookingStatus.CANCELED, BookingStatus.REJECTED), LocalDateTime.now(), tenElementsOnPageSortByIdRequest);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(rejectedPastBooking.getId(), result.get(0).getId());
        assertEquals(canceledPastBooking.getId(), result.get(1).getId());
    }

    @Test
    void shouldGet8BookingsForItem1() {
        List<Booking> result = bookingRepository.findAllByItem_IdIn(List.of(item1.getId()), tenElementsOnPageSortByIdRequest);

        assertNotNull(result);
        assertEquals(8, result.size());
        assertEquals(rejectedPastBooking.getId(), result.get(0).getId());
        assertEquals(rejectedFutureBooking.getId(), result.get(1).getId());
        assertEquals(waitingCurrentBooking.getId(), result.get(2).getId());
        assertEquals(waitingFutureBooking.getId(), result.get(3).getId());
        assertEquals(canceledPastBooking.getId(), result.get(4).getId());
        assertEquals(canceledCurrentBooking.getId(), result.get(5).getId());
        assertEquals(approvedCurrentBooking.getId(), result.get(6).getId());
        assertEquals(approvedFutureBooking.getId(), result.get(7).getId());
    }

    @Test
    void shouldGet3CurrentBookingsForItem() {
        List<Booking> result = bookingRepository.findAllByItem_IdInAndStartBeforeAndEndAfter(
                List.of(item1.getId()), LocalDateTime.now(), LocalDateTime.now(), tenElementsOnPageSortByIdRequest
        );

        assertNotNull(result);
        assertEquals(3, result.size());
        assertEquals(waitingCurrentBooking.getId(), result.get(0).getId());
        assertEquals(canceledCurrentBooking.getId(), result.get(1).getId());
        assertEquals(approvedCurrentBooking.getId(), result.get(2).getId());
    }

    @Test
    void shouldGet3FutureBookingsForItem() {
        List<Booking> result = bookingRepository.findAllByItem_IdInAndStartAfter(List.of(item1.getId()),
                LocalDateTime.now(), tenElementsOnPageSortByIdRequest);

        assertNotNull(result);
        assertEquals(3, result.size());
        assertEquals(rejectedFutureBooking.getId(), result.get(0).getId());
        assertEquals(waitingFutureBooking.getId(), result.get(1).getId());
        assertEquals(approvedFutureBooking.getId(), result.get(2).getId());
    }

    @Test
    void shouldGet2ApprovedBookingsForItem() {
        List<Booking> result = bookingRepository.findAllByItem_IdInAndStatusIn(List.of(item1.getId()),
                List.of(BookingStatus.APPROVED), tenElementsOnPageSortByIdRequest);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(approvedCurrentBooking.getId(), result.get(0).getId());
        assertEquals(approvedFutureBooking.getId(), result.get(1).getId());
    }

    @Test
    void shouldGet1RejectedAnd1CanceledBookingsForItem() {
        List<Booking> result = bookingRepository.findAllByItem_IdInAndStatusInAndEndBefore(List.of(item1.getId()),
                List.of(BookingStatus.CANCELED, BookingStatus.REJECTED), LocalDateTime.now(), tenElementsOnPageSortByIdRequest);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(rejectedPastBooking.getId(), result.get(0).getId());
        assertEquals(canceledPastBooking.getId(), result.get(1).getId());
    }

    @Test
    void shouldGet2BookingsForBookerAndItemInPast() {
        List<Booking> result = bookingRepository.findBookingByBooker_IdAndItem_IdAndEndBefore(booker.getId(),
                item1.getId(), LocalDateTime.now());

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(rejectedPastBooking.getId(), result.get(0).getId());
        assertEquals(canceledPastBooking.getId(), result.get(1).getId());
    }
}