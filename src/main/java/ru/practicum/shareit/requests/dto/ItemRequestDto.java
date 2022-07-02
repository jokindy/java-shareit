package ru.practicum.shareit.requests.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ItemRequestDto {

    private int id;
    private String description;
    private int requestorId;
    private int itemId;
}
