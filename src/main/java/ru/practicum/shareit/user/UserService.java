package ru.practicum.shareit.user;

import org.springframework.stereotype.Service;

import java.util.Collection;

@Service
public class UserService {

    private final UserStorage userStorage;

    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public void add(User user) {
        userStorage.add(user);
    }

    public void update(User user) {
        userStorage.update(user);
    }

    public Collection<User> getAll() {
        return userStorage.getAll();
    }

    public User get(int userId) {
        return userStorage.get(userId);
    }

    public void delete(int userId) {
        userStorage.delete(userId);
    }
}
