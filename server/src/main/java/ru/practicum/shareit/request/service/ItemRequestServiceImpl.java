package ru.practicum.shareit.request.service;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.ItemDto;
import ru.practicum.shareit.item.mapper.ItemDtoMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.RequestBodyItemRequestDto;
import ru.practicum.shareit.request.mapper.ItemRequestDtoMapper;
import ru.practicum.shareit.request.mapper.RequestBodyItemRequestDtoMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@Qualifier("ItemRequestServiceImpl")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ItemRequestServiceImpl implements ItemRequestService {
    final ItemRequestRepository itemRequestRepository;
    final ItemRepository itemRepository;
    final UserRepository userRepository;

    final ItemRequestValidation itemRequestValidation;

    @Autowired
    public ItemRequestServiceImpl(ItemRequestRepository itemRequestRepository,
                                  ItemRepository itemRepository,
                                  UserRepository userRepository,
                                  ItemRequestValidation itemRequestValidation) {
        this.itemRequestRepository = itemRequestRepository;
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;

        this.itemRequestValidation = itemRequestValidation;
    }

    @Override
    public ItemRequestDto addNewItemRequest(Long userId, RequestBodyItemRequestDto requestBodyItemRequestDto) {
        ItemRequest itemRequest = createItemRequest(requestBodyItemRequestDto, userId);
        itemRequestValidation.itemRequestValidation(requestBodyItemRequestDto);
        return itemRequestToItemRequestDto(itemRequestRepository.save(itemRequest));
    }

    @Override
    public List<ItemRequestDto> getOwnItemRequests(Long userId) {
        User author = getUserById(userId);
        return itemRequestsToItemRequestsDto(itemRequestRepository.findItemRequestsByAuthor(author));
    }

    @Override
    public List<ItemRequestDto> getAllItemRequests(Integer from, Integer size, Long userId) {
        User user = getUserById(userId);
        if (from < 0 || size < 1) {
            String message = "Номер страницы или количество элементов недопустимо";
            log.info(message);
            throw new ValidationException(message);
        }
        return itemRequestsToItemRequestsDto(itemRequestRepository.findAllItemRequests(user, PageRequest.of(from / size, size)));
    }

    @Override
    public ItemRequestDto getRequestById(Long userId, long requestId) {
        getUserById(userId);
        if (itemRequestRepository.findById(requestId).isEmpty()) {
            String message = String.format("%s %d %s", "Заявка с id =", userId, "не найдена");
            log.info(message);
            throw new NotFoundException(message);
        }
        return itemRequestToItemRequestDto(itemRequestRepository.findById(requestId).get());
    }

    private ItemRequest createItemRequest(RequestBodyItemRequestDto requestBodyItemRequestDto, Long userId) {
        LocalDateTime currentDate = LocalDateTime.now();
        ItemRequest itemRequest = RequestBodyItemRequestDtoMapper.mapRow(requestBodyItemRequestDto);
        User author = getUserById(userId);
        itemRequest.setCreated(currentDate);
        itemRequest.setAuthor(author);
        return itemRequest;
    }


    private User getUserById(Long userId) {
        if (userRepository.findById(userId).isEmpty()) {
            String message = String.format("%s %d %s", "Пользователь с id =", userId, "не найден");
            log.info(message);
            throw new NotFoundException(message);
        }
        return userRepository.findById(userId).get();
    }

    private List<ItemDto> itemsToItemsDto(List<Item> items) {
        List<ItemDto> itemsDto = new ArrayList<>();
        for (Item item : items) {
            itemsDto.add(ItemDtoMapper.itemToItemDTO(item));
        }
        return itemsDto;
    }

    public ItemRequestDto itemRequestToItemRequestDto(ItemRequest itemRequest) {
        ItemRequestDto itemRequestDto = ItemRequestDtoMapper.mapRow(itemRequest);
        List<Item> items = new ArrayList<>();
        if (!itemRepository.findItemsByRequests(itemRequest.getId()).isEmpty()) {
            items = itemRepository.findItemsByRequests(itemRequest.getId());
        }
        itemRequestDto.setItems(itemsToItemsDto(items));
        return itemRequestDto;
    }

    public List<ItemRequestDto> itemRequestsToItemRequestsDto(List<ItemRequest> itemRequests) {
        List<ItemRequestDto> itemRequestsDto = new ArrayList<>();
        for (ItemRequest itemRequest : itemRequests) {
            itemRequestsDto.add(itemRequestToItemRequestDto(itemRequest));
        }
        return itemRequestsDto;
    }

}