package ru.practicum.shareit.user.dto;

import lombok.Data;

import javax.validation.constraints.Email;

@Data
public class UserDto {

    private int id;
    private String name;

    @Email(regexp = "[a-z0-9A-Z._%+-]+@[a-z0-9.-]+\\.[a-z]{2,3}", message = "Wrong e-mail format")
    private String email;
}
