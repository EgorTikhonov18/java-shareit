package ru.practicum.shareit.user;


import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

@RestController
@Slf4j
@RequestMapping(path = "/users")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserController {
    final UserService userService;

    final String pathUserId = "/{userId}";


    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public UserDto addNewUser(@RequestBody User user) {
        log.info("Поступил запрос на создание нового пользователя");
        return userService.addUser(user);
    }


    @PatchMapping(pathUserId)
    public UserDto updateUser(@PathVariable long userId, @RequestBody User user) {
        log.info(String.format("%s %d", "Поступил запрос на изменение пользователя с id =", userId));
        return userService.updateUser(user, userId);
    }


    @GetMapping
    public List<UserDto> getAllUsers() {
        log.info("Поступил запрос на вывод всех пользователей");
        return userService.getAllUsers();
    }

    @GetMapping(pathUserId)
    public UserDto getUserById(@PathVariable long userId) {
        log.info(String.format("%s %d", "Поступил запрос на вывод пользователя с id =", userId));
        return userService.getUserById(userId);
    }


    @DeleteMapping(pathUserId)
    public void deleteUser(@PathVariable long userId) {
        log.info(String.format("%s %d", "Поступил запрос на удаление пользователя с id =", userId));
        userService.deleteUser(userId);
    }
}
