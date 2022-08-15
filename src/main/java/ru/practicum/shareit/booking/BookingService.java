package ru.practicum.shareit.booking;

import lombok.AllArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ModelNotFoundException;
import ru.practicum.shareit.exception.SizeIsZeroException;
import ru.practicum.shareit.exception.ValidationException;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.*;

import static ru.practicum.shareit.booking.BookingStatus.*;

@Slf4j
@Service
@Setter
@AllArgsConstructor
public class BookingService {

    private final BookingRepository repository;
    private final BookingValidator bookingValidator;

    public void add(Booking booking) {
        bookingValidator.isBookingValidOrThrow(booking);
        log.info("BookingService - saving new booking: {} to DB", booking);
        repository.save(booking);
    }

    public Booking getBookingByUser(int bookingId, int userId) {
        Booking booking = get(bookingId);
        bookingValidator.isUserCanGetBookingOrThrow(booking, userId);
        return booking;
    }

    @Transactional
    public Booking updateStatus(int bookingId, int userId, boolean isApproved) {
        Booking booking = get(bookingId);
        bookingValidator.isInputIdsIsValidOrThrow(booking.getItemId(), userId, booking.getBookerId());
        BookingStatus oldStatus = booking.getStatus();
        log.info("BookingService - updating status for booking id: {}", bookingId);
        BookingStatus newStatus = isApproved ? APPROVED : REJECTED;
        if (oldStatus.equals(newStatus)) {
            throw new ValidationException("Availability is already changed");
        }
        repository.updateBookingInfo(newStatus, bookingId);
        booking.setStatus(newStatus);
        return booking;
    }

    public Page<Booking> getBookingsByUser(int userId, BookingState state, int from, int size) {
        bookingValidator.isUserHasItemsOrThrow(userId);
        log.info("BookingService - getting bookings by user id: {} and state: {}", userId, state);
        LocalDateTime now = LocalDateTime.now();
        Pageable pageable = getPageable(from, size);
        switch (state) {
            case CURRENT:
                return repository.findAllCurrentByBooker(userId, now, pageable);
            case PAST:
                return repository.findAllByEndIsBeforeAndBookerIdOrderByIdDesc(now, userId, pageable);
            case FUTURE:
                return repository.findAllByStartIsAfterAndBookerIdOrderByIdDesc(now, userId, pageable);
            case WAITING:
                return repository.findAllByStatusAndBookerIdOrderByIdDesc(WAITING, userId, pageable);
            case REJECTED:
                return repository.findAllByStatusAndBookerIdOrderByIdDesc(REJECTED, userId, pageable);
            default:
                return repository.findAllByBookerIdOrderByIdDesc(userId, pageable);
        }
    }

    public Page<Booking> getBookingsByOwner(int userId, BookingState state, int from, int size) {
        bookingValidator.isUserHasItemsOrThrow(userId);
        log.info("BookingService - getting bookings by owner and state: {}", state);
        LocalDateTime now = LocalDateTime.now();
        Pageable pageable = getPageable(from, size);
        switch (state) {
            case CURRENT:
                return repository.findAllCurrentByOwner(userId, now, pageable);
            case PAST:
                return repository.findAllPastByOwner(userId, now, pageable);
            case FUTURE:
                return repository.findAllFutureByOwner(userId, now, pageable);
            case WAITING:
                return repository.findAllWaitingByOwner(userId, pageable);
            case REJECTED:
                return repository.findAllRejectedByOwner(userId, pageable);
            default:
                return repository.findAllByOwner(userId, pageable);
        }
    }

    private Booking get(int bookingId) {
        log.info("BookingService - getting booking id: {}", bookingId);
        Optional<Booking> bookingOptional = repository.findById(bookingId);
        if (bookingOptional.isPresent()) {
            return bookingOptional.get();
        } else {
            throw new ModelNotFoundException(String.format("Booking id: %s not found", bookingId));
        }
    }

    private Pageable getPageable(int from, int size) {
        if (size == 0) {
            throw new SizeIsZeroException("Size can't be a zero");
        }
        return PageRequest.of(from, size, Sort.by("id").descending());
    }
}