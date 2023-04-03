package ru.practicum.shareit.user.mapper;


import org.junit.jupiter.api.Test;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.User;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class UserMapperTest {
    @Test
    public void toUserDtoTest() {
        User user = User.builder().id(1L).name("userName").email("userEmail@mail.ru").build();

        UserDto userDto = UserDtoMapper.userToUserDto(user);

        assertEquals(1L, userDto.getId());
        assertEquals("userName", userDto.getName());
        assertEquals("userEmail@mail.ru", userDto.getEmail());
    }
}
