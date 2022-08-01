package ru.practicum.shareit.item.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
public class ItemInputDto {

    private Integer id;

    @NotEmpty(message = "Name can't be empty")
    @NotNull(message = "Name can't be null")
    private String name;

    @NotEmpty(message = "Description can't be empty")
    @NotNull(message = "Description can't be null")
    private String description;

    @NotNull(message = "Available can't be null")
    private Boolean available;

    private Integer requestId;
}
