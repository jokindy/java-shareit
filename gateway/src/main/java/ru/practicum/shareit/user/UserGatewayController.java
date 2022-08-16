package ru.practicum.shareit.user;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Slf4j
@RestController
@RequestMapping(path = "/users")
@AllArgsConstructor
public class UserGatewayController {

    private UserClient userClient;

    @PostMapping
    public Object addUser(@Valid @RequestBody UserDto userDto) {
        log.info("GATEWAY - Add new user");
        return userClient.addUser(userDto);
    }

    @PatchMapping("/{userId}")
    public Object updateUser(@RequestBody UserDto userDto, @PathVariable int userId) {
        return userClient.updateUser(userDto, userId);
    }

    @GetMapping("/{userId}")
    public Object getUser(@PathVariable int userId) {
        log.info("Get user id: {}", userId);
        return userClient.getUser(userId);
    }

    @GetMapping
    public Object getAllUsers() {
        log.info("Get all users");
        return userClient.getAll();
    }

    @DeleteMapping("/{userId}")
    public Object deleteUser(@PathVariable int userId) {
        log.info("Delete user id: {}", userId);
        return userClient.deleteUser(userId);
    }
}
