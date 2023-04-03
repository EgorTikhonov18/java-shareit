package ru.practicum.shareit.user.service;


import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import ru.practicum.shareit.exception.IsAlreadyExistsException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.UserServiceImpl;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserDtoMapper;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserServiceTest {
    @Mock
    UserRepository userRepository;
    @InjectMocks
    UserServiceImpl userServiceImpl;
    User user;
    User updatedUser;

    @BeforeEach
    void beforeEach() {
        Mockito.when(userRepository.save(ArgumentMatchers.any())).thenAnswer(input -> input.getArguments()[0]);
        user = User.builder().id(0L).name("userName1").email("userEmail1@mail.ru").build();
        updatedUser = User.builder().id(0L).name("updatedUserName1").email("updatedUserEmail1@mail.ru").build();
    }

    @Test
    void addUserTest_whenUserCorrect_thenSave() {
        long userId = 0L;
        Mockito.when(userRepository.save(user)).thenReturn(user);
        UserDto userDto = userServiceImpl.addUser(user);

        Mockito.verify(userRepository).save(user);
        assertEquals(UserDtoMapper.userToUserDto(user), userDto);
    }

    @Test
    void addUserTest_whenDuplicateEmailUser_thenThrowException() {
        Mockito.when(userRepository.save(ArgumentMatchers.any()))
                .thenThrow(new IsAlreadyExistsException("A user with the same email already exists"));

        Assertions.assertThrows(IsAlreadyExistsException.class,
                () -> userServiceImpl.addUser(user));
        Mockito.verify(userRepository).save(user);
    }

    @Test
    void addUserTest_whenUserNameEmpty_thenThrowException() {
        Mockito.when(userRepository.save(ArgumentMatchers.any()))
                .thenThrow(new ValidationException("The field's value is not valid"));
        user.setName("");

        Assertions.assertThrows(ValidationException.class,
                () -> userServiceImpl.addUser(user));
        Mockito.verify(userRepository, Mockito.never()).save(user);
    }

    @Test
    void addUserTest_whenUserNameIsNull_thenThrowException() {
        Mockito.when(userRepository.save(ArgumentMatchers.any()))
                .thenThrow(new ValidationException("The field's value is not valid"));
        user.setName(null);

        Assertions.assertThrows(ValidationException.class,
                () -> userServiceImpl.addUser(user));
        Mockito.verify(userRepository, Mockito.never()).save(user);
    }

    @Test
    void addUserTest_whenUserNameSpace_thenThrowException() {
        Mockito.when(userRepository.save(ArgumentMatchers.any()))
                .thenThrow(new ValidationException("The field's value is not valid"));
        user.setName(" ");

        Assertions.assertThrows(ValidationException.class,
                () -> userServiceImpl.addUser(user));
        Mockito.verify(userRepository, Mockito.never()).save(user);
    }

    @Test
    void addUserTest_whenUserEmailEmpty_thenThrowException() {
        Mockito.when(userRepository.save(ArgumentMatchers.any()))
                .thenThrow(new ValidationException("The field's value is not valid"));
        user.setEmail("");

        Assertions.assertThrows(ValidationException.class,
                () -> userServiceImpl.addUser(user));
        Mockito.verify(userRepository, Mockito.never()).save(user);
    }

    @Test
    void addUserTest_whenUserEmailIsNull_thenThrowException() {
        Mockito.when(userRepository.save(ArgumentMatchers.any()))
                .thenThrow(new ValidationException("The field's value is not valid"));
        user.setEmail(null);

        Assertions.assertThrows(ValidationException.class,
                () -> userServiceImpl.addUser(user));
        Mockito.verify(userRepository, Mockito.never()).save(user);
    }

    @Test
    void addUserTest_whenUserEmailSpace_thenThrowException() {
        Mockito.when(userRepository.save(ArgumentMatchers.any()))
                .thenThrow(new ValidationException("The field's value is not valid"));
        user.setEmail(" ");

        Assertions.assertThrows(ValidationException.class,
                () -> userServiceImpl.addUser(user));
        Mockito.verify(userRepository, Mockito.never()).save(user);
    }

    @Test
    void addUserTest_whenUserEmailWithoutSymbol_thenThrowException() {
        Mockito.when(userRepository.save(ArgumentMatchers.any()))
                .thenThrow(new ValidationException("The field's value is not valid"));
        user.setEmail("mail.ru");

        Assertions.assertThrows(ValidationException.class,
                () -> userServiceImpl.addUser(user));
        Mockito.verify(userRepository, Mockito.never()).save(user);
    }


    @Test
    void updateItemTest_whenCorrect_thenUpdate() {
        long userId = 0L;
        Mockito.when(userRepository.findById(ArgumentMatchers.any())).thenReturn(Optional.of(user));

        userServiceImpl.updateUser(user, userId);
        Mockito.verify(userRepository).save(ArgumentMatchers.any());
    }

    @Test
    void updateUserTest_whenUserNotFound_thenThrowException() {
        long userId = 999L;
        Assertions.assertThrows(
                NotFoundException.class,
                () -> {
                    Mockito.when(userRepository.findById(ArgumentMatchers.eq(userId))).thenReturn(Optional.empty());
                    userServiceImpl.getUserById(userId);
                }
        );
    }

    @Test
    void getAllUsersTest() {
        List<User> users = List.of(User.builder().name("userName1").email("userEmail1@mail.ru").build());
        Mockito.when(userRepository.findAll()).thenReturn(users);
        List<UserDto> usersDto = userServiceImpl.getAllUsers();
        Mockito.verify(userRepository).findAll();
        Assertions.assertEquals(1, usersDto.size());
        assertEquals(UserDtoMapper.userToUserDto(users.get(0)), usersDto.get(0));
    }

    @Test
    void getUserByIdTest_whenUserPresent_thenUser() {
        long userId = 0L;
        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        UserDto userDto = UserDtoMapper.userToUserDto(user);
        UserDto userDtoFromDb = userServiceImpl.getUserById(userId);

        Mockito.verify(userRepository, Mockito.times(2)).findById(userId);
        assertEquals(userDto, userDtoFromDb);
    }

    @Test
    void getUserByIdTest_whenUserNotFound_thenThrowException() {
        long userId = 0L;
        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.empty());

        Assertions.assertThrows(NotFoundException.class,
                () -> userServiceImpl.getUserById(userId));
    }

    @Test
    void deleteUserById_deletes() {
        Mockito.when(userRepository.findById(ArgumentMatchers.anyLong())).thenReturn(Optional.of(user));
        userServiceImpl.deleteUser(0L);
        Mockito.verify(userRepository).deleteById(0L);
    }
}
