package ru.practicum.shareit.user;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.ModelAlreadyExistException;
import ru.practicum.shareit.exception.ModelNotFoundException;

import java.util.*;

@Repository
public class UserStorageImpl implements UserStorage {

    private final Map<Integer, User> users;
    private final List<String> emails;
    private static int id = 0;

    public UserStorageImpl() {
        this.users = new HashMap<>();
        this.emails = new ArrayList<>();
    }

    @Override
    public void add(User user) {
        String email = user.getEmail();
        if (!emails.contains(email)) {
            id++;
            user.setId(id);
            users.put(id, user);
            emails.add(email);
        } else {
            throw new ModelAlreadyExistException(String.format("User with email: '%s' is already added", email));
        }
    }

    @Override
    public User get(int userId) {
        if (!users.containsKey(userId)) {
            throw new ModelNotFoundException(String.format("User id: %s not found", userId));
        }
        return users.get(userId);
    }

    @Override
    public Collection<User> getAll() {
        return users.values();
    }

    @Override
    public void update(User user) {
        String email = user.getEmail();
        if (!users.containsValue(user)) {
            User storageUser = users.get(user.getId());
            String storageUserEmail = storageUser.getEmail();
            if (email.equals(storageUserEmail)) {
                users.put(user.getId(), user);
            } else {
                if (emails.contains(email)) {
                    throw new ModelAlreadyExistException(String.format("User with email: '%s' is already added", email));
                }
                users.put(user.getId(), user);
                emails.remove(storageUserEmail);
            }
        } else {
            throw new ModelAlreadyExistException("User is the same");
        }
    }

    @Override
    public void delete(int userId) {
        String email = get(userId).getEmail();
        users.remove(userId);
        emails.remove(email);
    }
}