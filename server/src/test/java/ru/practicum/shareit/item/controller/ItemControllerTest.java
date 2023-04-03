package ru.practicum.shareit.item.controller;

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
import ru.practicum.shareit.item.CommentDto;
import ru.practicum.shareit.item.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import java.nio.charset.StandardCharsets;
import java.util.List;

@WebMvcTest(controllers = ItemController.class)
@AutoConfigureMockMvc
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ItemControllerTest {
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    MockMvc mockMvc;

    @MockBean
    @Qualifier("ItemServiceImpl")
    ItemService itemService;

    ItemDto itemDtoCorrect;
    ItemDto itemDtoEmptyName;
    CommentDto commentDtoCorrect;
    CommentDto commentDtoEmptyText;
    final String pathItems = "/items";
    final String pathItemId = "/{itemId}";
    final String pathComment = "/comment";
    final String headerUserValue = "X-Sharer-User-Id";


    @BeforeEach
    void beforeEach() {
        itemDtoCorrect = ItemDto.builder().id(1L).name("itemCorrect").description("itemCorrectDesc").available(true).build();
        itemDtoEmptyName = ItemDto.builder().id(2L).name("").description("itemEmptyNameDesc").available(true).build();
        commentDtoCorrect = CommentDto.builder().id(1L).text("commentCorrect").build();
        commentDtoEmptyText = CommentDto.builder().id(2L).text("").build();
    }

    @SneakyThrows
    @Test
    void addItemTest_whenItemCorrect_thenReturnOK() {
        Mockito.when(itemService.addNewItem(ArgumentMatchers.any(), ArgumentMatchers.anyLong())).thenReturn(itemDtoCorrect);

        String result = mockMvc.perform(MockMvcRequestBuilders.post(pathItems)
                        .content(objectMapper.writeValueAsString(itemDtoCorrect))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(headerUserValue, 1))
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);
        Mockito.verify(itemService).addNewItem(ArgumentMatchers.any(), ArgumentMatchers.anyLong());
        Assertions.assertEquals(objectMapper.writeValueAsString(itemDtoCorrect), result);
    }

    @SneakyThrows
    @Test
    void addItemTest_whenBookingNameEmpty_thenThrow() {
        Mockito.when(itemService.addNewItem(ArgumentMatchers.any(), ArgumentMatchers.anyLong())).thenThrow(new ValidationException("Название не может быть пустым"));

        String result = mockMvc.perform(MockMvcRequestBuilders.post(pathItems)
                        .content(objectMapper.writeValueAsString(itemDtoEmptyName))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(headerUserValue, 1))
                .andExpect(MockMvcResultMatchers.status().is4xxClientError())
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);

        Mockito.verify(itemService).addNewItem(ArgumentMatchers.any(), ArgumentMatchers.anyLong());
        Assertions.assertEquals("{\"error\":\"Название не может быть пустым\"}", result);
    }

    @SneakyThrows
    @Test
    void addCommentTest_whenCommentCorrect_thenReturnOK() {
        Mockito.when(itemService.addNewComment(ArgumentMatchers.any(), ArgumentMatchers.anyLong(), ArgumentMatchers.anyLong())).thenReturn(commentDtoCorrect);

        String result = mockMvc.perform(MockMvcRequestBuilders.post(pathItems + pathItemId + pathComment, 1)
                        .content(objectMapper.writeValueAsString(commentDtoCorrect))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(headerUserValue, 1))
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);
        Mockito.verify(itemService).addNewComment(ArgumentMatchers.any(), ArgumentMatchers.anyLong(), ArgumentMatchers.anyLong());
        Assertions.assertEquals(objectMapper.writeValueAsString(commentDtoCorrect), result);
    }

    @SneakyThrows
    @Test
    void addCommentTest_whenCommentTextEmpty_thenThrow() {
        Mockito.when(itemService.addNewComment(ArgumentMatchers.any(), ArgumentMatchers.anyLong(), ArgumentMatchers.anyLong())).thenThrow(new ValidationException("Описание не может быть пустым"));

        String result = mockMvc.perform(MockMvcRequestBuilders.post(pathItems + pathItemId + pathComment, 1)
                        .content(objectMapper.writeValueAsString(commentDtoEmptyText))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(headerUserValue, 1))
                .andExpect(MockMvcResultMatchers.status().is4xxClientError())
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);

        Mockito.verify(itemService).addNewComment(ArgumentMatchers.any(), ArgumentMatchers.anyLong(), ArgumentMatchers.anyLong());
        Assertions.assertEquals("{\"error\":\"Описание не может быть пустым\"}", result);
    }


    @SneakyThrows
    @Test
    void updateItemTest_whenItemCorrect_thenReturnOK() {
        long itemId = 1L;
        Mockito.when(itemService.updateItem(ArgumentMatchers.anyLong(), ArgumentMatchers.any(), ArgumentMatchers.anyLong())).thenReturn(itemDtoCorrect);

        String result = mockMvc.perform(MockMvcRequestBuilders.patch(pathItems + pathItemId, itemId)
                        .content(objectMapper.writeValueAsString(itemDtoCorrect))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(headerUserValue, 1))
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);
        Mockito.verify(itemService).updateItem(ArgumentMatchers.anyLong(), ArgumentMatchers.any(), ArgumentMatchers.anyLong());
        Assertions.assertEquals(objectMapper.writeValueAsString(itemDtoCorrect), result);
    }

    @SneakyThrows
    @Test
    void getAllItemsTest() {
        LinkedMultiValueMap<String, String> requestParams = new LinkedMultiValueMap<>();
        requestParams.add("from", "0");
        requestParams.add("size", "10");

        Mockito.when(itemService.getAllItems(ArgumentMatchers.anyLong(), ArgumentMatchers.anyInt(), ArgumentMatchers.anyInt())).thenReturn(List.of(itemDtoCorrect));

        mockMvc.perform(MockMvcRequestBuilders.get(pathItems)
                        .header(headerUserValue, 1)
                        .params(requestParams))
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful());

        Mockito.verify(itemService).getAllItems(ArgumentMatchers.anyLong(), ArgumentMatchers.anyInt(), ArgumentMatchers.anyInt());
    }

    @SneakyThrows
    @Test
    void getItemByIdTest() {
        Mockito.when(itemService.getItemById(ArgumentMatchers.anyLong(), ArgumentMatchers.anyLong())).thenReturn(itemDtoCorrect);

        mockMvc.perform(MockMvcRequestBuilders.get(pathItems + pathItemId, 1).header(headerUserValue, 1)).andExpect(MockMvcResultMatchers.status().is2xxSuccessful());

        Mockito.verify(itemService).getItemById(ArgumentMatchers.anyLong(), ArgumentMatchers.anyLong());
    }

    @SneakyThrows
    @Test
    void getAllItemsBySearchTest() {
        LinkedMultiValueMap<String, String> requestParams = new LinkedMultiValueMap<>();
        requestParams.add("from", "0");
        requestParams.add("size", "10");
        requestParams.add("text", "itemCorrectDesc");

        Mockito.when(itemService.getItemByNameOrDescription(ArgumentMatchers.anyString(), ArgumentMatchers.anyLong(), ArgumentMatchers.anyInt(), ArgumentMatchers.anyInt())).thenReturn(List.of(itemDtoCorrect));

        mockMvc.perform(MockMvcRequestBuilders.get(pathItems + "/search")
                        .header(headerUserValue, 1)
                        .params(requestParams))
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful());

        Mockito.verify(itemService).getItemByNameOrDescription(ArgumentMatchers.anyString(), ArgumentMatchers.anyLong(), ArgumentMatchers.anyInt(), ArgumentMatchers.anyInt());
    }
}