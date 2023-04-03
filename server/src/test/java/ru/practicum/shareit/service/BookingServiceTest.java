package ru.practicum.shareit.service;


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
import ru.practicum.shareit.booking.BookingState;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.mapper.BookingDtoMapper;
import ru.practicum.shareit.booking.dto.RequestBodyBookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.service.BookingServiceImpl;
import ru.practicum.shareit.exception.IsAlreadyDoneException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BookingServiceTest {

    @Mock
    ItemRequestRepository itemRequestRepository;
    @Mock
    UserRepository userRepository;
    @Mock
    ItemRepository itemRepository;
    @Mock
    BookingRepository bookingRepository;
    @InjectMocks
    BookingServiceImpl bookingServiceImpl;
    User booker;
    User owner;
    Item item;
    Booking booking;
    RequestBodyBookingDto requestBodyBookingDto;
    BookingDto bookingDto;
    final TestHelper testHelper = new TestHelper();
    final long userId = 1L;
    final long bookingId = 1L;
    final int from = 1;
    final int size = 1;

    @BeforeEach
    void beforeEach() {
        booker = testHelper.getBooker();
        owner = testHelper.getOwner();
        item = testHelper.getItem();
        item.setOwner(owner);
        requestBodyBookingDto = RequestBodyBookingDto.builder().itemId(0L).start(LocalDateTime.now())
                .end(LocalDateTime.now().plus(1, ChronoUnit.DAYS)).build();
        booking = testHelper.getBooking();
        booking.setBooker(booker);
        bookingDto = BookingDtoMapper.mapRow(booking);

        Mockito.when(itemRequestRepository.save(ArgumentMatchers.any())).thenAnswer(input -> input.getArguments()[0]);
    }

    @Test
    void addBookingTest_whenCorrect_thenSave() {

        Mockito.when(itemRepository.findById(ArgumentMatchers.anyLong())).thenReturn(Optional.of(item));
        Mockito.when(userRepository.findById(ArgumentMatchers.anyLong())).thenReturn(Optional.of(booker));

        bookingServiceImpl.addNewBooking(requestBodyBookingDto, userId);

        Mockito.verify(bookingRepository).save(ArgumentMatchers.any());
    }

    @Test
    void addBookingTest_whenItemIsNotAvailable_thenThrowException() {
        Mockito.when(itemRepository.findById(ArgumentMatchers.anyLong())).thenReturn(Optional.of(item));
        Mockito.when(userRepository.findById(ArgumentMatchers.anyLong())).thenReturn(Optional.of(booker));
        item.setAvailable(false);

        Assertions.assertThrows(ValidationException.class,
                () -> bookingServiceImpl.addNewBooking(requestBodyBookingDto, userId));
    }

    @Test
    void addBookingTest_whenStartIsNull_thenThrowException() {
        Mockito.when(itemRepository.findById(ArgumentMatchers.anyLong())).thenReturn(Optional.of(item));
        Mockito.when(userRepository.findById(ArgumentMatchers.anyLong())).thenReturn(Optional.of(booker));
        requestBodyBookingDto.setStart(null);

        Assertions.assertThrows(ValidationException.class,
                () -> bookingServiceImpl.addNewBooking(requestBodyBookingDto, userId));
    }

    @Test
    void addBookingTest_whenEndIsNull_thenThrowException() {
        Mockito.when(itemRepository.findById(ArgumentMatchers.anyLong())).thenReturn(Optional.of(item));
        Mockito.when(userRepository.findById(ArgumentMatchers.anyLong())).thenReturn(Optional.of(booker));
        requestBodyBookingDto.setEnd(null);

        Assertions.assertThrows(ValidationException.class,
                () -> bookingServiceImpl.addNewBooking(requestBodyBookingDto, userId));
    }

    @Test
    void addBookingTest_whenStartMoreEnd_thenThrowException() {
        Mockito.when(itemRepository.findById(ArgumentMatchers.anyLong())).thenReturn(Optional.of(item));
        Mockito.when(userRepository.findById(ArgumentMatchers.anyLong())).thenReturn(Optional.of(booker));
        requestBodyBookingDto.setStart(LocalDateTime.now().plus(100, ChronoUnit.DAYS));

        Assertions.assertThrows(ValidationException.class,
                () -> bookingServiceImpl.addNewBooking(requestBodyBookingDto, userId));
    }

    @Test
    void addBookingTest_whenStartEqualsEnd_thenThrowException() {
        Mockito.when(itemRepository.findById(ArgumentMatchers.anyLong())).thenReturn(Optional.of(item));
        Mockito.when(userRepository.findById(ArgumentMatchers.anyLong())).thenReturn(Optional.of(booker));
        requestBodyBookingDto.setStart(requestBodyBookingDto.getEnd());

        Assertions.assertThrows(ValidationException.class,
                () -> bookingServiceImpl.addNewBooking(requestBodyBookingDto, userId));
    }

    @Test
    void addBookingTest_whenStartLessNow_thenThrowException() {
        Mockito.when(itemRepository.findById(ArgumentMatchers.anyLong())).thenReturn(Optional.of(item));
        Mockito.when(userRepository.findById(ArgumentMatchers.anyLong())).thenReturn(Optional.of(booker));
        requestBodyBookingDto.setStart(LocalDateTime.now().minus(100, ChronoUnit.DAYS));

        Assertions.assertThrows(ValidationException.class,
                () -> bookingServiceImpl.addNewBooking(requestBodyBookingDto, userId));
    }

    @Test
    void addBookingTest_whenBookerIsOwner_thenThrowException() {
        Mockito.when(itemRepository.findById(ArgumentMatchers.anyLong())).thenReturn(Optional.of(item));
        Mockito.when(userRepository.findById(ArgumentMatchers.anyLong())).thenReturn(Optional.of(booker));


        Assertions.assertThrows(NotFoundException.class,
                () -> bookingServiceImpl.addNewBooking(requestBodyBookingDto, 2L));
    }

    @Test
    void addBookingTest_whenItemNotFound_thenThrowException() {
        Mockito.when(itemRepository.findById(ArgumentMatchers.anyLong())).thenReturn(Optional.empty());
        Mockito.when(userRepository.findById(ArgumentMatchers.anyLong())).thenReturn(Optional.of(booker));

        Assertions.assertThrows(NotFoundException.class,
                () -> bookingServiceImpl.addNewBooking(requestBodyBookingDto, userId));
    }

    @Test
    void addBookingTest_whenBookerNotFound_thenThrowException() {
        Mockito.when(itemRepository.findById(ArgumentMatchers.anyLong())).thenReturn(Optional.of(item));
        Mockito.when(userRepository.findById(ArgumentMatchers.anyLong())).thenReturn(Optional.empty());

        Assertions.assertThrows(NotFoundException.class,
                () -> bookingServiceImpl.addNewBooking(requestBodyBookingDto, userId));
    }

    @Test
    void approvedBookingTest_whenIsAlreadyApproved_thenThrowException() {
        Mockito.when(userRepository.findById(ArgumentMatchers.anyLong())).thenReturn(Optional.of(owner));
        Mockito.when(bookingRepository.findById(ArgumentMatchers.anyLong())).thenReturn(Optional.of(booking));
        booking.setItem(item);

        Assertions.assertThrows(IsAlreadyDoneException.class,
                () -> bookingServiceImpl.approveOrRejectBooking(owner.getId(), bookingId, true));
    }

    @Test
    void approvedBookingTest_whenWaiting_thenApprove() {
        Mockito.when(userRepository.findById(ArgumentMatchers.anyLong())).thenReturn(Optional.of(owner));
        Mockito.when(bookingRepository.findById(ArgumentMatchers.anyLong())).thenReturn(Optional.of(booking));
        Mockito.when(itemRepository.findById(ArgumentMatchers.anyLong())).thenReturn(Optional.of(item));
        booking.setItem(item);
        booking.setStatus(BookingStatus.WAITING);

        bookingServiceImpl.approveOrRejectBooking(owner.getId(), bookingId, true);

        Mockito.verify(bookingRepository).save(ArgumentMatchers.any());
    }

    @Test
    void rejectedBookingTest_whenWaiting_thenReject() {
        Mockito.when(userRepository.findById(ArgumentMatchers.anyLong())).thenReturn(Optional.of(owner));
        Mockito.when(bookingRepository.findById(ArgumentMatchers.anyLong())).thenReturn(Optional.of(booking));
        booking.setStatus(BookingStatus.WAITING);

        Mockito.when(itemRepository.findById(ArgumentMatchers.anyLong())).thenReturn(Optional.of(item));
        booking.setItem(item);
        booking.setStatus(BookingStatus.WAITING);

        bookingServiceImpl.approveOrRejectBooking(owner.getId(), bookingId, false);
        Mockito.verify(bookingRepository).save(ArgumentMatchers.any());
    }

    @Test
    void approvedBookingTest_whenIsAlreadyRejected_thenThrowException() {
        Mockito.when(userRepository.findById(ArgumentMatchers.anyLong())).thenReturn(Optional.of(owner));
        Mockito.when(bookingRepository.findById(ArgumentMatchers.anyLong())).thenReturn(Optional.of(booking));
        booking.setStatus(BookingStatus.REJECTED);
        booking.setItem(item);

        Assertions.assertThrows(IsAlreadyDoneException.class,
                () -> bookingServiceImpl.approveOrRejectBooking(owner.getId(), bookingId, false));
    }

    @Test
    void approvedBookingTest_whenBooingIsNotFound_thenThrowException() {
        Mockito.when(userRepository.findById(ArgumentMatchers.anyLong())).thenReturn(Optional.of(owner));

        Assertions.assertThrows(NotFoundException.class,
                () -> bookingServiceImpl.approveOrRejectBooking(userId, bookingId, false));
    }

    @Test
    void approvedBookingTest_whenNotOwner_thenThrowException() {
        Mockito.when(userRepository.findById(ArgumentMatchers.anyLong())).thenReturn(Optional.of(owner));
        Mockito.when(bookingRepository.findById(ArgumentMatchers.anyLong())).thenReturn(Optional.of(booking));
        booking.setStatus(BookingStatus.REJECTED);
        booking.setItem(item);

        Assertions.assertThrows(NotFoundException.class,
                () -> bookingServiceImpl.approveOrRejectBooking(4L, bookingId, false));
    }

    @Test
    void getBookingByIdTest_whenBookingPresent_thenBooking() {
        Mockito.when(userRepository.findById(ArgumentMatchers.anyLong())).thenReturn(Optional.of(booker));
        Mockito.when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));

        BookingDto result = bookingServiceImpl.getBookingById(booker.getId(), bookingId);

        Mockito.verify(bookingRepository, Mockito.times(2)).findById(ArgumentMatchers.anyLong());
        assertEquals(bookingDto, result);
    }

    @Test
    void getBookingByIdTest_whenBookingNotFound_thenThrowException() {
        Mockito.when(userRepository.findById(ArgumentMatchers.anyLong())).thenReturn(Optional.of(booker));
        Mockito.when(bookingRepository.findById(ArgumentMatchers.anyLong())).thenReturn(Optional.empty());

        Mockito.verify(bookingRepository, Mockito.never()).findById(ArgumentMatchers.anyLong());
        Assertions.assertThrows(NotFoundException.class, () -> bookingServiceImpl.getBookingById(userId, bookingId));
    }

    @Test
    void getBookingByIdTest_whenUserNotBookerOrNotOwner_thenThrowException() {
        Mockito.when(userRepository.findById(ArgumentMatchers.anyLong())).thenReturn(Optional.of(booker));
        Mockito.when(bookingRepository.findById(ArgumentMatchers.anyLong())).thenReturn(Optional.of(booking));
        Mockito.when(itemRepository.findById(ArgumentMatchers.anyLong())).thenReturn(Optional.of(item));
        booking.setItem(item);

        Mockito.verify(bookingRepository, Mockito.never()).findById(ArgumentMatchers.anyLong());
        Assertions.assertThrows(NotFoundException.class, () -> bookingServiceImpl.getBookingById(10L, bookingId));
    }

    @Test
    void getBookingCurrentUserTest_whenStateIllegal_thenThrowException() {
        Mockito.when(userRepository.findById(ArgumentMatchers.anyLong())).thenReturn(Optional.of(owner));

        Assertions.assertThrows(java.lang.IllegalArgumentException.class,
                () -> bookingServiceImpl.getBookingCurrentUser(userId, "State", from, size));
    }

    @Test
    void getBookingCurrentUserTest_whenSizeIllegal_thenThrowException() {
        Mockito.when(userRepository.findById(ArgumentMatchers.anyLong())).thenReturn(Optional.of(owner));
        Assertions.assertThrows(ValidationException.class,
                () -> bookingServiceImpl.getBookingCurrentUser(userId, "ALL", from, -1));
    }

    @Test
    void getBookingCurrentUserTest_whenStateAll_thenBookings() {
        Mockito.when(userRepository.findById(ArgumentMatchers.anyLong())).thenReturn(Optional.of(owner));

        List<BookingDto> result = bookingServiceImpl.getBookingCurrentUser(owner.getId(), BookingState.ALL.toString(), from, size);

        Mockito.verify(bookingRepository).findAllBookingsForUser(ArgumentMatchers.any(), ArgumentMatchers.any());
        Assertions.assertEquals(new ArrayList<>(), result);
    }

    @Test
    void getBookingCurrentUserTest_whenStateCurrent_thenBookings() {
        Mockito.when(userRepository.findById(ArgumentMatchers.anyLong())).thenReturn(Optional.of(owner));

        List<BookingDto> result = bookingServiceImpl.getBookingCurrentUser(owner.getId(), BookingState.CURRENT.toString(), from, size);

        Mockito.verify(bookingRepository).findCurrentBookingsForUser(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any());
        Assertions.assertEquals(new ArrayList<>(), result);
    }

    @Test
    void getBookingCurrentUserTest_whenStatePast_thenBookings() {
        Mockito.when(userRepository.findById(ArgumentMatchers.anyLong())).thenReturn(Optional.of(owner));

        List<BookingDto> result = bookingServiceImpl.getBookingCurrentUser(owner.getId(), BookingState.PAST.toString(), from, size);

        Mockito.verify(bookingRepository).findPastBookingsForUser(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any());
        Assertions.assertEquals(new ArrayList<>(), result);
    }

    @Test
    void getBookingCurrentUserTest_whenStateFuture_thenBookings() {
        Mockito.when(userRepository.findById(ArgumentMatchers.anyLong())).thenReturn(Optional.of(owner));

        List<BookingDto> result = bookingServiceImpl.getBookingCurrentUser(owner.getId(), BookingState.FUTURE.toString(), from, size);

        Mockito.verify(bookingRepository).findFutureBookingsForUser(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any());
        Assertions.assertEquals(new ArrayList<>(), result);
    }

    @Test
    void getBookingCurrentUserTest_whenStateWaiting_thenBookings() {
        Mockito.when(userRepository.findById(ArgumentMatchers.anyLong())).thenReturn(Optional.of(owner));

        List<BookingDto> result = bookingServiceImpl.getBookingCurrentUser(owner.getId(), BookingState.WAITING.toString(), from, size);

        Mockito.verify(bookingRepository).findWaitingOrRejectedBookingsForUser(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any());
        Assertions.assertEquals(new ArrayList<>(), result);
    }

    @Test
    void getBookingCurrentUserTest_whenStateRejected_thenBookings() {
        Mockito.when(userRepository.findById(ArgumentMatchers.anyLong())).thenReturn(Optional.of(owner));

        List<BookingDto> result = bookingServiceImpl.getBookingCurrentUser(owner.getId(), BookingState.REJECTED.toString(), from, size);

        Mockito.verify(bookingRepository).findWaitingOrRejectedBookingsForUser(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any());
        Assertions.assertEquals(new ArrayList<>(), result);
    }

    @Test
    void getBookingItemsCurrentUserTest_whenStateIllegal_thenThrowException() {
        Mockito.when(userRepository.findById(ArgumentMatchers.anyLong())).thenReturn(Optional.of(owner));

        Assertions.assertThrows(IllegalArgumentException.class,
                () -> bookingServiceImpl.getBookingForItemsCurrentUser(2L, "State", from, size));
    }

    @Test
    void getBookingItemsCurrentUserTest_whenStateAll_thenBookings() {
        Mockito.when(userRepository.findById(ArgumentMatchers.anyLong())).thenReturn(Optional.of(owner));

        List<BookingDto> result = bookingServiceImpl.getBookingForItemsCurrentUser(owner.getId(), BookingState.ALL.toString(), from, size);

        Mockito.verify(bookingRepository).findAllBookingsForItems(ArgumentMatchers.any(), ArgumentMatchers.any());
        Assertions.assertEquals(new ArrayList<>(), result);
    }

    @Test
    void getBookingItemsCurrentUserTest_whenStateCurrent_thenBookings() {
        Mockito.when(userRepository.findById(ArgumentMatchers.anyLong())).thenReturn(Optional.of(owner));

        List<BookingDto> result = bookingServiceImpl.getBookingForItemsCurrentUser(owner.getId(), BookingState.CURRENT.toString(), from, size);

        Mockito.verify(bookingRepository).findCurrentBookingsForItems(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any());
        Assertions.assertEquals(new ArrayList<>(), result);
    }

    @Test
    void getBookingItemsCurrentUserTest_whenStatePast_thenBookings() {
        Mockito.when(userRepository.findById(ArgumentMatchers.anyLong())).thenReturn(Optional.of(owner));

        List<BookingDto> result = bookingServiceImpl.getBookingForItemsCurrentUser(owner.getId(), BookingState.PAST.toString(), from, size);

        Mockito.verify(bookingRepository).findPastBookingsForItems(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any());
        Assertions.assertEquals(new ArrayList<>(), result);
    }

    @Test
    void getBookingItemsCurrentUserTest_whenStateFuture_thenBookings() {
        Mockito.when(userRepository.findById(ArgumentMatchers.anyLong())).thenReturn(Optional.of(owner));

        List<BookingDto> result = bookingServiceImpl.getBookingForItemsCurrentUser(owner.getId(), BookingState.FUTURE.toString(), from, size);

        Mockito.verify(bookingRepository).findFutureBookingsForItems(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any());
        Assertions.assertEquals(new ArrayList<>(), result);
    }

    @Test
    void getBookingItemsCurrentUserTest_whenStateWaiting_thenBookings() {
        Mockito.when(userRepository.findById(ArgumentMatchers.anyLong())).thenReturn(Optional.of(owner));

        List<BookingDto> result = bookingServiceImpl.getBookingForItemsCurrentUser(owner.getId(), BookingState.WAITING.toString(), from, size);

        Mockito.verify(bookingRepository).findWaitingOrRejectedBookingsForItems(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any());
        Assertions.assertEquals(new ArrayList<>(), result);
    }

    @Test
    void getBookingItemsCurrentUserTest_whenStateRejected_thenBookings() {
        Mockito.when(userRepository.findById(ArgumentMatchers.anyLong())).thenReturn(Optional.of(owner));

        List<BookingDto> result = bookingServiceImpl.getBookingForItemsCurrentUser(owner.getId(), BookingState.REJECTED.toString(), from, size);

        Mockito.verify(bookingRepository).findWaitingOrRejectedBookingsForItems(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any());
        Assertions.assertEquals(new ArrayList<>(), result);
    }
}

