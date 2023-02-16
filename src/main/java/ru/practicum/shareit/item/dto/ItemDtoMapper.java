package ru.practicum.shareit.item.dto;

import ru.practicum.shareit.item.model.Item;

public class ItemDtoMapper {
    public static ItemDto ItemToItemDto(Item item) { //mapRow ->ItemToItemDto
        return ItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .owner(Math.toIntExact(item.getOwnerId()))
                .build();
    }
}