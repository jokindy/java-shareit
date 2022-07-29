package ru.practicum.shareit.booking;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Integer> {

    @Modifying
    @Query("update Booking b set b.status = ?1 where b.id = ?2")
    void updateBookingInfo(BookingStatus status, int bookingId);

    List<Booking> findAllByBookerIdOrderByIdDesc(int bookerId);

    List<Booking> findAllByEndIsAfterAndStartIsBeforeAndBookerId(LocalDateTime end, LocalDateTime start, int bookerId);

    List<Booking> findAllByEndIsBeforeAndBookerId(LocalDateTime now, int bookerId);

    List<Booking> findAllByStartIsAfterAndBookerId(LocalDateTime now, int bookerId);

    List<Booking> findAllByStatusAndBookerId(BookingStatus status, int bookerId);
}
