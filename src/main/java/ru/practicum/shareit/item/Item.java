package ru.practicum.shareit.item;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@EqualsAndHashCode
@NoArgsConstructor
public class Item {

    @EqualsAndHashCode.Exclude
    private int id;
    private String name;
    private String description;
    private boolean available;
    private int ownerId;

    @EqualsAndHashCode.Exclude
    private List<Integer> requests;

    public Item(Item item) {
        this.id = item.getId();
        this.name = item.getName();
        this.description = item.getDescription();
        this.available = item.isAvailable();
        this.ownerId = item.getOwnerId();
        this.requests = item.getRequests();
    }
}
