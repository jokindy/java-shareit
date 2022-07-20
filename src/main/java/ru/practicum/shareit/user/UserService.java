package ru.practicum.shareit.user;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ModelNotFoundException;

import javax.transaction.Transactional;
import java.util.Collection;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository repository;

    public UserService(UserRepository userRepository) {
        this.repository = userRepository;
    }

    @Transactional
    public User add(User user) {
        return repository.save(user);
    }

    @Transactional
    public void update(User user) {
        repository.setUserInfoById(user.getName(), user.getEmail(), user.getId());
    }

    public Collection<User> getAll() {
        return repository.findAll();
    }

    public User get(int userId) {
        Optional<User> userOptional = repository.findById(userId);
        if (userOptional.isPresent()) {
            return userOptional.get();
        } else {
            throw new ModelNotFoundException(String.format("User id: %s not found", userId));
        }
    }

    public void delete(int userId) {
        get(userId);
        repository.deleteById(userId);
    }
}
