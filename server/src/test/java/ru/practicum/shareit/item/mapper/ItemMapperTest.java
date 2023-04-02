package ru.practicum.shareit.item.mapper;


import org.junit.jupiter.api.Test;
import ru.practicum.shareit.TestHelper;
import ru.practicum.shareit.item.ItemDto;
import ru.practicum.shareit.item.model.Item;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ItemMapperTest {
    final TestHelper testHelper = new TestHelper();

    @Test
    public void toItemDtoTest() {
        Item item = testHelper.getItem();

        ItemDto itemDto = ItemDtoMapper.itemToItemDTO(item);

        assertEquals(1L, itemDto.getId());
        assertEquals("itemName", itemDto.getName());
        assertEquals("itemDesc", itemDto.getDescription());
        assertTrue(itemDto.getAvailable());
    }
}
