package ru.practicum.shareit.booking.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.BookingStatus;

import java.time.LocalDate;

@Data
@NoArgsConstructor
public class BookingDto {

    private int id;
    private LocalDate start;
    private LocalDate end;
    private int itemId;
    private int bookerId;
    private BookingStatus status;
}
