package ru.practicum.shareit.item;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.ModelAlreadyExistException;
import ru.practicum.shareit.exception.ModelNotFoundException;
import ru.practicum.shareit.item.model.Item;

import java.util.*;
import java.util.stream.Collectors;

@Repository
public class ItemStorageImpl implements ItemStorage {

    private final Map<Integer, Map<Integer, Item>> items;
    private static int id = 0;

    public ItemStorageImpl() {
        this.items = new HashMap<>();
    }

    @Override
    public void add(Item item) {
        int userId = item.getOwnerUserId();
        if (!items.containsKey(userId)) {
            Map<Integer, Item> itemMap = new HashMap<>();
            id++;
            item.setId(id);
            itemMap.put(item.getId(), item);
            items.put(id, itemMap);
        } else {
            if (items.get(userId).containsValue(item)) {
                throw new ModelAlreadyExistException("This item is already added");
            }
            id++;
            item.setId(id);
            items.get(userId).put(item.getId(), item);
        }
    }

    @Override
    public Item get(int itemId) {
        for (Map<Integer, Item> map : items.values()) {
            if (map.containsKey(itemId)) {
                return map.get(itemId);
            }
        }
        throw new ModelNotFoundException(String.format("Item id: %s not found", itemId));
    }

    @Override
    public Collection<Item> getAll(int userId) {
        if (!items.containsKey(userId)) {
            throw new ModelNotFoundException(String.format("User id: %s has no items", userId));
        }
        return items.get(userId).values();
    }

    @Override
    public void update(Item item) {
        int userId = item.getOwnerUserId();
        int itemId = item.getId();
        if (!items.containsKey(userId)) {
            throw new ModelNotFoundException(String.format("User id: %s has no items", userId));
        }
        items.get(userId).put(itemId, item);
    }

    @Override
    public void delete(int itemId, int userId) {
        items.get(userId).remove(itemId);
    }

    @Override
    public Collection<Item> getItemsBySearch(String text) {
        if (items.isEmpty()) {
            throw new ModelNotFoundException("Storage is empty");
        }
        return items.values().stream()
                .map(x -> findItem(x, text))
                .collect(Collectors.toList());
    }

    private Item findItem(Map<Integer, Item> map, String text) {
        text = text.toLowerCase(Locale.ROOT);
        for (Item item : map.values()) {
            String description = item.getDescription().toLowerCase(Locale.ROOT);
            String name = item.getName().toLowerCase(Locale.ROOT);
            if (name.contains(text) || description.contains(text)) {
                return item;
            }
        }
        throw new ModelNotFoundException("Can't find any item");
    }
}
