package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.RequestBodyItemRequestDto;

import java.util.List;

public interface ItemRequestService {
    ItemRequestDto addNewItemRequest(Long userId, RequestBodyItemRequestDto requestBodyRequestDto);

    List<ItemRequestDto> getOwnItemRequests(Long userId);

    List<ItemRequestDto> getAllItemRequests(Integer from, Integer size, Long userId);

    ItemRequestDto getRequestById(Long userId, long requestId);
}
