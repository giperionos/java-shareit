package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemController.class)
@AutoConfigureMockMvc
class ItemControllerTest {

    @Autowired
    ObjectMapper mapper;

    @MockBean
    ItemClient itemClient;

    @Autowired
    MockMvc mockMvc;

    String headerName = "X-Sharer-User-Id";
    Long userId = 1L;

    Boolean available = Boolean.TRUE;

    Long nullRequestId = null;
    String emptyName = "";
    String emptyDesc = "";
    String emptyText = "";

    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss");

    private ItemDto itemDto = new ItemDto(
            1L,
            "Item_Name_1",
            "Item_Desc_1",
            available,
            nullRequestId
    );

    private ItemDto itemDto2 = new ItemDto(
            2L,
            emptyName,
            "Item_Desc_1",
            available,
            nullRequestId
    );

    private ItemDto itemDto3 = new ItemDto(
            3L,
            "Item_Name_1",
            emptyDesc,
            available,
            nullRequestId
    );

    private CommentDto commentDto3 = new CommentDto(
            3L,
            emptyText,
            "author_3",
            LocalDateTime.parse(LocalDateTime.now().plusHours(3L).format(formatter), formatter)
    );

    @Test
    void getBadResponseOnAddNewCommentForItemWithInvalidText() throws Exception {
        when(itemClient.addNewCommentByItemId(any(Long.class), any(CommentDto.class), any(Long.class)))
                .thenReturn(new ResponseEntity<>(commentDto3, HttpStatus.OK));

        mockMvc.perform(post("/items/" + itemDto.getId() + "/comment")
                        .content(mapper.writeValueAsString(commentDto3))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(headerName, userId))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Не указан сам комментарий."));


        verify(itemClient, times(0)).addNewCommentByItemId(any(Long.class), any(CommentDto.class), any(Long.class));
    }

    @Test
    void getBadResponseOnCreateItemWhenInvalidItemDesc() throws Exception {
        when(itemClient.createItem(any(ItemDto.class), any(Long.class)))
                .thenReturn(new ResponseEntity<>(itemDto, HttpStatus.OK));

        mockMvc.perform(post("/items")
                        .content(mapper.writeValueAsString(itemDto3))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(headerName, userId))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Не указано описание вещи."));

        verify(itemClient, times(0)).createItem(any(ItemDto.class), any(Long.class));
    }

    @Test
    void getBadResponseOnCreateItemWhenInvalidItemName() throws Exception {
        when(itemClient.createItem(any(ItemDto.class), any(Long.class)))
                .thenReturn(new ResponseEntity<>(itemDto, HttpStatus.OK));

        mockMvc.perform(post("/items")
                        .content(mapper.writeValueAsString(itemDto2))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(headerName, userId))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Не указано название вещи."));

        verify(itemClient, times(0)).createItem(any(ItemDto.class), any(Long.class));
    }
}