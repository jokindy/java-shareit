package ru.practicum.shareit.booking;

import lombok.AllArgsConstructor;
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
@AllArgsConstructor
public class BookingService {

    private final BookingRepository repository;

    public void add(Booking booking) {
        repository.save(booking);
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
    public BookingStatus updateStatus(int bookingId, boolean isApproved, BookingStatus oldStatus) {
        BookingStatus newStatus = isApproved ? APPROVED : REJECTED;
        if (oldStatus.equals(newStatus)) {
            throw new ValidationException("Availability is already changed");
        }
        repository.updateBookingInfo(newStatus, bookingId);
        return newStatus;
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
}
