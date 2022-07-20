package ru.practicum.shareit.item;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ModelNotFoundException;
import ru.practicum.shareit.exception.UserIsNotOwnerException;
import ru.practicum.shareit.item.comment.Comment;
import ru.practicum.shareit.item.comment.CommentRepository;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

@Service
@AllArgsConstructor
public class ItemService {

    private final ItemRepository repository;
    private final CommentRepository commentRepository;

    public void add(Item item) {
        repository.save(item);
    }

    public Item getByItemId(int itemId) {
        Optional<Item> itemOptional = repository.findById(itemId);
        if (itemOptional.isPresent()) {
            return itemOptional.get();
        } else {
            throw new ModelNotFoundException(String.format("Item id: %s not found", itemId));
        }
    }

    public Collection<Item> getAllByOwnerId(int userId) {
        return repository.getItemsByOwnerId(userId);
    }

    @Transactional
    public void update(Item item, int userId) {
        int ownerId = item.getOwnerId();
        checkIds(ownerId, userId);
        repository.setItemInfoById(item.getName(), item.getDescription(), item.isAvailable(), userId);
    }

    public void delete(int itemId, int userId) {
        Item item = getByItemId(itemId);
        int ownedId = item.getOwnerId();
        checkIds(ownedId, userId);
        repository.deleteById(itemId);
    }

    public Collection<Item> getItemsBySearch(String text) {
        if (text.isEmpty()) {
            return new ArrayList<>();
        }
        text = "%" + text + "%";
        return repository.findItemByText(text);
    }

    public Comment addComment(Comment comment) {
        System.out.println(comment);
        return commentRepository.save(comment);
    }

    public Collection<Comment> getComments(int itemId) {
        return commentRepository.findAllByItemId(itemId);
    }

    public void checkIds(int ownerId, int userId) {
        if (ownerId != userId) {
            throw new UserIsNotOwnerException(String.format("User id: %s is not owner for this item", userId));
        }
    }
}