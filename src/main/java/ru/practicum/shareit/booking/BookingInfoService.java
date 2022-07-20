package ru.practicum.shareit.booking;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingInfo;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.dto.BookingOutputDto;
import ru.practicum.shareit.exception.ItemIsNotAvailableException;
import ru.practicum.shareit.exception.ModelNotFoundException;
import ru.practicum.shareit.exception.UserIsNotOwnerException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Service
@AllArgsConstructor
public class BookingInfoService {

    private final UserService userService;
    private final ItemService itemService;
    private final ItemRepository itemRepository;

    public BookingInfo isBookingValid(Booking booking) {
        User user = userService.get(booking.getBookerId());
        Item item = itemService.getByItemId(booking.getItemId());
        boolean available = item.isAvailable();
        if (!available) {
            throw new ItemIsNotAvailableException("This item is not available");
        }
        if (booking.getBookerId() == item.getOwnerId()) {
            throw new UserIsNotOwnerException("Owner and booker is the same");
        }
        return new BookingInfo(user, item);
    }

    public BookingInfo getBookingInfoByBookingOrThrow(int itemId, int ownerId, int bookerId) {
        userService.get(ownerId);
        User booker = userService.get(bookerId);
        Item item = itemService.getByItemId(itemId);
        int ownerDbId = item.getOwnerId();
        if (ownerDbId != ownerId) {
            throw new UserIsNotOwnerException(String.format("User id: %s is not owner/booker for this item", ownerId));
        }
        return new BookingInfo(booker, item);
    }

    public BookingInfo isBookingValidOrThrow(Booking booking, int userId) {
        User user = userService.get(userId);
        Item item = itemService.getByItemId(booking.getItemId());
        int ownerId = item.getOwnerId();
        int bookerId = booking.getBookerId();
        if (ownerId != userId && bookerId != userId) {
            throw new UserIsNotOwnerException(String.format("User id: %s is not owner/booker for this item", userId));
        }
        if (bookerId != userId) {
            return new BookingInfo(userService.get(bookerId), item);
        }
        return new BookingInfo(user, item);
    }

    public Collection<BookingOutputDto> getBookings(Collection<Booking> bookings, BookingMapper bookingMapper) {
        List<BookingOutputDto> list = new ArrayList<>();
        for (Booking booking : bookings) {
            User user = userService.get(booking.getBookerId());
            Item item = itemService.getByItemId(booking.getItemId());
            BookingOutputDto dto = bookingMapper.toOutputDto(booking, new BookingInfo(user, item));
            list.add(dto);
        }
        return list;
    }

    public Collection<Item> getListOfUserItemsOrThrow(int userId) {
        userService.get(userId);
        Collection<Item> items = itemRepository.getItemsByOwnerId(userId);
        if (items.isEmpty()) {
            throw new ModelNotFoundException("User don't have any item");
        }
        return items;
    }
}
