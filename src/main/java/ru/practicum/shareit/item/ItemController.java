package ru.practicum.shareit.item;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.user.UserService;

import javax.validation.Valid;
import java.util.Collection;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/items")
@AllArgsConstructor
public class ItemController {

    private ItemService itemService;
    private UserService userService;
    private ItemMapper itemMapper;

    @PostMapping
    public ItemDto addItem(@Valid @RequestBody ItemDto itemDto, @RequestHeader("X-Sharer-User-Id") int userId) {
        log.info("Add new item by owner - user id: {}", userId);
        Item item = itemMapper.toDomain(itemDto);
        userService.get(userId);
        item.setOwnerId(userId);
        itemService.add(item);
        itemDto.setId(item.getId());
        return itemDto;
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@RequestBody ItemDto itemDto, @RequestHeader("X-Sharer-User-Id") int userId,
                              @PathVariable int itemId) {
        log.info("Update item id: {} by owner - user id: {}", itemId, userId);
        Item item = new Item(itemService.get(itemId));
        Item updatedItem = itemMapper.update(itemDto, item);
        itemService.update(updatedItem, userId);
        return itemMapper.toDto(updatedItem);
    }

    @GetMapping("/{itemId}")
    public ItemDto getItem(@PathVariable int itemId) {
        log.info("Get item id: {}", itemId);
        Item item = itemService.get(itemId);
        return itemMapper.toDto(item);
    }

    @GetMapping
    public List<ItemDto> getAllItems(@RequestHeader("X-Sharer-User-Id") int userId) {
        log.info("Get user's id: {} items", userId);
        userService.get(userId);
        Collection<Item> list = itemService.getAll(userId);
        return itemMapper.toDtoList(list);
    }

    @DeleteMapping("/{itemId}")
    public String deleteItem(@PathVariable int itemId, @RequestHeader("X-Sharer-User-Id") int userId) {
        log.info("Delete item id: {} by owner - user id: {}", itemId, userId);
        userService.get(userId);
        itemService.delete(itemId, userId);
        return String.format("Item id: %s is deleted", itemId);
    }

    @GetMapping("/search")
    public List<ItemDto> getSearchItems(@RequestParam String text) {
        log.info("Search items by '{}'", text);
        Collection<Item> list = itemService.getItemsBySearch(text);
        return itemMapper.toDtoList(list);
    }
}
