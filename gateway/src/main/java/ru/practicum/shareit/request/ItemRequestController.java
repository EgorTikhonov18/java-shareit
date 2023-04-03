package ru.practicum.shareit.request;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

import static ru.practicum.shareit.Constants.ID;

@Controller
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemRequestController {
    private final ItemRequestClient itemRequestClient;

    @PostMapping
    public ResponseEntity<Object> addItemRequest(@RequestHeader(ID) long userId,
                                                 @RequestBody @Valid ItemRequestDto itemRequestDto) {
        return itemRequestClient.addItemRequest(userId, itemRequestDto);
    }

    @GetMapping
    public ResponseEntity<Object> getAllOwnItemRequests(@RequestHeader(ID) long userId) {
        return itemRequestClient.getAllOwnItemRequests(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAllItemRequestsOfUsers(@RequestHeader(ID) long userId,
                                                            @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                                            @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
        return itemRequestClient.getAllItemRequestsOfUsers(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getItemRequestById(@RequestHeader(ID) long userId,
                                                     @PathVariable Long requestId) {
        return itemRequestClient.getItemRequestById(userId, requestId);
    }
}
