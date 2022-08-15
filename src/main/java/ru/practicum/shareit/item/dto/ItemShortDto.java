package ru.practicum.shareit.item.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ItemShortDto {

    private int id;
    private String name;
    private String description;
    private boolean available;
    private Integer requestId;
    private int ownerId;
}
