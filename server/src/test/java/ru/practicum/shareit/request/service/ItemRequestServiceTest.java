package ru.practicum.shareit.request.service;


import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import ru.practicum.shareit.TestHelper;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.mapper.ItemDtoMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.mapper.ItemRequestDtoMapper;
import ru.practicum.shareit.request.dto.RequestBodyItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ItemRequestServiceTest {
    @Mock
    ItemRequestRepository itemRequestRepository;
    @Mock
    UserRepository userRepository;
    @Mock
    ItemRepository itemRepository;
    @InjectMocks
    ItemRequestServiceImpl itemRequestServiceimpl;
    User author;
    User owner;
    Item item;
    ItemRequest itemRequest;
    RequestBodyItemRequestDto requestBodyItemRequestDto;
    ItemRequestDto itemRequestDto;
    final TestHelper testHelper = new TestHelper();

    @BeforeEach
    void beforeEach() {
        author = testHelper.getAuthor();
        owner = testHelper.getOwner();
        itemRequest = testHelper.getItemRequest();
        requestBodyItemRequestDto = RequestBodyItemRequestDto.builder().description(itemRequest.getDescription()).build();
        itemRequestDto = ItemRequestDtoMapper.mapRow(itemRequest);
        item = testHelper.getItem();
        Mockito.when(itemRequestRepository.save(ArgumentMatchers.any())).thenAnswer(input -> input.getArguments()[0]);
        item.setItemRequest(itemRequest);
    }

    @Test
    void addItemRequestTest_whenItemRequestCorrect_thenSave() {
        itemRequestDto.setItems(new ArrayList<>());
        Mockito.when(userRepository.findById(ArgumentMatchers.anyLong())).thenReturn(Optional.of(author));

        ItemRequestDto result = itemRequestServiceimpl.addNewItemRequest(2L, requestBodyItemRequestDto);

        Mockito.verify(itemRequestRepository).save(ArgumentMatchers.any());
        Assertions.assertNotNull(result);
        assertEquals(itemRequestDto.getDescription(), result.getDescription());
        assertEquals(itemRequestDto.getItems(), result.getItems());
    }

    @Test
    void addItemRequestTest_whenDescIsMissing_thenThrowException() {
        itemRequestDto.setItems(new ArrayList<>());
        Mockito.when(userRepository.findById(ArgumentMatchers.anyLong())).thenReturn(Optional.of(author));
        requestBodyItemRequestDto.setDescription("");

        Mockito.verify(itemRequestRepository, Mockito.never()).save(ArgumentMatchers.any());
        Assertions.assertThrows(ValidationException.class,
                () -> itemRequestServiceimpl.addNewItemRequest(1L, requestBodyItemRequestDto));
    }

    @Test
    void getItemRequestByIdTest_whenItemRequestPresent_thenItemRequest() {
        Mockito.when(userRepository.findById(ArgumentMatchers.anyLong())).thenReturn(Optional.of(author));
        Mockito.when(itemRequestRepository.findById(ArgumentMatchers.any())).thenReturn(Optional.of(itemRequest));
        Mockito.when(itemRepository.findItemsByRequests(ArgumentMatchers.anyLong())).thenReturn(List.of(item));

        ItemRequestDto result = itemRequestServiceimpl.getRequestById(2L, 1L);

        Mockito.verify(itemRepository, Mockito.times(2)).findItemsByRequests(ArgumentMatchers.anyLong());
        itemRequestDto.setItems(List.of(ItemDtoMapper.itemToItemDTO(item)));
        assertEquals(itemRequestDto, result);
    }

    @Test
    void getItemRequestByIdTest_whenItemRequestNotFound_thenThrowException() {
        Mockito.when(userRepository.findById(ArgumentMatchers.anyLong())).thenReturn(Optional.of(author));
        Mockito.when(itemRequestRepository.findById(ArgumentMatchers.anyLong())).thenReturn(Optional.empty());

        Mockito.verify(itemRepository, Mockito.never()).findItemsByRequests(ArgumentMatchers.anyLong());
        Assertions.assertThrows(NotFoundException.class, () -> itemRequestServiceimpl.getRequestById(2L, 2L));
    }

    @Test
    void getItemRequestsTest() {
        Mockito.when(userRepository.findById(ArgumentMatchers.anyLong())).thenReturn(Optional.of(owner));

        List<ItemRequestDto> result = itemRequestServiceimpl.getAllItemRequests(1, 1, owner.getId());

        Mockito.verify(itemRequestRepository).findAllItemRequests(ArgumentMatchers.any(), ArgumentMatchers.any());
        Assertions.assertEquals(new ArrayList<>(), result);
    }

    @Test
    void getOwnItemRequestsTest() {
        Mockito.when(userRepository.findById(ArgumentMatchers.anyLong())).thenReturn(Optional.of(owner));

        List<ItemRequestDto> result = itemRequestServiceimpl.getOwnItemRequests(owner.getId());

        Mockito.verify(itemRequestRepository).findItemRequestsByAuthor(ArgumentMatchers.any());
        Assertions.assertEquals(new ArrayList<>(), result);
    }

}
