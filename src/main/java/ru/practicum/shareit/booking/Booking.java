package ru.practicum.shareit.booking;

import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.item.Item;
import java.time.LocalDate;

@Data
@NoArgsConstructor
public class Booking {

    private int id;
    private LocalDate start;
    private LocalDate end;
    private Item item;
    private int bookerId;
    private BookingStatus status;
}
