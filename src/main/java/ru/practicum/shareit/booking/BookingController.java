package ru.practicum.shareit.booking;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserService;

@RestController
@RequestMapping(path = "/bookings")
@AllArgsConstructor
public class BookingController {

    private final ItemService itemService;
    private final UserService userService;
    private final BookingMapper bookingMapper;

    @PostMapping
    public Booking add(@RequestBody BookingDto bookingDto) {
        // проверка маппинга бронирования
        User user = userService.get(bookingDto.getBookerId());
        Item item = itemService.get(bookingDto.getItemId());
        Booking booking = bookingMapper.toDomain(bookingDto);
        booking.setBooker(user);
        booking.setItem(item);
        return booking;
    }
}
