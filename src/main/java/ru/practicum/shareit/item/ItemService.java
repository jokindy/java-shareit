package ru.practicum.shareit.item;

import lombok.AllArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.exception.ModelNotFoundException;
import ru.practicum.shareit.exception.SizeIsZeroException;
import ru.practicum.shareit.exception.UserIsNotBookerException;
import ru.practicum.shareit.exception.UserIsNotOwnerException;
import ru.practicum.shareit.item.comment.Comment;
import ru.practicum.shareit.item.comment.CommentRepository;
import ru.practicum.shareit.user.UserService;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static ru.practicum.shareit.booking.BookingStatus.APPROVED;

@Slf4j
@Service
@Setter
@AllArgsConstructor
public class ItemService {

    private UserService userService;
    private final ItemRepository repository;
    private final CommentRepository commentRepository;

    public void add(Item item) {
        userService.get(item.getOwnerId());
        log.info("ItemService - saving new item: {} to DB", item);
        repository.save(item);
    }

    public Item getByItemId(int itemId) {
        log.info("ItemService - finding item id: {}", itemId);
        Optional<Item> itemOptional = repository.findById(itemId);
        if (itemOptional.isPresent()) {
            return itemOptional.get();
        } else {
            throw new ModelNotFoundException(String.format("Item id: %s not found", itemId));
        }
    }

    public Collection<Item> getItemsByOwnerIdInPage(int userId, int from, int size) {
        userService.get(userId);
        Pageable page = getPageable(from, size);
        log.info("ItemService - finding items by owner id: {} ", userId);
        return repository.getItemsByOwnerId(userId, page).getContent();
    }

    public Collection<Item> getAllItemsByOwner(int userId) {
        userService.get(userId);
        log.info("ItemService - finding items by owner id: {} ", userId);
        return repository.findAllByOwnerId(userId);
    }

    @Transactional
    public void update(Item item, int userId) {
        log.info("ItemService - updating item id: {} by owner id: {}", item.getId(), userId);
        int ownerId = item.getOwnerId();
        checkIds(ownerId, userId);
        repository.setItemInfoById(item.getName(), item.getDescription(), item.isAvailable(), userId);
    }

    public void delete(int itemId, int userId) {
        userService.get(userId);
        log.info("ItemService - deleting item id: {} by owner id: {}", itemId, userId);
        Item item = getByItemId(itemId);
        int ownedId = item.getOwnerId();
        checkIds(ownedId, userId);
        repository.deleteById(itemId);
    }

    public Collection<Item> getItemsBySearch(String text, int from, int size) {
        log.info("ItemService - searching items by text: {}", text);
        if (text.isEmpty()) {
            return new ArrayList<>();
        }
        Pageable page = getPageable(from, size);
        text = "%" + text + "%";
        return repository.findItemByText(text, page).getContent();
    }


    public void addComment(Comment comment) {
        isUserAbleToCommentOrThrow(comment.getItemId(), comment.getAuthorId());
        log.info("ItemService - saving new comment to DB");
        commentRepository.save(comment);
    }

    public void checkIds(int ownerId, int userId) {
        if (ownerId != userId) {
            throw new UserIsNotOwnerException(String.format("User id: %s is not owner for this item", userId));
        }
    }

    public void isUserAbleToCommentOrThrow(int itemId, int userId) {
        log.info("ItemService - checking is user id: {} able to leave comment to item id: {}", userId, itemId);
        userService.get(userId);
        Item item = getByItemId(itemId);
        List<Booking> bookings = item.getBookings();
        Map<Booking, Integer> bookerMap = bookings.stream()
                .collect(Collectors.toMap(e -> e, Booking::getBookerId));
        Booking checkedBooking = null;
        if (bookerMap.containsValue(userId)) {
            for (Booking booking : bookerMap.keySet()) {
                BookingStatus status = booking.getStatus();
                LocalDateTime start = booking.getStart();
                if (status.equals(APPROVED) && start.isBefore(LocalDateTime.now())) {
                    checkedBooking = booking;
                }
            }
        }
        if (checkedBooking == null) {
            throw new UserIsNotBookerException(String.format("User id: %s is not booked this item yet", userId));
        }
    }

    private Pageable getPageable(int from, int size) {
        if (size == 0) {
            throw new SizeIsZeroException("Size can't be a zero");
        }
        return PageRequest.of(from, size, Sort.by("id").descending());
    }
}