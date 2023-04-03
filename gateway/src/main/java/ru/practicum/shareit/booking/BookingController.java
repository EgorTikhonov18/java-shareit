package ru.practicum.shareit.booking;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookItemRequestDto;
import ru.practicum.shareit.booking.dto.BookingState;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

import static ru.practicum.shareit.Constants.ID;

@Controller
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
@Validated
public class BookingController {
    private final BookingClient bookingClient;

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getBookingById(@RequestHeader(ID) long userId,
                                                 @PathVariable Long bookingId) {
        log.info("Get booking {}, userId={}", bookingId, userId);
        return bookingClient.getBooking(userId, bookingId);
    }

    @GetMapping
    public ResponseEntity<Object> getAllBookingsOfUser(@RequestHeader(ID) long userId,
                                                       @RequestParam(name = "state", defaultValue = "all") String stateParam,
                                                       @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                                       @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
        BookingState state = BookingState.from(stateParam)
                .orElseThrow(() -> new IllegalArgumentException("Unknown state: " + stateParam));
        log.info("Get booking of user with state {}, userId={}, from={}, size={}", stateParam, userId, from, size);
        return bookingClient.getBookingsOfUser(userId, state, from, size);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getAllItemsBookingsOfOwner(@RequestHeader(ID) long userId,
                                                             @RequestParam(name = "state", defaultValue = "all") String stateParam,
                                                             @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                                             @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
        BookingState state = BookingState.from(stateParam)
                .orElseThrow(() -> new IllegalArgumentException("Unknown state: " + stateParam));
        log.info("Get all item bookings for owner with state {}, userId={}, from={}, size={}", stateParam, userId, from, size);
        return bookingClient.getBookingsOfOwner(userId, state, from, size);
    }

    @PostMapping
    public ResponseEntity<Object> addBooking(@RequestHeader(ID) long userId,
                                             @RequestBody @Valid BookItemRequestDto requestDto) {
        log.info("Creating booking {}, userId={}", requestDto, userId);
        return bookingClient.bookItem(userId, requestDto);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> approveBooking(@RequestHeader(ID) Long userId,
                                                 @PathVariable Long bookingId,
                                                 @RequestParam boolean approved) {
        log.info("Approving booking with id-{} ", userId);
        return bookingClient.approveBooking(bookingId, userId, approved);
    }
}

