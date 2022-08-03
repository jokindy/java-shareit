package ru.practicum.shareit.request;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ModelNotFoundException;
import ru.practicum.shareit.exception.SizeIsZeroException;
import ru.practicum.shareit.user.UserService;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@AllArgsConstructor
public class ItemRequestService {

    private final ItemRequestRepository repository;
    private final UserService userService;

    public void add(ItemRequest itemRequest) {
        userService.get(itemRequest.getRequesterId());
        log.info("BookingService - saving new item request: {} to DB", itemRequest);
        repository.save(itemRequest);
    }

    public List<ItemRequest> getRequestsByUser(int userId) {
        userService.get(userId);
        log.info("ItemRequestService - getting item requests by user id: {}", userId);
        return repository.findAllByRequesterId(userId);
    }

    public ItemRequest getRequestById(int userId, int requestId) {
        userService.get(userId);
        log.info("ItemRequestService - getting request id: {}", requestId);
        Optional<ItemRequest> requestOptional = repository.findById(requestId);
        if (requestOptional.isPresent()) {
            return requestOptional.get();
        } else {
            throw new ModelNotFoundException(String.format("Request id: %s not found", requestId));
        }
    }

    public List<ItemRequest> getAllRequestsWithPagination(int userId, int from, int size) {
        log.info("ItemRequestService - getting item requests from: {}, size: {}", from, size);
        if (size == 0) {
            throw new SizeIsZeroException("Size can't be a zero");
        }
        Pageable page = PageRequest.of(from, size, Sort.by("created").descending());
        Page<ItemRequest> allRequests = repository.findAllOtherRequests(userId, page);
        return allRequests.getContent();
    }
}