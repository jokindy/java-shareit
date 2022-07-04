package ru.practicum.shareit.requests;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.requests.dto.ItemRequestDto;
import ru.practicum.shareit.requests.dto.ItemRequestMapper;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserService;

@RestController
@RequestMapping(path = "/requests")
@AllArgsConstructor
public class ItemRequestController {

    private final ItemService itemService;
    private final UserService userService;
    private final ItemRequestMapper requestMapper;

    @PostMapping
    public ItemRequest add(@RequestBody ItemRequestDto itemRequestDto) {
        // проверка маппинга запросов
        User user = userService.get(itemRequestDto.getRequestorId());
        Item item = itemService.get(itemRequestDto.getItemId());
        ItemRequest itemRequest = requestMapper.toDomain(itemRequestDto);
        itemRequest.setRequestor(user);
        itemRequest.setItem(item);
        return itemRequest;
    }
}