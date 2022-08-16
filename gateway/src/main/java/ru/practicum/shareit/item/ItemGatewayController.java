package ru.practicum.shareit.item;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Slf4j
@RestController
@RequestMapping("/items")
@AllArgsConstructor
public class ItemGatewayController {

    private final ItemClient itemClient;

    @PostMapping
    public Object addItem(@Valid @RequestBody ItemDto itemDto,
                          @RequestHeader("X-Sharer-User-Id") int userId) {
        log.info("Add new item by owner - user id: {}", userId);
        return itemClient.addItem(itemDto, userId);
    }

    @PatchMapping("/{itemId}")
    public Object updateItem(@RequestBody ItemDto itemDto,
                             @RequestHeader("X-Sharer-User-Id") int userId, @PathVariable int itemId) {
        log.info("Update item id: {} by owner - user id: {}", itemId, userId);
        return itemClient.updateItem(itemDto, itemId, userId);
    }

    @GetMapping("/{itemId}")
    public Object getItem(@PathVariable int itemId, @RequestHeader("X-Sharer-User-Id") int userId) {
        log.info("Get item id: {}", itemId);
        return itemClient.get(userId, itemId);
    }

    @GetMapping
    public Object getAllItems(@RequestHeader("X-Sharer-User-Id") int userId,
                              @Valid @PositiveOrZero @RequestParam(defaultValue = "0") int from,
                              @Valid @Positive @RequestParam(defaultValue = "10") int size) {
        log.info("Get user's id: {} items", userId);
        return itemClient.getAll(userId, from, size);
    }

    @DeleteMapping("/{itemId}")
    public Object deleteItem(@PathVariable int itemId, @RequestHeader("X-Sharer-User-Id") int userId) {
        log.info("Delete item id: {} by owner - user id: {}", itemId, userId);
        return null;
    }

    @GetMapping("/search")
    public Object getSearchItems(@RequestHeader("X-Sharer-User-Id") int userId,
                                 @RequestParam String text,
                                 @Valid @PositiveOrZero @RequestParam(defaultValue = "0") int from,
                                 @Valid @Positive @RequestParam(defaultValue = "10") int size) {
        log.info("Search items by '{}'", text);
        return itemClient.getBySearch(text, from, size, userId);
    }

    @PostMapping("/{itemId}/comment")
    public Object addComment(@Valid @RequestBody CommentDto commentDto, @PathVariable int itemId,
                             @RequestHeader("X-Sharer-User-Id") int userId) {
        log.info("Add new comment by user id: {} to item id: {}", itemId, userId);
        return itemClient.addComment(commentDto, itemId, userId);
    }
}
