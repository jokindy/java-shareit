package ru.practicum.shareit.item;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.exception.ModelNotFoundException;
import ru.practicum.shareit.exception.UserIsNotBookerException;
import ru.practicum.shareit.exception.UserIsNotOwnerException;
import ru.practicum.shareit.item.comment.Comment;
import ru.practicum.shareit.item.comment.CommentRepository;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static ru.practicum.shareit.booking.BookingStatus.APPROVED;

@Slf4j
@Service
@AllArgsConstructor
public class ItemService {

    private final ItemRepository repository;
    private final CommentRepository commentRepository;

    public void add(Item item) {
        log.info("ItemService - saving new item to DB");
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

    public Collection<Item> getAllByOwnerId(int userId) {
        log.info("ItemService - finding items by owner id: {} ", userId);
        return repository.getItemsByOwnerId(userId);
    }

    @Transactional
    public void update(Item item, int userId) {
        log.info("ItemService - updating item id: {} by owner id: {}", item.getId(), userId);
        int ownerId = item.getOwnerId();
        checkIds(ownerId, userId);
        repository.setItemInfoById(item.getName(), item.getDescription(), item.isAvailable(), userId);
    }

    public void delete(int itemId, int userId) {
        log.info("ItemService - deleting item id: {} by owner id: {}", itemId, userId);
        Item item = getByItemId(itemId);
        int ownedId = item.getOwnerId();
        checkIds(ownedId, userId);
        repository.deleteById(itemId);
    }

    public Collection<Item> getItemsBySearch(String text) {
        log.info("ItemService - searching items by text: {}", text);
        if (text.isEmpty()) {
            return new ArrayList<>();
        }
        text = "%" + text + "%";
        return repository.findItemByText(text);
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

    private void isUserAbleToCommentOrThrow(int itemId, int userId) {
        log.info("ItemService - checking is user id: {} able to leave comment to item id: {}", userId, itemId);
        Item item = getByItemId(itemId);
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
    }

    private <T> Predicate<T> distinctByKey(Function<? super T, ?> keyExtractor) {
        Map<Object, Boolean> seen = new ConcurrentHashMap<>();
        return t -> seen.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
    }
}