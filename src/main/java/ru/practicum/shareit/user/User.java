package ru.practicum.shareit.user;

import lombok.*;

import javax.validation.constraints.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class User {

    @EqualsAndHashCode.Exclude
    private int id;
    private String name;

    @NotNull(message = "Email cannot be null")
    @Email(regexp = "[a-z0-9A-Z._%+-]+@[a-z0-9.-]+\\.[a-z]{2,3}", message = "Wrong e-mail format")
    @NotBlank(message = "Email cannot be empty")
    private String email;
}
