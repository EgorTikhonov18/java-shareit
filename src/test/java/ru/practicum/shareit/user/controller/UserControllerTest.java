package ru.practicum.shareit.user.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AccessLevel;
import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.exception.IsAlreadyExistsException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.UserController;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.UserService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(controllers = UserController.class)
@AutoConfigureMockMvc
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserControllerTest {

    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    MockMvc mockMvc;
    @MockBean
    @Qualifier("UserServiceImpl")
    UserService userService;

    UserDto userDtoCorrect;
    UserDto userDtoDuplicateEmail;
    UserDto userDtoEmptyEmail;
    UserDto userDtoInvalidEmail;
    UserDto userDtoEmptyName;
    final String pathUsers = "/users";
    final String pathUserId = "/{userId}";

    @BeforeEach
    void beforeEach() {
        userDtoCorrect = UserDto.builder().id(1L).name("userName1").email("userEmail1@mail.ru").build();
        userDtoEmptyEmail = UserDto.builder().id(2L).name("userName2").email("").build();
        userDtoInvalidEmail = UserDto.builder().id(3L).name("userName3").email("userEmail3mail.ru").build();
        userDtoEmptyName = UserDto.builder().id(4L).name("").email("userEmail4@mail.ru").build();
        userDtoDuplicateEmail = UserDto.builder().id(5L).name("").email("userEmail1@mail.ru").build();
    }

    @SneakyThrows
    @Test
    void addUserTest_whenUserCorrect_thenReturnOK() {
        when(userService.addUser(any())).thenReturn(userDtoCorrect);

        String result = mockMvc.perform(post(pathUsers)
                        .content(objectMapper.writeValueAsString(userDtoCorrect))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is2xxSuccessful())
                .andReturn()
                .getResponse()
                .getContentAsString();
        verify(userService).addUser(any());
        assertEquals(objectMapper.writeValueAsString(userDtoCorrect), result);
    }

    @SneakyThrows
    @Test
    void addUserTest_whenDuplicateEmailUser_thenThrow() {
        when(userService.addUser(any())).thenThrow(new IsAlreadyExistsException("A user with the same email already exists"));

        String result = mockMvc.perform(post(pathUsers)
                        .content(objectMapper.writeValueAsString(userDtoDuplicateEmail))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError())
                .andReturn()
                .getResponse()
                .getContentAsString();
        verify(userService).addUser(any());
        assertEquals("{\"error\":\"A user with the same email already exists\"}", result);
    }

    @SneakyThrows
    @Test
    void addUserTest_whenEmptyEmail_thenBadRequest() {
        when(userService.addUser(any())).thenThrow(new ValidationException("The field's value is not valid"));

        String result = mockMvc.perform(post(pathUsers)
                        .content(objectMapper.writeValueAsString(userDtoEmptyEmail))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError())
                .andReturn()
                .getResponse()
                .getContentAsString();
        verify(userService).addUser(any());
        assertEquals("{\"error\":\"The field's value is not valid\"}", result);
    }

    @SneakyThrows
    @Test
    void addUserTest_whenInvalidEmail_thenBadRequest() {
        when(userService.addUser(any())).thenThrow(new ValidationException("The field's value is not valid"));

        String result = mockMvc.perform(post(pathUsers)
                        .content(objectMapper.writeValueAsString(userDtoInvalidEmail))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError())
                .andReturn()
                .getResponse()
                .getContentAsString();
        verify(userService).addUser(any());
        assertEquals("{\"error\":\"The field's value is not valid\"}", result);
    }

    @SneakyThrows
    @Test
    void addUserTest_whenEmptyName_thenBadRequest() {
        when(userService.addUser(any())).thenThrow(new ValidationException("The field's value is not valid"));

        String result = mockMvc.perform(post(pathUsers)
                        .content(objectMapper.writeValueAsString(userDtoEmptyName))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError())
                .andReturn()
                .getResponse()
                .getContentAsString();
        verify(userService).addUser(any());
        assertEquals("{\"error\":\"The field's value is not valid\"}", result);
    }

    @SneakyThrows
    @Test
    void updateUserTest_whenUserCorrect_thenReturnOK() {
        long userId = 1L;
        when(userService.updateUser(any(), anyLong())).thenReturn(userDtoCorrect);

        String result = mockMvc.perform(patch(pathUsers + pathUserId, userId)
                        .content(objectMapper.writeValueAsString(userDtoCorrect))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is2xxSuccessful())
                .andReturn()
                .getResponse()
                .getContentAsString();
        verify(userService).updateUser(any(), anyLong());
        assertEquals(objectMapper.writeValueAsString(userDtoCorrect), result);
    }

    @SneakyThrows
    @Test
    void getUserByIdTest_whenIsPresent_thenReturnOK() {
        long userId = 1L;

        mockMvc.perform(get(pathUsers + pathUserId, userId))
                .andExpect(status().isOk());
        verify(userService).getUserById(userId);
    }

    @SneakyThrows
    @Test
    void getUserByIdTest_whenIsNotPresent_thenThrow() {
        long userId = 1L;
        when(userService.getUserById(userId)).thenThrow(new NotFoundException(String.format("%s %d %s", "The user with id =", userId, "not found")));
        mockMvc.perform(get(pathUsers + pathUserId, userId))
                .andExpect(status().is4xxClientError());
        verify(userService).getUserById(userId);
    }

    @SneakyThrows
    @Test
    void getAllUsersTest() {
        mockMvc.perform(get(pathUsers)).andExpect(status().isOk());
        verify(userService).getAllUsers();
    }

    @SneakyThrows
    @Test
    void deleteUserTest() {
        long userId = 1L;
        doNothing().when(userService).deleteUser(anyLong());

        mockMvc.perform(delete(pathUsers + pathUserId, userId)).andExpect(status().is2xxSuccessful());

        verify(userService).deleteUser(userId);
    }
}
