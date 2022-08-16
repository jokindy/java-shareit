package ru.practicum.shareit.request;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Slf4j
@RestController
@RequestMapping(path = "/requests")
@AllArgsConstructor
public class RequestGatewayController {

    private final RequestClient requestClient;

    @PostMapping
    public Object add(@Valid @RequestBody RequestDto requestDto, @RequestHeader("X-Sharer-User-Id") int userId) {
        log.info("Add ");
        return requestClient.addRequest(requestDto, userId);
    }

    @GetMapping
    public Object getRequestsByUserId(@RequestHeader("X-Sharer-User-Id") int userId) {
        return requestClient.getByUserId(userId);
    }

    @GetMapping("/{requestId}")
    public Object getRequestsById(@RequestHeader("X-Sharer-User-Id") int userId, @PathVariable int requestId) {
        return requestClient.getByRequestId(userId, requestId);
    }

    @GetMapping("/all")
    public Object getAllRequests(@RequestHeader("X-Sharer-User-Id") int userId,
                                 @Valid @PositiveOrZero @RequestParam(defaultValue = "0") int from,
                                 @Valid @Positive @RequestParam(defaultValue = "10") int size) {
        return requestClient.getAll(userId, from, size);
    }
}
