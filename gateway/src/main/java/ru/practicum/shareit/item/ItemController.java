package ru.practicum.shareit.item;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

import static ru.practicum.shareit.Constants.ID;

@Controller
@RequestMapping(path = "/items")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemController {
    private final ItemClient itemClient;

    @PostMapping
    public ResponseEntity<Object> addItem(@RequestHeader(ID) long userId,
                                          @RequestBody @Valid ItemDto itemDto) {
        return itemClient.addItem(userId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> updateItem(@RequestHeader(ID) long userId,
                                             @PathVariable long itemId,
                                             @RequestBody ItemDto itemDto) {
        return itemClient.updateItem(itemId, userId, itemDto);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getItemById(@RequestHeader(ID) long userId,
                                              @PathVariable long itemId) {
        return itemClient.getItem(userId, itemId);
    }

    @GetMapping
    public ResponseEntity<Object> getAllItemsOfUserWithId(@RequestHeader(ID) long userId,
                                                          @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                                          @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
        return itemClient.getAllItemsOfUserWithId(userId, from, size);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchItem(@RequestParam String text,
                                             @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                             @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
        return itemClient.searchItem(text, from, size);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> addComment(@RequestHeader(ID) long userId,
                                             @PathVariable Long itemId,
                                             @RequestBody @Valid CommentDto commentDto) {
        return itemClient.addComment(itemId, userId, commentDto);
    }
}
