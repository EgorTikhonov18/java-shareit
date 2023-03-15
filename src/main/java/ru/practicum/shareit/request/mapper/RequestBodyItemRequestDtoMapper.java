package ru.practicum.shareit.request.mapper;

import ru.practicum.shareit.request.dto.RequestBodyItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;

public class RequestBodyItemRequestDtoMapper {
    public static ItemRequest mapRow(RequestBodyItemRequestDto requestBodyRequestDto) {
        return ItemRequest.builder()
                .description(requestBodyRequestDto.getDescription())
                .build();
    }
}
