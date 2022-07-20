package ru.practicum.shareit.item;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.exception.UserIsNotBookerException;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static ru.practicum.shareit.booking.BookingStatus.*;

@Component
@AllArgsConstructor
public class ItemInfoService {

    private final UserService userService;

    public String getAuthorNameOrThrow(Item item, int userId) {
        User user = userService.get(userId);
        List<Booking> bookings = item.getBookings();
        Map<Integer, Booking> bookerMap = bookings.stream()
                .filter(distinctByKey(Booking::getBookerId))
                .collect(Collectors.toMap(Booking::getBookerId, e -> e));
        if (bookerMap.containsKey(userId)) {
            BookingStatus status = bookerMap.get(userId).getStatus();
            LocalDateTime start = bookerMap.get(userId).getStart();
            if (!status.equals(APPROVED) || start.isAfter(LocalDateTime.now())) {
                throw new UserIsNotBookerException(String.format("User id: %s is not booked this item yet", userId));
            }
        }
        return user.getName();
    }

    private <T> Predicate<T> distinctByKey(Function<? super T, ?> keyExtractor) {
        Map<Object, Boolean> seen = new ConcurrentHashMap<>();
        return t -> seen.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
    }
}
