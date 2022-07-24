package ru.practicum.shareit.user.dto;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.user.User;


@Component
public class UserMapper {

    private final ModelMapper modelMapper;

    public UserMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    public User toDomain(UserDto userDto) {
        return modelMapper.map(userDto, User.class);
    }

    public UserDto toDto(User user) {
        return modelMapper.map(user, UserDto.class);
    }

    public void prepareDto(UserDto userDto, User user) {
        if (userDto.getName() == null) {
            userDto.setName(user.getName());
        }
        if (userDto.getEmail() == null) {
            userDto.setEmail(user.getEmail());
        }
        userDto.setId(user.getId());
    }
}
