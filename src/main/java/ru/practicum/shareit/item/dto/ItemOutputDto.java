package ru.practicum.shareit.item.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.item.comment.dto.CommentDto;

import java.util.List;

@Data
@NoArgsConstructor
public class ItemOutputDto {

    private Integer id;
    private String name;
    private String description;
    private Boolean available;
    private BookingShortDto lastBooking;
    private BookingShortDto nextBooking;
    private Integer requestId;
    private List<CommentDto> comments;
}
