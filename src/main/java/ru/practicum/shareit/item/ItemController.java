package ru.practicum.shareit.item;


import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.ItemServiceImpl;

import java.util.List;

/**
 * TODO Sprint add-controllers.
 */

@RestController
@Slf4j
@RequestMapping(path = "/items")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ItemController {
    final ItemServiceImpl itemServiceImpl;

    final String pathIdItem = "/{itemId}";
    final String headerOwnerValue = "X-Sharer-User-Id";

    @Autowired
    public ItemController(@Qualifier("ItemServiceImpl") ItemServiceImpl itemServiceImpl) {
        this.itemServiceImpl = itemServiceImpl;
    }

    @PostMapping
    public ItemDto addNewItem(@RequestHeader(value = headerOwnerValue, required = false) Long ownerId, @RequestBody Item item) {
        log.info("Поступил запрос на создание нового товара");
        return itemServiceImpl.addNewItem(item, ownerId);
    }


    @PatchMapping(pathIdItem)
    public ItemDto updateItem(@RequestHeader(value = headerOwnerValue, required = false) Long ownerId, @PathVariable long itemId, @RequestBody Item item) {
        log.info(String.format("%s %d", "Поступил запрос на изменение товара с id =", itemId));
        return itemServiceImpl.updateItem(itemId, item, ownerId);
    }


    @GetMapping
    public List<ItemDto> getAllItems(@RequestHeader(value = headerOwnerValue, required = false) Long ownerId) {
        log.info("Поступил запрос на вывод всех товаров");
        return itemServiceImpl.getAllItems(ownerId);
    }

    @GetMapping(pathIdItem)
    public ItemDto getItemById(@PathVariable long itemId) {
        log.info(String.format("%s %d", "Поступил запрос на вывод товара с id =", itemId));
        return itemServiceImpl.getItemById(itemId);
    }

    @GetMapping("/search")
    public List<ItemDto> getItemByNameOrDescription(@RequestParam String text) {
        log.info("Поступил запрос на вывод товара по имени или описанию");
        return itemServiceImpl.getItemByNameOrDescription(text);
    }
}
