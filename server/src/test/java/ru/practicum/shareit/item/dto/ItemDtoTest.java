package ru.practicum.shareit.item.dto;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.TestHelper;
import ru.practicum.shareit.item.ItemDto;
import ru.practicum.shareit.item.mapper.ItemDtoMapper;

import java.io.IOException;


@JsonTest
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ItemDtoTest {
    @Autowired
    JacksonTester<ItemDto> jacksonTester;
    final TestHelper testHelper = new TestHelper();

    @Test
    void userDtoJsonTest() throws IOException {
        ItemDto itemDto = ItemDtoMapper.itemToItemDTO(testHelper.getItem());
        itemDto.setRequestId(1L);

        JsonContent<ItemDto> result = jacksonTester.write(itemDto);

        Assertions.assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo((int) itemDto.getId());
        Assertions.assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo(itemDto.getName());
        Assertions.assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo(itemDto.getDescription());
        Assertions.assertThat(result).extractingJsonPathBooleanValue("$.available").isEqualTo(itemDto.getAvailable());
        Assertions.assertThat(result).extractingJsonPathNumberValue("$.requestId").isEqualTo((int) itemDto.getRequestId());
    }
}
