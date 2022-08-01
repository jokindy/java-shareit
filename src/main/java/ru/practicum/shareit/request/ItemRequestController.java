package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestMapper;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
public class ItemRequestController {

    private final ItemRequestService itemRequestService;
    private final ItemRequestMapper requestMapper;

    @PostMapping
    public ItemRequestDto add(@Valid @RequestBody ItemRequestDto itemRequestDto,
                              @RequestHeader("X-Sharer-User-Id") int userId) {
        itemRequestDto.setRequesterId(userId);
        itemRequestDto.setCreated(LocalDateTime.now());
        ItemRequest request = requestMapper.toDomain(itemRequestDto);
        itemRequestService.add(request);
        return requestMapper.toDto(request);
    }

    @GetMapping
    public List<ItemRequestDto> getRequestsByUserId(@RequestHeader("X-Sharer-User-Id") int userId) {
        List<ItemRequest> requests = itemRequestService.getRequestsByUser(userId);
        return requestMapper.toDtoList(requests);
    }

    @GetMapping("/{requestId}")
    public ItemRequestDto getRequestsById(@RequestHeader("X-Sharer-User-Id") int userId,
                                          @PathVariable int requestId) {
        ItemRequest request = itemRequestService.getRequestById(userId, requestId);
        return requestMapper.toDto(request);
    }

    @GetMapping("/all")
    public List<ItemRequestDto> getAllRequests(@RequestHeader("X-Sharer-User-Id") int userId,
                                               @Valid @Positive @RequestParam(defaultValue = "0") int from,
                                               @Valid @Positive @RequestParam(defaultValue = "10") int size) {
        List<ItemRequest> requests = itemRequestService.getAllRequestsWithPagination(userId, from, size);
        return requestMapper.toDtoList(requests);
    }
}