package ru.practicum.shareit.user;

import lombok.Data;

import javax.validation.constraints.Email;

@Data
public class UserDto {

    private int id;
    private String name;

    @Email(regexp = "[a-z0-9A-Z._%+-]+@[a-z0-9.-]+\\.[a-z]{2,3}", message = "Wrong e-mail format")
    private String email;

    public static User toUser(UserDto userDto, User user) {
        int id = user.getId();
        String name = userDto.getName() != null ? userDto.getName() : user.getName();
        String email = userDto.getEmail() != null ? userDto.getEmail() : user.getEmail();
        return new User(id, name, email);
    }
}
