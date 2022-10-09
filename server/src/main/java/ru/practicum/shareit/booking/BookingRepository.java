package ru.practicum.shareit.booking;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findAllByBooker_Id(Long userId, PageRequest pageRequest);

    List<Booking> findAllByBooker_IdAndStartBeforeAndEndAfter(Long userId, LocalDateTime dateStart, LocalDateTime dateEnd, PageRequest pageRequest);

    List<Booking> findAllByBooker_IdAndStartAfter(Long userId, LocalDateTime dateStart, PageRequest pageRequest);

    List<Booking> findAllByBooker_IdAndStatus(Long userId, BookingStatus status, PageRequest pageRequest);

    List<Booking> findAllByBooker_IdAndStatusInAndEndBefore(Long userId, List<BookingStatus> statuses, LocalDateTime endTime, PageRequest pageRequest);

    List<Booking> findAllByItem_IdIn(List<Long> itemIds, PageRequest pageRequest);

    List<Booking> findAllByItem_IdInAndStartBeforeAndEndAfter(List<Long> itemIds, LocalDateTime dateStart, LocalDateTime dateEnd, PageRequest pageRequest);

    List<Booking> findAllByItem_IdInAndStartAfter(List<Long> itemIds, LocalDateTime dateStart, PageRequest pageRequest);

    List<Booking> findAllByItem_IdInAndStatusIn(List<Long> itemIds, List<BookingStatus> statuses, PageRequest pageRequest);

    List<Booking> findAllByItem_IdInAndStatusInAndEndBefore(List<Long> itemIds, List<BookingStatus> statuses, LocalDateTime dateEnd, PageRequest pageRequest);

    List<Booking> findBookingByBooker_IdAndItem_IdAndEndBefore(Long userId, Long itemId, LocalDateTime dateEnd);
}
