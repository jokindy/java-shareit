package ru.practicum.shareit.item;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ModelAlreadyExistException;
import ru.practicum.shareit.exception.UserIsNotOwnerException;
import ru.practicum.shareit.item.model.Item;

import java.util.Collection;

@Service
public class ItemService {

    private final ItemStorage itemStorage;

    public ItemService(ItemStorage itemStorage) {
        this.itemStorage = itemStorage;
    }

    public void add(Item item) {
        itemStorage.add(item);
    }

    public Item get(int itemId) {
        return itemStorage.get(itemId);
    }

    public Collection<Item> getAll(int userId) {
        return itemStorage.getAll(userId);
    }

    public void update(Item item) {
        int itemId = item.getId();
        Item anotherItem = get(itemId);
        int ownerId = anotherItem.getOwnerUserId();
        int userId = item.getOwnerUserId();
        checkIds(ownerId, userId);
        if (item.equals(anotherItem)) {
            throw new ModelAlreadyExistException("Same item");
        }
        itemStorage.update(item);
    }

    public void delete(int itemId, int userId) {
        Item item = get(itemId);
        int ownedId = item.getOwnerUserId();
        checkIds(ownedId, userId);
        itemStorage.delete(itemId, userId);
    }

    public Collection<Item> getItemsBySearch(String text) {
        return itemStorage.getItemsBySearch(text);
    }

    private void checkIds(int ownerId, int userId) {
        if (ownerId != userId) {
            throw new UserIsNotOwnerException(String.format("User id: %s is not owner for this item", userId));
        }
    }
}