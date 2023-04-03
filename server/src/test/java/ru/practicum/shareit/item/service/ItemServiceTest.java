package ru.practicum.shareit.item.service;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import ru.practicum.shareit.TestHelper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.exception.ForbiddenException;
import ru.practicum.shareit.exception.InternalServerException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.*;
import ru.practicum.shareit.item.mapper.CommentDtoMapper;
import ru.practicum.shareit.item.mapper.ItemDtoMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ItemServiceTest {
    @Mock
    ItemRequestRepository itemRequestRepository;
    @Mock
    UserRepository userRepository;
    @Mock
    ItemRepository itemRepository;
    @Mock
    BookingRepository bookingRepository;
    @Mock
    CommentRepository commentRepository;

    @InjectMocks
    ItemServiceImpl itemServiceimpl;
    final TestHelper testHelper = new TestHelper();

    User owner;
    User author;
    User booker;
    RequestBodyItemDto requestBodyItemDto;
    Item item;
    Booking booking;
    Comment comment;
    ItemDto itemDto;
    final long userId = 1L;
    final long itemId = 1L;

    @BeforeEach
    void beforeEach() {
        owner = testHelper.getOwner();
        author = testHelper.getAuthor();
        item = testHelper.getItem();
        item.setOwner(owner);
        comment = testHelper.getComment();
        comment.setItem(item);
        comment.setAuthor(author);
        itemDto = ItemDtoMapper.itemToItemDTO(item);
        booking = testHelper.getBooking();
        booking.setItem(item);
        booking.setBooker(booker);
        requestBodyItemDto = RequestBodyItemDto.builder().name(item.getName())
                .description(item.getDescription()).available(item.getAvailable()).build();
        Mockito.when(itemRequestRepository.save(ArgumentMatchers.any())).thenAnswer(input -> input.getArguments()[0]);
    }

    @Test
    void addItemTest_whenItemCorrect_thenSave() {
        Mockito.when(userRepository.findById(ArgumentMatchers.anyLong())).thenReturn(Optional.of(owner));
        itemServiceimpl.addNewItem(requestBodyItemDto, owner.getId());
        requestBodyItemDto.setRequestId(null);

        ArgumentCaptor<Item> captor = ArgumentCaptor.forClass(Item.class);
        Mockito.verify(itemRepository).save(captor.capture());

        Item savedItem = captor.getValue();
        assertEquals(requestBodyItemDto.getName(), savedItem.getName());
        assertEquals(requestBodyItemDto.getDescription(), savedItem.getDescription());
        assertEquals(requestBodyItemDto.getAvailable(), savedItem.getAvailable());
        assertEquals(owner, savedItem.getOwner());
    }

    @Test
    void addItemTest_whenDescEmpty_thenThrowException() {
        Mockito.when(itemRepository.findById(ArgumentMatchers.anyLong())).thenReturn(Optional.of(item));
        Mockito.when(userRepository.findById(ArgumentMatchers.anyLong())).thenReturn(Optional.of(author));
        requestBodyItemDto.setDescription("");

        Assertions.assertThrows(
                ValidationException.class,
                () -> itemServiceimpl.addNewItem(requestBodyItemDto, userId));

        Mockito.verify(itemRepository, Mockito.never()).save(ArgumentMatchers.any());
    }

    @Test
    void addItemTest_whenDescOnlySpace_thenThrowException() {
        Mockito.when(itemRepository.findById(ArgumentMatchers.anyLong())).thenReturn(Optional.of(item));
        Mockito.when(userRepository.findById(ArgumentMatchers.anyLong())).thenReturn(Optional.of(author));
        requestBodyItemDto.setDescription(" ");

        Assertions.assertThrows(
                ValidationException.class,
                () -> itemServiceimpl.addNewItem(requestBodyItemDto, userId));

        Mockito.verify(itemRepository, Mockito.never()).save(ArgumentMatchers.any());
    }

    @Test
    void addItemTest_whenDescIsNull_thenThrowException() {
        Mockito.when(itemRepository.findById(ArgumentMatchers.anyLong())).thenReturn(Optional.of(item));
        Mockito.when(userRepository.findById(ArgumentMatchers.anyLong())).thenReturn(Optional.of(author));
        requestBodyItemDto.setDescription(null);

        Assertions.assertThrows(
                ValidationException.class,
                () -> itemServiceimpl.addNewItem(requestBodyItemDto, userId));

        Mockito.verify(itemRepository, Mockito.never()).save(ArgumentMatchers.any());
    }

    @Test
    void addItemTest_whenNameEmpty_thenThrowException() {
        Mockito.when(itemRepository.findById(ArgumentMatchers.anyLong())).thenReturn(Optional.of(item));
        Mockito.when(userRepository.findById(ArgumentMatchers.anyLong())).thenReturn(Optional.of(author));
        requestBodyItemDto.setName("");

        Assertions.assertThrows(
                ValidationException.class,
                () -> itemServiceimpl.addNewItem(requestBodyItemDto, userId));

        Mockito.verify(itemRepository, Mockito.never()).save(ArgumentMatchers.any());
    }

    @Test
    void addItemTest_whenNameOnlySpace_thenThrowException() {
        Mockito.when(itemRepository.findById(ArgumentMatchers.anyLong())).thenReturn(Optional.of(item));
        Mockito.when(userRepository.findById(ArgumentMatchers.anyLong())).thenReturn(Optional.of(author));
        requestBodyItemDto.setName(" ");

        Assertions.assertThrows(
                ValidationException.class,
                () -> itemServiceimpl.addNewItem(requestBodyItemDto, userId));

        Mockito.verify(itemRepository, Mockito.never()).save(ArgumentMatchers.any());
    }

    @Test
    void addItemTest_whenNameIsNull_thenThrowException() {
        Mockito.when(itemRepository.findById(ArgumentMatchers.anyLong())).thenReturn(Optional.of(item));
        Mockito.when(userRepository.findById(ArgumentMatchers.anyLong())).thenReturn(Optional.of(author));
        requestBodyItemDto.setName(null);

        Assertions.assertThrows(
                ValidationException.class,
                () -> itemServiceimpl.addNewItem(requestBodyItemDto, userId));

        Mockito.verify(itemRepository, Mockito.never()).save(ArgumentMatchers.any());
    }

    @Test
    void addItemTest_whenAvailableIsNull_thenThrowException() {
        Mockito.when(itemRepository.findById(ArgumentMatchers.anyLong())).thenReturn(Optional.of(item));
        Mockito.when(userRepository.findById(ArgumentMatchers.anyLong())).thenReturn(Optional.of(author));
        requestBodyItemDto.setAvailable(null);

        Assertions.assertThrows(
                ValidationException.class,
                () -> itemServiceimpl.addNewItem(requestBodyItemDto, userId));

        Mockito.verify(itemRepository, Mockito.never()).save(ArgumentMatchers.any());
    }

    @Test
    void addItemTest_whenUserIdIsNull_thenThrowException() {
        Mockito.when(itemRepository.findById(ArgumentMatchers.anyLong())).thenReturn(Optional.of(item));
        Mockito.when(userRepository.findById(ArgumentMatchers.anyLong())).thenReturn(Optional.of(author));

        Assertions.assertThrows(
                ValidationException.class,
                () -> itemServiceimpl.addNewItem(requestBodyItemDto, null));

        Mockito.verify(itemRepository, Mockito.never()).save(ArgumentMatchers.any());
    }

    @Test
    void addCommentTest_whenBookingNotFound_thenThrowException() {
        Mockito.when(itemRepository.findById(ArgumentMatchers.anyLong())).thenReturn(Optional.of(item));
        Mockito.when(userRepository.findById(ArgumentMatchers.anyLong())).thenReturn(Optional.of(author));

        Assertions.assertThrows(
                ValidationException.class,
                () -> itemServiceimpl.addNewComment(comment, userId, itemId));

        Mockito.verify(commentRepository, Mockito.never()).save(ArgumentMatchers.any());
    }

    @Test
    void addCommentTest_whenTextIsEmpty_thenThrowException() {
        Mockito.when(itemRepository.findById(ArgumentMatchers.anyLong())).thenReturn(Optional.of(item));
        Mockito.when(userRepository.findById(ArgumentMatchers.anyLong())).thenReturn(Optional.of(author));
        comment.setText("");

        Assertions.assertThrows(
                ValidationException.class,
                () -> itemServiceimpl.addNewComment(comment, userId, itemId));

        Mockito.verify(commentRepository, Mockito.never()).save(ArgumentMatchers.any());
    }

    @Test
    void addCommentTest_whenTextIsNull_thenThrowException() {
        Mockito.when(itemRepository.findById(ArgumentMatchers.anyLong())).thenReturn(Optional.of(item));
        Mockito.when(userRepository.findById(ArgumentMatchers.anyLong())).thenReturn(Optional.of(author));
        comment.setText(null);

        Assertions.assertThrows(
                ValidationException.class,
                () -> itemServiceimpl.addNewComment(comment, userId, itemId));

        Mockito.verify(commentRepository, Mockito.never()).save(ArgumentMatchers.any());
    }

    @Test
    void addCommentTest_whenTextIsSpace_thenThrowException() {
        Mockito.when(itemRepository.findById(ArgumentMatchers.anyLong())).thenReturn(Optional.of(item));
        Mockito.when(userRepository.findById(ArgumentMatchers.anyLong())).thenReturn(Optional.of(author));
        comment.setText(" ");

        Assertions.assertThrows(
                ValidationException.class,
                () -> itemServiceimpl.addNewComment(comment, userId, itemId));

        Mockito.verify(commentRepository, Mockito.never()).save(ArgumentMatchers.any());
    }

    @Test
    void addCommentTest_whenItemNotFound_thenThrowException() {
        Mockito.when(userRepository.findById(ArgumentMatchers.anyLong())).thenReturn(Optional.of(author));
        Mockito.when(bookingRepository.findById(ArgumentMatchers.anyLong())).thenReturn(Optional.of(booking));


        Assertions.assertThrows(
                NotFoundException.class,
                () -> itemServiceimpl.addNewComment(comment, userId, itemId));

        Mockito.verify(commentRepository, Mockito.never()).save(ArgumentMatchers.any());
    }

    @Test
    void addCommentTest_whenUserNotFound_thenThrowException() {
        Mockito.when(itemRepository.findById(ArgumentMatchers.anyLong())).thenReturn(Optional.of(item));
        Mockito.when(bookingRepository.findById(ArgumentMatchers.anyLong())).thenReturn(Optional.of(booking));


        Assertions.assertThrows(
                NotFoundException.class,
                () -> itemServiceimpl.addNewComment(comment, userId, itemId));

        Mockito.verify(commentRepository, Mockito.never()).save(ArgumentMatchers.any());
    }

    @Test
    void addCommentTest_whenCommentCorrect_thenSave() {
        Mockito.when(itemRepository.findById(ArgumentMatchers.anyLong())).thenReturn(Optional.of(item));
        Mockito.when(userRepository.findById(ArgumentMatchers.anyLong())).thenReturn(Optional.of(author));
        Mockito.when(bookingRepository.findPastBookingsForUserAndItem(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any())).thenReturn(List.of(booking));
        Mockito.when(commentRepository.save(comment)).thenReturn(comment);
        CommentDto commentDto = itemServiceimpl.addNewComment(comment, userId, itemId);

        Mockito.verify(commentRepository).save(comment);
        assertEquals(CommentDtoMapper.mapRow(comment), commentDto);
    }

    @Test
    void updateItemTest_whenCorrect_thenUpdate() {
        Mockito.when(userRepository.findById(ArgumentMatchers.anyLong())).thenReturn(Optional.of(owner));
        Mockito.when(itemRepository.findById(ArgumentMatchers.anyLong())).thenReturn(Optional.of(item));
        item.setOwner(owner);
        requestBodyItemDto.setDescription(null);
        requestBodyItemDto.setName(null);
        requestBodyItemDto.setAvailable(null);
        requestBodyItemDto.setRequestId(null);

        itemServiceimpl.updateItem(itemId, requestBodyItemDto, owner.getId());

        Mockito.verify(itemRepository).save(ArgumentMatchers.any());
    }

    @Test
    void updateItemTest_whenUserMissing_thenThrowException() {
        long itemId = 1L;
        Mockito.when(itemRepository.findById(ArgumentMatchers.anyLong())).thenReturn(Optional.of(item));
        Assertions.assertThrows(
                InternalServerException.class,
                () -> itemServiceimpl.updateItem(itemId, requestBodyItemDto, null));

        Mockito.verify(itemRepository, Mockito.never()).save(ArgumentMatchers.any());
    }

    @Test
    void updateItemTest_whenItemNotFound_thenThrowException() {
        Assertions.assertThrows(
                NotFoundException.class,
                () -> itemServiceimpl.updateItem(itemId, requestBodyItemDto, userId));

        Mockito.verify(itemRepository, Mockito.never()).save(ArgumentMatchers.any());
    }

    @Test
    void updateItemTest_whenUserIsNotOwner_thenThrowException() {
        Mockito.when(itemRepository.findById(ArgumentMatchers.anyLong())).thenReturn(Optional.of(item));
        Mockito.when(userRepository.findById(ArgumentMatchers.anyLong())).thenReturn(Optional.of(owner));
        item.setOwner(owner);
        Assertions.assertThrows(
                ForbiddenException.class,
                () -> itemServiceimpl.updateItem(itemId, requestBodyItemDto, userId));

        Mockito.verify(itemRepository, Mockito.never()).save(ArgumentMatchers.any());
    }

    @Test
    void getItemByIdTest_whenItemPresent_thenItem() {
        Mockito.when(userRepository.findById(ArgumentMatchers.anyLong())).thenReturn(Optional.of(owner));
        Mockito.when(itemRepository.findById(ArgumentMatchers.anyLong())).thenReturn(Optional.of(item));
        item.setOwner(owner);
        itemDto.setComments(new ArrayList<>());

        ItemDto result = itemServiceimpl.getItemById(itemId, userId);

        Mockito.verify(itemRepository, Mockito.times(2)).findById(ArgumentMatchers.anyLong());
        assertEquals(itemDto, result);
    }

    @Test
    void getItemByIdTest_whenItemNotFound_thenThrowException() {
        Mockito.when(userRepository.findById(ArgumentMatchers.anyLong())).thenReturn(Optional.of(owner));
        Mockito.when(itemRepository.findById(ArgumentMatchers.anyLong())).thenReturn(Optional.empty());

        Mockito.verify(itemRepository, Mockito.never()).findById(ArgumentMatchers.anyLong());
        Assertions.assertThrows(NotFoundException.class, () -> itemServiceimpl.getItemById(1L, 1L));
    }

    @Test
    void getItemsTest() {
        Mockito.when(userRepository.findById(ArgumentMatchers.anyLong())).thenReturn(Optional.of(owner));

        List<ItemDto> result = itemServiceimpl.getAllItems(owner.getId(), 1, 1);

        Mockito.verify(itemRepository).findItemsForUserWithPage(ArgumentMatchers.any(), ArgumentMatchers.any());
        Assertions.assertEquals(new ArrayList<>(), result);
    }

    @Test
    void getItemByIdTest_whenSizeIllegal_thenThrowException() {
        Mockito.when(userRepository.findById(ArgumentMatchers.anyLong())).thenReturn(Optional.of(owner));

        Mockito.verify(itemRepository, Mockito.never()).findItemsForUserWithPage(ArgumentMatchers.any(), ArgumentMatchers.any());
        Assertions.assertThrows(ValidationException.class, () -> itemServiceimpl.getAllItems(2L, -1, 1));
    }

    @Test
    void getItemByIdTest_whenUserNotFound_thenThrowException() {

        Mockito.verify(itemRepository, Mockito.never()).findItemsForUserWithPage(ArgumentMatchers.any(), ArgumentMatchers.any());
        Assertions.assertThrows(NotFoundException.class, () -> itemServiceimpl.getAllItems(1L, 1, 1));
    }

    @Test
    void getItemsByTextTest() {
        Mockito.when(userRepository.findById(ArgumentMatchers.anyLong())).thenReturn(Optional.of(owner));

        List<ItemDto> result = itemServiceimpl.getItemByNameOrDescription("itemName1", owner.getId(), 1, 1);

        Mockito.verify(itemRepository).findAvailableItemsByNameOrDescription(ArgumentMatchers.anyString(), ArgumentMatchers.any(), ArgumentMatchers.any());
        Assertions.assertEquals(new ArrayList<>(), result);
    }

}