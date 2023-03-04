package ru.practicum.shareit.item;


import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

@RestController
@Slf4j
@RequestMapping(path = "/items")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ItemController {
    final ItemService itemService;

    final String pathIdItem = "/{itemId}";
    final String headerOwnerValue = "X-Sharer-User-Id";


    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @PostMapping
    public ItemDto addNewItem(@RequestHeader(value = headerOwnerValue, required = false) Long ownerId, @RequestBody Item item) {
        log.info("Поступил запрос на создание нового товара");
        return itemService.addNewItem(item, ownerId);
    }


    @PatchMapping(pathIdItem)
    public ItemDto updateItem(@RequestHeader(value = headerOwnerValue, required = false) Long ownerId, @PathVariable long itemId, @RequestBody Item item) {
        log.info(String.format("%s %d", "Поступил запрос на изменение товара с id =", itemId));
        return itemService.updateItem(itemId, item, ownerId);
    }


    @GetMapping
    public List<ItemDto> getAllItems(@RequestHeader(value = headerOwnerValue, required = false) Long ownerId) {
        log.info("Поступил запрос на вывод всех товаров");
        return itemService.getAllItems(ownerId);
    }

    @GetMapping(pathIdItem)
    public ItemDto getItemById(@PathVariable long itemId) {
        log.info(String.format("%s %d", "Поступил запрос на вывод товара с id =", itemId));
        return itemService.getItemById(itemId);
    }

    @GetMapping("/search")
    public List<ItemDto> getItemByNameOrDescription(@RequestParam String text) {
        log.info("Поступил запрос на вывод товара по имени или описанию");
        return itemService.getItemByNameOrDescription(text);
    }
}
