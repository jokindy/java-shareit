package ru.practicum.shareit.user;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;

import java.util.Collection;

@Slf4j
@RestController
@RequestMapping(path = "/users")
@AllArgsConstructor
public class UserController {

    private UserService userService;
    private UserMapper userMapper;

    @PostMapping
    public UserDto addUser(@RequestBody UserDto userDto) {
        log.info("Add new user");
        User user = userMapper.toDomain(userDto);
        return userMapper.toDto(userService.add(user));
    }

    @PatchMapping("/{userId}")
    public User updateUser(@RequestBody UserDto userDto, @PathVariable int userId) {
        log.info("Update user id: {}", userId);
        User anotherUser = userService.get(userId);
        userMapper.prepareDto(userDto, anotherUser);
        User updatedUser = userMapper.toDomain(userDto);
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
    public void deleteUser(@PathVariable int userId) {
        log.info("Delete user id: {}", userId);
        userService.delete(userId);
    }
}