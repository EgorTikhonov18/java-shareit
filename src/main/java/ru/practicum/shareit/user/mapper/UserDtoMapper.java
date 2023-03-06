package ru.practicum.shareit.user.mapper;

import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UserDto;

public class UserDtoMapper {
    public static UserDto userToUserDto(User user) {
        return UserDto.builder().id(user.getId()).name(user.getName()).email(user.getEmail()).build();
    }
}