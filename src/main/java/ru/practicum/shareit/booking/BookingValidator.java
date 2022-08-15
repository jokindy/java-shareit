package ru.practicum.shareit.booking;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ItemIsNotAvailableException;
import ru.practicum.shareit.exception.ModelNotFoundException;
import ru.practicum.shareit.exception.UserIsNotOwnerException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.user.UserService;

import java.util.Collection;

@Slf4j
@Service
@AllArgsConstructor
public class BookingValidator {

    private final UserService userService;
    private final ItemService itemService;

    public void isBookingValidOrThrow(Booking booking) {
        log.info("BookingValidationService - Check booking before saving in DB");
        userService.get(booking.getBookerId());
        Item item = itemService.getByItemId(booking.getItemId());
        boolean available = item.isAvailable();
        if (!available) {
            throw new ItemIsNotAvailableException("This item is not available");
        }
        if (booking.getBookerId() == item.getOwnerId()) {
            throw new UserIsNotOwnerException("Owner and booker is the same");
        }
    }

    public void isInputIdsIsValidOrThrow(int itemId, int ownerId, int bookerId) {
        log.info("BookingValidationService - Check input id's before getting booking");
        userService.get(ownerId);
        userService.get(bookerId);
        Item item = itemService.getByItemId(itemId);
        int ownerDbId = item.getOwnerId();
        if (ownerDbId != ownerId) {
            throw new UserIsNotOwnerException(String.format("User id: %s is not owner/booker for this item", ownerId));
        }
    }

    public void isUserCanGetBookingOrThrow(Booking booking, int userId) {
        log.info("BookingValidationService - Check is user id: {} has permission to get booking", userId);
        userService.get(userId);
        Item item = itemService.getByItemId(booking.getItemId());
        int ownerId = item.getOwnerId();
        int bookerId = booking.getBookerId();
        if (ownerId != userId && bookerId != userId) {
            throw new UserIsNotOwnerException(String.format("User id: %s is not owner/booker for this item", userId));
        }
    }

    public void isUserHasItemsOrThrow(int userId) {
        log.info("BookingValidationService - Check is user id: {} has items to show their bookings", userId);
        Collection<Item> items = itemService.getAllItemsByOwner(userId);
        if (items.isEmpty()) {
            throw new ModelNotFoundException("User don't have any item");
        }
    }
}