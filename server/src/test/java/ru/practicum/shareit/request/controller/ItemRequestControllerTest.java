package ru.practicum.shareit.request.controller;

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
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemRequestController.class)
@AutoConfigureMockMvc
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ItemRequestControllerTest {
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    MockMvc mockMvc;

    @MockBean
    @Qualifier("ItemRequestServiceImpl")
    ItemRequestService itemRequestService;

    ItemRequestDto itemRequestDtoCorrect;
    ItemRequestDto itemRequestDtoEmptyDesc;
    final String pathRequests = "/requests";
    final String headerUserValue = "X-Sharer-User-Id";

    @BeforeEach
    void beforeEach() {
        itemRequestDtoCorrect = ItemRequestDto.builder().id(1L)
                .created(LocalDateTime.now()).description("itemRequestDescCorrect").build();
        itemRequestDtoEmptyDesc = ItemRequestDto.builder().id(2L)
                .created(LocalDateTime.now()).description("").build();
    }

    @SneakyThrows
    @Test
    void addItemRequestTest_whenItemRequestCorrect_thenReturnOK() {
        Mockito.when(itemRequestService.addNewItemRequest(ArgumentMatchers.anyLong(), ArgumentMatchers.any())).thenReturn(itemRequestDtoCorrect);

        String result = mockMvc.perform(MockMvcRequestBuilders.post(pathRequests)
                        .content(objectMapper.writeValueAsString(itemRequestDtoCorrect))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(headerUserValue, 1))
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);
        Mockito.verify(itemRequestService).addNewItemRequest(ArgumentMatchers.anyLong(), ArgumentMatchers.any());
        Assertions.assertEquals(objectMapper.writeValueAsString(itemRequestDtoCorrect), result);
    }


    @SneakyThrows
    @Test
    void addItemRequestTest_whenItemRequestEmptyDesc_thenThrow() {
        Mockito.when(itemRequestService.addNewItemRequest(ArgumentMatchers.anyLong(), ArgumentMatchers.any())).thenThrow(new ValidationException("Описание должно быть указано"));

        String result = mockMvc.perform(MockMvcRequestBuilders.post(pathRequests)
                        .content(objectMapper.writeValueAsString(itemRequestDtoEmptyDesc))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(headerUserValue, 1))
                .andExpect(MockMvcResultMatchers.status().is4xxClientError())
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);

        Mockito.verify(itemRequestService).addNewItemRequest(ArgumentMatchers.anyLong(), ArgumentMatchers.any());
        Assertions.assertEquals("{\"error\":\"Описание должно быть указано\"}", result);
    }

    @SneakyThrows
    @Test
    void getAllItemRequestsTest() {
        LinkedMultiValueMap<String, String> requestParams = new LinkedMultiValueMap<>();
        requestParams.add("from", "0");
        requestParams.add("size", "10");
        Mockito.when(itemRequestService.getAllItemRequests(ArgumentMatchers.anyInt(), ArgumentMatchers.anyInt(), ArgumentMatchers.anyLong())).thenReturn(List.of(itemRequestDtoCorrect));

        mockMvc.perform(MockMvcRequestBuilders.get(pathRequests + "/all").params(requestParams).header(headerUserValue, 1)).andExpect(MockMvcResultMatchers.status().is2xxSuccessful());

        Mockito.verify(itemRequestService).getAllItemRequests(ArgumentMatchers.anyInt(), ArgumentMatchers.anyInt(), ArgumentMatchers.anyLong());
    }

    @SneakyThrows
    @Test
    void getOwnItemRequestsTest() {
        Mockito.when(itemRequestService.getOwnItemRequests(ArgumentMatchers.anyLong())).thenReturn(List.of(itemRequestDtoCorrect));

        mockMvc.perform(MockMvcRequestBuilders.get(pathRequests).header(headerUserValue, 1)).andExpect(MockMvcResultMatchers.status().is2xxSuccessful());

        Mockito.verify(itemRequestService).getOwnItemRequests(ArgumentMatchers.anyLong());
    }

    @SneakyThrows
    @Test
    void getItemRequestByIdTest() {
        Mockito.when(itemRequestService.getRequestById(ArgumentMatchers.anyLong(), ArgumentMatchers.anyLong())).thenReturn(itemRequestDtoCorrect);

        mockMvc.perform(MockMvcRequestBuilders.get(pathRequests + "/{requestId}", 1).header(headerUserValue, 1)).andExpect(MockMvcResultMatchers.status().is2xxSuccessful());

        Mockito.verify(itemRequestService).getRequestById(ArgumentMatchers.anyLong(), ArgumentMatchers.anyLong());
    }
}