package ru.practicum.shareit.request.controller;


import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.RequestBodyItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping(path = "/requests")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ItemRequestController {
    final ItemRequestService itemRequestService;
    final String headerUserValue = "X-Sharer-User-Id";

    public ItemRequestController(ItemRequestService itemRequestService) {
        this.itemRequestService = itemRequestService;
    }

    @PostMapping
    public ItemRequestDto addNewItemRequest(@RequestHeader(value = headerUserValue, required = false) Long userId,
                                            @RequestBody RequestBodyItemRequestDto requestBodyRequestDto) {
        log.info("Запрос на добавление нового запроса вещи"); // не знаю как лучше назвать
        return itemRequestService.addNewItemRequest(userId, requestBodyRequestDto);
    }

    @GetMapping
    public List<ItemRequestDto> getOwnItemRequests(@RequestHeader(value = headerUserValue, required = false) Long userId) {
        log.info("Запрос на получение своих запросов");
        return itemRequestService.getOwnItemRequests(userId);
    }

    @GetMapping("/all")
    public List<ItemRequestDto> getAllItemRequests(@RequestHeader(value = headerUserValue, required = false) Long userId,
                                                   @RequestParam(defaultValue = "0") Integer from,
                                                   @RequestParam(defaultValue = "10") Integer size) {
        log.info("Запрос на получение всех запросов, созданных другими пользователями");
        return itemRequestService.getAllItemRequests(from, size, userId);
    }

    @GetMapping("/{requestId}")
    public ItemRequestDto getRequestById(@RequestHeader(value = headerUserValue, required = false) Long userId,
                                         @PathVariable long requestId) {
        log.info(String.format("%s %d", "Запрос на получение данных о запросе с id = ", requestId));
        return itemRequestService.getRequestById(userId, requestId);
    }
}
