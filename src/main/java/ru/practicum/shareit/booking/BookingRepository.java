package ru.practicum.shareit.booking;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findAllByBooker_IdOrderByStartDesc(Long userId);

    List<Booking> findAllByBooker_IdAndStartBeforeAndEndAfterOrderByStartDesc(Long userId, LocalDateTime dateStart, LocalDateTime dateEnd);

    List<Booking> findAllByBooker_IdAndStartAfterOrderByStartDesc(Long userId, LocalDateTime dateStart);

    List<Booking> findAllByBooker_IdAndStatusOrderByStartDesc(Long userId, BookingStatus status);

    List<Booking> findAllByBooker_IdAndStatusInAndEndBeforeOrderByStartDesc(Long userId, List<BookingStatus> statuses, LocalDateTime endTime);

    List<Booking> findAllByItem_IdInOrderByStartDesc(List<Long> itemIds);

    List<Booking> findAllByItem_IdInAndStartBeforeAndEndAfterOrderByStartDesc(List<Long> itemIds, LocalDateTime dateStart, LocalDateTime dateEnd);

    List<Booking> findAllByItem_IdInAndStartAfterOrderByStartDesc(List<Long> itemIds, LocalDateTime dateStart);

    List<Booking> findAllByItem_IdInAndStatusInOrderByStartDesc(List<Long> itemIds, List<BookingStatus> statuses);

    List<Booking> findAllByItem_IdInAndStatusInAndEndBeforeOrderByStartDesc(List<Long> itemIds, List<BookingStatus> statuses, LocalDateTime dateEnd);

    Booking findBookingByBooker_IdAndItem_IdAndEndBeforeOrderByStartDesc(Long userId, Long itemId, LocalDateTime dateEnd);
}
