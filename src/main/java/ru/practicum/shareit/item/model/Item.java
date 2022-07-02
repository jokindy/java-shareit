package ru.practicum.shareit.item.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode
public class Item {

    @EqualsAndHashCode.Exclude
    private int id;
    private String name;
    private String description;
    private boolean available;
    private int ownerUserId;

    @EqualsAndHashCode.Exclude
    private List<Integer> requests;
}
