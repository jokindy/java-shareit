package ru.practicum.shareit.item;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.comment.Comment;
import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.comment.dto.CommentMapper;
import ru.practicum.shareit.item.dto.*;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.util.Collection;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/items")
@AllArgsConstructor
public class ItemController {

    private ItemService itemService;
    private ItemMapper itemMapper;
    private CommentMapper commentMapper;

    @PostMapping
    public ItemOutputDto addItem(@Valid @RequestBody ItemInputDto itemInputDto,
                                 @RequestHeader("X-Sharer-User-Id") int userId) {
        log.info("Add new item by owner - user id: {}", userId);
        Item item = itemMapper.toDomain(itemInputDto, userId);
        itemService.add(item);
        return itemMapper.toOutputDto(item, userId);
    }

    @PatchMapping("/{itemId}")
    public ItemOutputDto updateItem(@RequestBody ItemInputDto itemInputDto,
                                    @RequestHeader("X-Sharer-User-Id") int userId, @PathVariable int itemId) {
        log.info("Update item id: {} by owner - user id: {}", itemId, userId);
        Item item = itemService.getByItemId(itemId);
        Item updatedItem = itemMapper.update(itemInputDto, item);
        itemService.update(updatedItem, userId);
        return itemMapper.toOutputDto(updatedItem, userId);
    }

    @GetMapping("/{itemId}")
    public ItemOutputDto getItem(@PathVariable int itemId, @RequestHeader("X-Sharer-User-Id") int userId) {
        log.info("Get item id: {}", itemId);
        Item item = itemService.getByItemId(itemId);
        return itemMapper.toOutputDto(item, userId);
    }

    @GetMapping
    public List<ItemOutputDto> getAllItems(@RequestHeader("X-Sharer-User-Id") int userId,
                                           @Valid @Positive @RequestParam(defaultValue = "0") int from,
                                           @Valid @Positive @RequestParam(defaultValue = "10") int size) {
        log.info("Get user's id: {} items", userId);
        Collection<Item> list = itemService.getItemsByOwnerIdInPage(userId, from, size);
        return itemMapper.toDtoList(list);
    }

    @DeleteMapping("/{itemId}")
    public String deleteItem(@PathVariable int itemId, @RequestHeader("X-Sharer-User-Id") int userId) {
        log.info("Delete item id: {} by owner - user id: {}", itemId, userId);
        itemService.delete(itemId, userId);
        return String.format("Item id: %s is deleted", itemId);
    }

    @GetMapping("/search")
    public List<ItemOutputDto> getSearchItems(@RequestParam String text,
                                              @Valid @Positive @RequestParam(defaultValue = "0") int from,
                                              @Valid @Positive @RequestParam(defaultValue = "10") int size) {
        log.info("Search items by '{}'", text);
        Collection<Item> list = itemService.getItemsBySearch(text, from, size);
        return itemMapper.toDtoList(list);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto addComment(@Valid @RequestBody CommentDto commentDto, @PathVariable int itemId,
                                 @RequestHeader("X-Sharer-User-Id") int userId) {
        log.info("Add new comment by user id: {} to item id: {}", itemId, userId);
        Comment comment = commentMapper.toDomain(commentDto, itemId, userId);
        itemService.addComment(comment);
        return commentMapper.toDto(comment);
    }
}