package ru.practicum.shareit.booking.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AccessLevel;
import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.util.LinkedMultiValueMap;
import ru.practicum.shareit.booking.BookingController;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exception.IsAlreadyDoneException;
import ru.practicum.shareit.exception.ValidationException;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;


@WebMvcTest(controllers = BookingController.class)
@AutoConfigureMockMvc
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BookingControllerTest {
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    MockMvc mockMvc;

    @MockBean
    @Qualifier("BookingServiceImpl")
    BookingService bookingService;

    BookingDto bookingDtoCorrect;
    BookingDto bookingDtoEmptyEnd;
    BookingDto bookingDtoIsAlreadyApproved;
    final String pathBookings = "/bookings";
    final String pathBookingId = "/{bookingId}";
    final String headerUserValue = "X-Sharer-User-Id";


    @BeforeEach
    void beforeEach() {
        bookingDtoCorrect = BookingDto.builder().id(1L).start(LocalDateTime.now())
                .end(LocalDateTime.now().plus(10, ChronoUnit.DAYS)).status(BookingStatus.WAITING).build();
        bookingDtoEmptyEnd = BookingDto.builder().id(2L).start(LocalDateTime.now())
                .end(null).status(BookingStatus.WAITING).build();
        bookingDtoIsAlreadyApproved = BookingDto.builder().id(3L).start(LocalDateTime.now())
                .end(LocalDateTime.now().plus(10, ChronoUnit.DAYS)).status(BookingStatus.APPROVED).build();
    }

    @SneakyThrows
    @Test
    void addBookingTest_whenBookingCorrect_thenReturnOK() {
        Mockito.when(bookingService.addNewBooking(ArgumentMatchers.any(), ArgumentMatchers.anyLong())).thenReturn(bookingDtoCorrect);

        String result = mockMvc.perform(MockMvcRequestBuilders.post(pathBookings)
                        .content(objectMapper.writeValueAsString(bookingDtoCorrect))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(headerUserValue, 1))
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
                .andReturn()
                .getResponse()
                .getContentAsString();
        Mockito.verify(bookingService).addNewBooking(ArgumentMatchers.any(), ArgumentMatchers.anyLong());
        Assertions.assertEquals(objectMapper.writeValueAsString(bookingDtoCorrect), result);
    }

    @SneakyThrows
    @Test
    void addBookingTest_whenBookingEndEmpty_thenThrow() {
        Mockito.when(bookingService.addNewBooking(ArgumentMatchers.any(), ArgumentMatchers.anyLong())).thenThrow(new ValidationException("Start date and end date must not be null"));

        String result = mockMvc.perform(MockMvcRequestBuilders.post(pathBookings)
                        .content(objectMapper.writeValueAsString(bookingDtoEmptyEnd))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(headerUserValue, 1))
                .andExpect(MockMvcResultMatchers.status().is4xxClientError())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Mockito.verify(bookingService).addNewBooking(ArgumentMatchers.any(), ArgumentMatchers.anyLong());
        Assertions.assertEquals("{\"error\":\"Start date and end date must not be null\"}", result);
    }

    @SneakyThrows
    @Test
    void approveBookingTest_whenBookingCorrect_thenReturnOK() {
        long bookingId = 1L;
        Mockito.when(bookingService.approveOrRejectBooking(ArgumentMatchers.anyLong(), ArgumentMatchers.anyLong(), ArgumentMatchers.anyBoolean())).thenReturn(bookingDtoCorrect);

        String result = mockMvc.perform(MockMvcRequestBuilders.patch(pathBookings + pathBookingId, bookingId)
                        .param("approved", "true")
                        .header(headerUserValue, 1)
                        .content(objectMapper.writeValueAsString(bookingDtoCorrect))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
                .andReturn()
                .getResponse()
                .getContentAsString();
        Mockito.verify(bookingService).approveOrRejectBooking(ArgumentMatchers.anyLong(), ArgumentMatchers.anyLong(), ArgumentMatchers.anyBoolean());
        Assertions.assertEquals(objectMapper.writeValueAsString(bookingDtoCorrect), result);
    }

    @SneakyThrows
    @Test
    void approveBookingTest_whenBookingIsAlreadyApproved_thenThrow() {
        long bookingId = 3L;
        Mockito.when(bookingService.approveOrRejectBooking(ArgumentMatchers.anyLong(), ArgumentMatchers.anyLong(), ArgumentMatchers.anyBoolean()))
                .thenThrow(new IsAlreadyDoneException("This booking is already approved"));

        String result = mockMvc.perform(MockMvcRequestBuilders.patch(pathBookings + pathBookingId, bookingId)
                        .param("approved", "true")
                        .header(headerUserValue, 1)
                        .content(objectMapper.writeValueAsString(bookingDtoIsAlreadyApproved))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().is4xxClientError())
                .andReturn()
                .getResponse()
                .getContentAsString();
        Mockito.verify(bookingService).approveOrRejectBooking(ArgumentMatchers.anyLong(), ArgumentMatchers.anyLong(), ArgumentMatchers.anyBoolean());
        Assertions.assertEquals("{\"error\":\"This booking is already approved\"}", result);
    }

    @SneakyThrows
    @Test
    void getBookingsForCurrentUserTest() {
        LinkedMultiValueMap<String, String> requestParams = new LinkedMultiValueMap<>();
        requestParams.add("from", "0");
        requestParams.add("size", "10");
        requestParams.add("state", "ALL");

        Mockito.when(bookingService.getBookingCurrentUser(ArgumentMatchers.anyLong(), ArgumentMatchers.anyString(), ArgumentMatchers.anyInt(), ArgumentMatchers.anyInt())).thenReturn(List.of(bookingDtoCorrect));

        mockMvc.perform(MockMvcRequestBuilders.get(pathBookings)
                        .header(headerUserValue, 1)
                        .params(requestParams))
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful());

        Mockito.verify(bookingService).getBookingCurrentUser(ArgumentMatchers.anyLong(), ArgumentMatchers.anyString(), ArgumentMatchers.anyInt(), ArgumentMatchers.anyInt());
    }

    @SneakyThrows
    @Test
    void getBookingsForItemsCurrentUserTest() {
        LinkedMultiValueMap<String, String> requestParams = new LinkedMultiValueMap<>();
        requestParams.add("from", "0");
        requestParams.add("size", "10");
        requestParams.add("state", "ALL");

        Mockito.when(bookingService.getBookingForItemsCurrentUser(ArgumentMatchers.anyLong(), ArgumentMatchers.anyString(), ArgumentMatchers.anyInt(), ArgumentMatchers.anyInt())).thenReturn(List.of(bookingDtoCorrect));

        mockMvc.perform(MockMvcRequestBuilders.get(pathBookings + "/owner")
                        .header(headerUserValue, 1)
                        .params(requestParams))
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful());

        Mockito.verify(bookingService).getBookingForItemsCurrentUser(ArgumentMatchers.anyLong(), ArgumentMatchers.anyString(), ArgumentMatchers.anyInt(), ArgumentMatchers.anyInt());
    }

    @SneakyThrows
    @Test
    void getBookingByIdTest() {
        Mockito.when(bookingService.getBookingById(ArgumentMatchers.anyLong(), ArgumentMatchers.anyLong())).thenReturn(bookingDtoCorrect);

        mockMvc.perform(MockMvcRequestBuilders.get(pathBookings + pathBookingId, 1).header(headerUserValue, 1)).andExpect(MockMvcResultMatchers.status().is2xxSuccessful());

        Mockito.verify(bookingService).getBookingById(ArgumentMatchers.anyLong(), ArgumentMatchers.anyLong());
    }
}
