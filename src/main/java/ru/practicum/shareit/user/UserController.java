package ru.practicum.shareit.user;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Collection;

@Slf4j
@RestController
@RequestMapping(path = "/users")
@AllArgsConstructor
public class UserController {

    private UserService userService;

    @PostMapping
    public User addUser(@Valid @RequestBody User user) {
        log.info("Add new user");
        userService.add(user);
        return user;
    }

    @PutMapping
    public User updateUser(@Valid @RequestBody UserDto userDto) {
        int id = userDto.getId();
        log.info("Update user id: {}", id);
        User anotherUser = userService.get(id);
        User updatedUser = UserDto.toUser(userDto, anotherUser);
        userService.update(updatedUser);
        return updatedUser;
    }

    @GetMapping("/{userId}")
    public User getUser(@PathVariable int userId) {
        log.info("Get user id: {}", userId);
        return userService.get(userId);
    }

    @GetMapping
    public Collection<User> getAllUsers() {
        log.info("Get all users");
        return userService.getAll();
    }


    @DeleteMapping("/{userId}")
    public String deleteUser(@PathVariable int userId) {
        log.info("Delete user id: {}", userId);
        userService.delete(userId);
        return String.format("User id: %s is deleted", userId);
    }
}
