package ru.practicum.shareit.user;

import java.util.Collection;

public interface UserStorage {

    void add(User user);

    User get(int id);

    void update(User user);

    void delete(int id);

    Collection<User> getAll();
}
