package ru.practicum.shareit.user;

import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface UserService {
    UserDto addUser(User user);

    UserDto updateUser(User user, long userId);

    List<UserDto> getAllUsers();

    UserDto getUserById(long userId);

    void deleteUser(long userId);
}
