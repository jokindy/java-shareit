package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.user.User;

@Data
@AllArgsConstructor
public class BookingInfo {

    private final User user;
    private final Item item;
}
