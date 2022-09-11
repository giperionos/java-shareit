package ru.practicum.shareit.booking;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    @Query("select b from Booking b where b.booker.id = ?1 order by b.start desc")
    List<Booking> findAllBookingsByUserId(Long userId);

    @Query("select b from Booking b where b.booker.id = ?1"
            + " and b.start < current_timestamp and b.end > current_timestamp order by b.start desc")
    List<Booking> findCurrentBookingsByUserId(Long userId);

    @Query("select b from Booking b where b.booker.id = ?1"
            + " and b.start > current_timestamp order by b.start desc")
    List<Booking> findFutureBookingsByUserId(Long userId);

    @Query("select b from Booking b where b.booker.id = ?1 and b.status = ?2"
            + " order by b.start desc")
    List<Booking> findBookingsByUserIdAndStatus(Long userId, BookingStatus status);

    @Query("select b from Booking b where b.booker.id = ?1 and b.status in (?2)"
            + " and b.end < current_timestamp order by b.start desc")
    List<Booking> findBookingsInPastByUserIdAndStatus(Long userId, List<BookingStatus> statuses);

    @Query("select b from Booking b where b.item.id in (?1) order by b.start desc")
    List<Booking> findAllBookingsByItemsIds(List<Long> itemIds);

    @Query("select b from Booking b where b.item.id in (?1)"
            + " and b.start < current_timestamp and b.end > current_timestamp order by b.start desc")
    List<Booking> findCurrentBookingsByItemsIds(List<Long> itemIds);

    @Query("select b from Booking b where b.item.id in (?1)"
            + " and b.start > current_timestamp order by b.start desc")
    List<Booking> findFutureBookingsByItemsIds(List<Long> itemIds);

    @Query("select b from Booking b where b.item.id in (?1) and b.status in (?2)"
            + " order by b.start desc")
    List<Booking> findBookingsByItemsIdsAndStatus(List<Long> itemIds, List<BookingStatus> statuses);

    @Query("select b from Booking b where b.item.id in (?1) and b.status in (?2)"
            + " and b.end < current_timestamp order by b.start desc")
    List<Booking> findBookingsInPastByItemsIdsAndStatus(List<Long> itemIds, List<BookingStatus> statuses);

    @Query("select b from Booking b where b.booker.id = ?1 and b.item.id = ?2"
            + " and b.end < current_timestamp order by b.start desc")
    Booking findBookingByUserIdAndItemIdInPast(Long userId, Long itemId);
}
