package ru.practicum.shareit.user.dto;


import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.user.dto.UserDto;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserDtoTest {
    @Autowired
    JacksonTester<UserDto> jacksonTester;

    @Test
    void userDtoJsonTest() throws IOException {
        long userId = 1L;
        UserDto userDto = UserDto.builder().id(userId).name("userName1").email("userEmail1@mail.ru").build();

        JsonContent<UserDto> result = jacksonTester.write(userDto);

        Assertions.assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo((int) userDto.getId());
        Assertions.assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo(userDto.getName());
        Assertions.assertThat(result).extractingJsonPathStringValue("$.email").isEqualTo(userDto.getEmail());
    }
}
