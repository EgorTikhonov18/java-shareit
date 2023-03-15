package ru.practicum.shareit.item.mapper;

import ru.practicum.shareit.item.ItemDto;
import ru.practicum.shareit.item.model.Item;


public class ItemDtoMapper {
    public static ItemDto itemToItemDTO(Item item) {
        ItemDto itemDto = ItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .build();
        if (item.getItemRequest() != null) {
            itemDto.setRequestId(item.getItemRequest().getId());
        }
        return itemDto;
    }
}