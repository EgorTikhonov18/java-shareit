package ru.practicum.shareit.booking.mapper;

import ru.practicum.shareit.booking.dto.RequestBodyBookingDto;
import ru.practicum.shareit.booking.model.Booking;

public class RequestBodyBookingDtoMapper {
    public static Booking mapRow(RequestBodyBookingDto requestBodyBookingDto) {
        return Booking.builder()
                .start(requestBodyBookingDto.getStart())
                .end(requestBodyBookingDto.getEnd())
                .build();
    }
}
