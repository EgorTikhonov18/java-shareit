package ru.practicum.shareit.request.mapper;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.TestHelper;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class ItemRequestMapperTest {
    TestHelper testHelper = new TestHelper();

    @Test
    public void toItemRequestDtoTest() {
        ItemRequest itemRequest = testHelper.getItemRequest();

        ItemRequestDto itemRequestDto = ItemRequestDtoMapper.mapRow(itemRequest);

        assertEquals(1L, itemRequestDto.getId());
        assertEquals("itemRequestDesc", itemRequestDto.getDescription());
        Assertions.assertNull(itemRequestDto.getItems());
    }
}