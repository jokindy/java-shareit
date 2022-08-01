package ru.practicum.shareit.booking;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;

public interface BookingRepository extends JpaRepository<Booking, Integer> {

    String QUERY_FOR_ALL = "select b.id, b.start_date_time, b.end_date_time, b.item_id, b.booker_id, b.status" +
            " from bookings b" +
            "         left outer join items i on b.item_id = i.id\n" +
            "         left outer join users u on u.id = i.owner_id\n" +
            "where u.id = ?1";

    String QUERY_FOR_CURRENT = QUERY_FOR_ALL + " and b.start_date_time < ?2 and b.end_date_time > ?3";

    String QUERY_FOR_PAST = QUERY_FOR_ALL + " and b.end_date_time < ?2";

    String QUERY_FOR_FUTURE = QUERY_FOR_ALL + " and b.start_date_time > ?2";

    String QUERY_FOR_WAITING = QUERY_FOR_ALL + " and b.status = 'WAITING'";

    String QUERY_FOR_REJECTED = QUERY_FOR_ALL + " and b.status = 'REJECTED'";

    @Modifying
    @Query("update Booking b set b.status = ?1 where b.id = ?2")
    void updateBookingInfo(BookingStatus status, int bookingId);

    Page<Booking> findAllByBookerIdOrderByIdDesc(int bookerId, Pageable pageable);

    Page<Booking> findAllByEndIsAfterAndStartIsBeforeAndBookerIdOrderByIdDesc(LocalDateTime end, LocalDateTime start,
                                                                              int bookerId, Pageable pageable);

    Page<Booking> findAllByEndIsBeforeAndBookerIdOrderByIdDesc(LocalDateTime now, int bookerId, Pageable pageable);

    Page<Booking> findAllByStartIsAfterAndBookerIdOrderByIdDesc(LocalDateTime now, int bookerId, Pageable pageable);

    Page<Booking> findAllByStatusAndBookerIdOrderByIdDesc(BookingStatus status, int bookerId, Pageable pageable);

    @Query(value = QUERY_FOR_ALL, nativeQuery = true)
    Page<Booking> findAllByOwner(int userId, Pageable pageable);

    @Query(value = QUERY_FOR_CURRENT, nativeQuery = true)
    Page<Booking> findCurrentByOwner(int userId, LocalDateTime end, LocalDateTime start, Pageable pageable);

    @Query(value = QUERY_FOR_PAST, nativeQuery = true)
    Page<Booking> findPastByOwner(int userId, LocalDateTime now, Pageable pageable);

    @Query(value = QUERY_FOR_FUTURE, nativeQuery = true)
    Page<Booking> findFutureByOwner(int userId, LocalDateTime now, Pageable pageable);

    @Query(value = QUERY_FOR_WAITING, nativeQuery = true)
    Page<Booking> findAllByOwnerAndStatusIsWaiting(int userId, Pageable pageable);

    @Query(value = QUERY_FOR_REJECTED, nativeQuery = true)
    Page<Booking> findAllByOwnerAndStatusIsRejected(int userId, Pageable pageable);
}
