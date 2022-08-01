package ru.practicum.shareit.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.ModelNotFoundException;

import java.util.Collection;
import java.util.Optional;

@Slf4j
@Service
public class UserService {

    private final UserRepository repository;

    public UserService(UserRepository userRepository) {
        this.repository = userRepository;
    }

    public User add(User user) {
        log.info("UserService - saving new user: {} to DB", user);
        return repository.save(user);
    }

    @Transactional
    public void update(User user) {
        log.info("UserService - updating user id: {}", user.getId());
        repository.setUserInfoById(user.getName(), user.getEmail(), user.getId());
    }

    public Collection<User> getAll() {
        log.info("UserService - finding all users");
        return repository.findAll();
    }

    public User get(int userId) {
        log.info("UserService - getting user id: {}", userId);
        Optional<User> userOptional = repository.findById(userId);
        if (userOptional.isPresent()) {
            return userOptional.get();
        } else {
            throw new ModelNotFoundException(String.format("User id: %s not found", userId));
        }
    }

    public void delete(int userId) {
        log.info("UserService - deleting user id: {}", userId);
        get(userId);
        repository.deleteById(userId);
    }
}
