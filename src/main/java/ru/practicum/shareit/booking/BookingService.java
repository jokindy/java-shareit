package ru.practicum.shareit.booking;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ModelNotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.Item;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static ru.practicum.shareit.booking.BookingStatus.*;

@Service
public class BookingService {

    private final BookingRepository repository;

    public BookingService(BookingRepository repository) {
        this.repository = repository;
    }

    public Booking add(Booking booking) {
        return repository.save(booking);
    }

    public Booking get(int bookingId) {
        Optional<Booking> bookingOptional = repository.findById(bookingId);
        if (bookingOptional.isPresent()) {
            return bookingOptional.get();
        } else {
            throw new ModelNotFoundException(String.format("Booking id: %s not found", bookingId));
        }
    }

    @Transactional
    public void updateStatus(int bookingId, boolean isApproved, Booking booking) {
        BookingStatus status = isApproved ? APPROVED : REJECTED;
        if (booking.getStatus().equals(status)) {
            throw new ValidationException("Availability is already changed");
        }
        booking.setStatus(status);
        repository.updateBookingInfo(status, bookingId);
    }

    public Collection<Booking> getBookingsByUser(int userId, BookingState state) {
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

    public Collection<Booking> getBookingsByOwner(Collection<Item> items, BookingState state) {
        LocalDateTime now = LocalDateTime.now();
        List<Booking> list = new ArrayList<>();
        for (Item item : items) {
            list.addAll(item.getBookings());
        }
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
                Collections.reverse(list);
                return list;
        }
    }
}
