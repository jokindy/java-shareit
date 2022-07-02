package ru.practicum.shareit.item;

import ru.practicum.shareit.item.model.Item;

import java.util.Collection;

public interface ItemStorage {

    void add(Item item);

    Item get(int id);

    Collection<Item> getAll(int userId);

    void update(Item item);

    void delete(int id, int userId);

    Collection<Item> getItemsBySearch(String text);
}
