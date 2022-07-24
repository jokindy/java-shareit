package ru.practicum.shareit.booking;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ModelNotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.Item;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static ru.practicum.shareit.booking.BookingStatus.*;

@Slf4j
@Service
@AllArgsConstructor
public class BookingService {

    private final BookingRepository repository;
    private final BookingValidationService bookingValidationService;

    public void add(Booking booking) {
        bookingValidationService.isBookingValidOrThrow(booking);
        log.info("BookingService - saving new booking: {} to DB", booking);
        repository.save(booking);
    }

    public Booking getBookingByUser(int bookingId, int userId) {
        Booking booking = get(bookingId);
        bookingValidationService.isUserCanGetBookingOrThrow(booking, userId);
        return booking;
    }

    @Transactional
    public Booking updateStatus(int bookingId, int userId, boolean isApproved) {
        Booking booking = get(bookingId);
        bookingValidationService.isInputIdsIsValidOrThrow(booking.getItemId(), userId, booking.getBookerId());
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

    public Collection<Booking> getBookingsByUser(int userId, BookingState state) {
        log.info("BookingService - getting bookings by user id: {} and state: {}", userId, state);
        LocalDateTime now = LocalDateTime.now();
        switch (state) {
            case CURRENT:
                return repository.findAllByEndIsAfterAndStartIsBeforeAndBookerId(now, now, userId);
            case PAST:
                return repository.findAllByEndIsBeforeAndBookerId(now, userId);
            case FUTURE:
                return repository.findAllByStartIsAfterAndBookerId(now, userId);
            case WAITING:
                return repository.findAllByStatusAndBookerId(WAITING, userId);
            case REJECTED:
                return repository.findAllByStatusAndBookerId(REJECTED, userId);
            default:
                return repository.findAllByBookerIdOrderByIdDesc(userId);
        }
    }

    public Collection<Booking> getBookingsByOwner(int userId, BookingState state) {
        Collection<Item> items = bookingValidationService.getListOfUserItemsOrThrow(userId);
        log.info("BookingService - getting bookings by owner and state: {}", state);
        LocalDateTime now = LocalDateTime.now();
        List<Booking> list = new ArrayList<>();
        for (Item item : items) {
            list.addAll(item.getBookings());
        }
        Collections.reverse(list);
        switch (state) {
            case CURRENT:
                return list.stream()
                        .filter(booking -> booking.getStart().isBefore(now) && booking.getEnd().isAfter(now))
                        .collect(Collectors.toList());
            case PAST:
                return list.stream()
                        .filter(booking -> booking.getEnd().isBefore(now))
                        .collect(Collectors.toList());
            case FUTURE:
                return list.stream()
                        .filter(booking -> booking.getStart().isAfter(now))
                        .collect(Collectors.toList());
            case WAITING:
                return list.stream()
                        .filter(booking -> booking.getStatus().equals(WAITING))
                        .collect(Collectors.toList());
            case REJECTED:
                return list.stream()
                        .filter(booking -> booking.getStatus().equals(REJECTED))
                        .collect(Collectors.toList());
            default:
                return list;
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
}
