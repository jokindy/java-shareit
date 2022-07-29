package ru.practicum.shareit.item;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.comment.Comment;
import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.comment.dto.CommentMapper;
import ru.practicum.shareit.item.dto.*;

import javax.validation.Valid;
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
    public ItemInputDto addItem(@Valid @RequestBody ItemInputDto itemInputDto,
                                @RequestHeader("X-Sharer-User-Id") int userId) {
        log.info("Add new item by owner - user id: {}", userId);
        Item item = itemMapper.toDomain(itemInputDto);
        item.setOwnerId(userId);
        itemService.add(item);
        return itemMapper.toDto(item);
    }

    @PatchMapping("/{itemId}")
    public ItemInputDto updateItem(@RequestBody ItemInputDto itemInputDto, @RequestHeader("X-Sharer-User-Id") int userId,
                                   @PathVariable int itemId) {
        log.info("Update item id: {} by owner - user id: {}", itemId, userId);
        Item item = itemService.getByItemId(itemId);
        Item updatedItem = itemMapper.update(itemInputDto, item);
        itemService.update(updatedItem, userId);
        return itemMapper.toDto(updatedItem);
    }

    @GetMapping("/{itemId}")
    public ItemOutputDto getItem(@PathVariable int itemId, @RequestHeader("X-Sharer-User-Id") int userId) {
        log.info("Get item id: {}", itemId);
        Item item = itemService.getByItemId(itemId);
        return itemMapper.toOutputDto(item, userId);
    }

    @GetMapping
    public List<ItemOutputDto> getAllItems(@RequestHeader("X-Sharer-User-Id") int userId) {
        log.info("Get user's id: {} items", userId);
        Collection<Item> list = itemService.getAllByOwnerId(userId);
        return itemMapper.toDtoList(list);
    }

    @DeleteMapping("/{itemId}")
    public String deleteItem(@PathVariable int itemId, @RequestHeader("X-Sharer-User-Id") int userId) {
        log.info("Delete item id: {} by owner - user id: {}", itemId, userId);
        itemService.delete(itemId, userId);
        return String.format("Item id: %s is deleted", itemId);
    }

    @GetMapping("/search")
    public List<ItemOutputDto> getSearchItems(@RequestParam String text) {
        log.info("Search items by '{}'", text);
        Collection<Item> list = itemService.getItemsBySearch(text);
        return itemMapper.toDtoList(list);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto addComment(@Valid @RequestBody CommentDto commentDto, @PathVariable int itemId,
                                 @RequestHeader("X-Sharer-User-Id") int userId) {
        log.info("Add new comment by user id: {} to item id: {}", itemId, userId);
        Comment comment = commentMapper.toDomain(commentDto);
        comment.setItemId(itemId);
        comment.setAuthorId(userId);
        itemService.addComment(comment);
        return commentMapper.toDto(comment);
    }
}
