package ru.practicum.shareit.requests;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class ItemRequest {

    private int id;
    private String description;
    private User requestor;
    private Item item;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime created;
}
