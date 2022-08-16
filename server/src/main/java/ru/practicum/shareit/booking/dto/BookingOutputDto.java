package ru.practicum.shareit.booking.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.item.dto.ItemShortDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class BookingOutputDto {

    private Integer id;
    private LocalDateTime start;
    private LocalDateTime end;
    private ItemShortDto item;
    private UserDto booker;
    private BookingStatus status;
}
