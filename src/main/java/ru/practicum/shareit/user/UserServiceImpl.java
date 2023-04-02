package ru.practicum.shareit.user;


import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.IsAlreadyExistsException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserDtoMapper;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@Qualifier("UserServiceImpl")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserServiceImpl implements UserService {
    final UserRepository userRepository;
    final UserValidation userValidation = new UserValidation();

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDto addUser(User user) {
        if (!userValidation.userValidation(user)) {
            String message = "Поля заполнены неверно";
            log.info(message);
            throw new ValidationException(message);
        }
        try {
            return UserDtoMapper.userToUserDto(userRepository.save(user));
        } catch (Exception e) {
            throw new IsAlreadyExistsException("Пользователь с таким email уже существует");
        }

    }

    @Override
    public UserDto updateUser(User user, long userId) {
        User checkedUser = checkFieldsForUpdate(user, userId);
        if (!userValidation.userValidation(checkedUser)) {
            String message = "Поля заполнены неверно";
            log.info(message);
            throw new ValidationException(message);
        }
        checkedUser.setId(userId);
        return UserDtoMapper.userToUserDto(userRepository.save(checkedUser));
    }

    @Override
    public List<UserDto> getAllUsers() {
        return usersToUsersDto(userRepository.findAll());
    }

    @Override
    public UserDto getUserById(long userId) {
        return UserDtoMapper.userToUserDto(getUser(userId));
    }

    @Override
    public void deleteUser(long userId) {
        getUser(userId);
        userRepository.deleteById(userId);
        log.info(String.format("%s %d %s", "Пользователь с id =", userId, "удалён"));
    }

    private User checkFieldsForUpdate(User user, long userId) {
        User oldUser = getUser(userId);
        if (user.getName() == null) {
            user.setName(oldUser.getName());
        }
        if (user.getEmail() == null) {
            user.setEmail(oldUser.getEmail());
        }
        return user;
    }

    private List<UserDto> usersToUsersDto(List<User> users) {
        List<UserDto> usersDto = new ArrayList<>();
        for (User user : users) {
            usersDto.add(UserDtoMapper.userToUserDto(user));
        }
        return usersDto;
    }

    private User getUser(long userId) {
        if (userRepository.findById(userId).isEmpty()) {
            String message = String.format("%s %d %s", "Пользователь с id =", userId, "не найден");
            log.info(message);
            throw new NotFoundException(message);
        }
        return userRepository.findById(userId).get();
    }
}