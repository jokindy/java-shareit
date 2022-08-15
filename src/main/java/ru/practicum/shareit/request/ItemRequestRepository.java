package ru.practicum.shareit.request;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ItemRequestRepository extends JpaRepository<ItemRequest, Integer> {

    List<ItemRequest> findAllByRequesterId(int userId);

    @Query("select i from ItemRequest i where i.requesterId <> ?1")
    Page<ItemRequest> findAllOtherRequests(int userId, Pageable pageable);
}
