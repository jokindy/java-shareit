package ru.practicum.shareit.booking.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.BookingStatus;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class BookingShortDto {

    private Integer id;
    private LocalDateTime start;
    private LocalDateTime end;
    private int bookerId;
    private BookingStatus status;
}
