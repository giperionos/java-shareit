package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemBookingInfoDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithBookingsAndCommentsDto;
import ru.practicum.shareit.item.exceptions.CommentForNotExistBookingException;
import ru.practicum.shareit.item.exceptions.ItemSecurityException;
import ru.practicum.shareit.item.exceptions.ItemUnknownException;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.exceptions.UserUnknownException;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

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
    ItemService itemService;

    @Autowired
    MockMvc mockMvc;

    String headerName = "X-Sharer-User-Id";
    Long userId = 1L;
    Long unknownUserId = 99L;

    String paramFromName = "from";
    String paramFromValue = "0";

    String paramSizeName = "size";
    String paramSizeValue = "2";

    String paramTextName = "text";
    String paramTextValue = "test";

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

    private ItemDto itemDto5 = new ItemDto(
            5L,
            "Item_Name_5",
            "Item_Desc_5",
            available,
            nullRequestId
    );

    private ItemBookingInfoDto lastBooking = new ItemBookingInfoDto(
            1L,
            LocalDateTime.parse(LocalDateTime.now().plusHours(1L).format(formatter), formatter),
            LocalDateTime.parse(LocalDateTime.now().plusDays(1L).format(formatter), formatter),
            1L
    );

    private ItemBookingInfoDto nextBooking = new ItemBookingInfoDto(
            2L,
            LocalDateTime.parse(LocalDateTime.now().plusDays(1L).format(formatter), formatter),
            LocalDateTime.parse(LocalDateTime.now().plusDays(2L).format(formatter), formatter),
            2L
    );

    private CommentDto commentDto1 = new CommentDto(
            1L,
            "text_1",
            "author_1",
            LocalDateTime.parse(LocalDateTime.now().plusHours(1L).format(formatter), formatter)
    );

    private CommentDto commentDto2 = new CommentDto(
            2L,
            "text_2",
            "author_2",
            LocalDateTime.parse(LocalDateTime.now().plusHours(2L).format(formatter), formatter)
    );

    private CommentDto commentDto3 = new CommentDto(
            3L,
            emptyText,
            "author_3",
            LocalDateTime.parse(LocalDateTime.now().plusHours(3L).format(formatter), formatter)
    );

    private ItemWithBookingsAndCommentsDto itemWithBookingsAndCommentsDto = new ItemWithBookingsAndCommentsDto(
            4L,
            "Item_name_4",
            "Item_desc_4",
            available,
            lastBooking,
            nextBooking,
            List.of(commentDto1,commentDto2)
    );

    @Test
    void getSuccessCreateItem() throws Exception {
        when(itemService.createItem(any(ItemDto.class), any(Long.class)))
                .thenReturn(itemDto);

        mockMvc.perform(post("/items")
                    .content(mapper.writeValueAsString(itemDto))
                    .characterEncoding(StandardCharsets.UTF_8)
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .header(headerName, userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(itemDto.getId()))
                .andExpect(jsonPath("$.name").value(itemDto.getName()))
                .andExpect(jsonPath("$.description").value(itemDto.getDescription()))
                .andExpect(jsonPath("$.available").value(itemDto.getAvailable()))
                .andExpect(jsonPath("$.requestId").value(itemDto.getRequestId()));

        verify(itemService, times(1)).createItem(any(ItemDto.class), any(Long.class));
    }

    @Test
    void getBadResponseOnCreateItemWhenNoUserId() throws Exception {
        when(itemService.createItem(any(ItemDto.class), any(Long.class)))
                .thenReturn(itemDto);

        mockMvc.perform(post("/items")
                        .content(mapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(itemService, times(0)).createItem(any(ItemDto.class), any(Long.class));
    }

    @Test
    void getNotFoundResponseOnCreateItemWhenUnknownUserId() throws Exception {
        when(itemService.createItem(any(ItemDto.class), any(Long.class)))
                .thenThrow(new UserUnknownException("Пользователь не найден."));

        mockMvc.perform(post("/items")
                        .content(mapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(headerName, unknownUserId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Пользователь не найден."));

        verify(itemService, times(1)).createItem(any(ItemDto.class), any(Long.class));
    }

    @Test
    void getBadResponseOnCreateItemWhenInvalidItemName() throws Exception {
        when(itemService.createItem(any(ItemDto.class), any(Long.class)))
                .thenReturn(itemDto);

        mockMvc.perform(post("/items")
                        .content(mapper.writeValueAsString(itemDto2))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(headerName, userId))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Не указано название вещи."));

        verify(itemService, times(0)).createItem(any(ItemDto.class), any(Long.class));
    }

    @Test
    void getBadResponseOnCreateItemWhenInvalidItemDesc() throws Exception {
        when(itemService.createItem(any(ItemDto.class), any(Long.class)))
                .thenReturn(itemDto);

        mockMvc.perform(post("/items")
                        .content(mapper.writeValueAsString(itemDto3))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(headerName, userId))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Не указано описание вещи."));

        verify(itemService, times(0)).createItem(any(ItemDto.class), any(Long.class));
    }

    @Test
    void getSuccessUpdateItem() throws Exception {
        when(itemService.updateItem(any(Long.class), any(ItemDto.class), any(Long.class)))
                .thenReturn(itemDto);

        mockMvc.perform(patch("/items/" + itemDto.getId())
                    .content(mapper.writeValueAsString(itemDto))
                    .characterEncoding(StandardCharsets.UTF_8)
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .header(headerName, userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(itemDto.getId()))
                .andExpect(jsonPath("$.name").value(itemDto.getName()))
                .andExpect(jsonPath("$.description").value(itemDto.getDescription()))
                .andExpect(jsonPath("$.available").value(itemDto.getAvailable()))
                .andExpect(jsonPath("$.requestId").value(itemDto.getRequestId()));

        verify(itemService, times(1)).updateItem(any(Long.class), any(ItemDto.class), any(Long.class));
    }

    @Test
    void getNotFoundResponseOnUpdateItemWhenUnknownItemId() throws Exception {
        when(itemService.updateItem(any(Long.class), any(ItemDto.class), any(Long.class)))
                .thenThrow(new ItemUnknownException("Не найдена вещь."));

        mockMvc.perform(patch("/items/" + itemDto.getId())
                        .content(mapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(headerName, userId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Не найдена вещь."));

        verify(itemService, times(1)).updateItem(any(Long.class), any(ItemDto.class), any(Long.class));
    }

    @Test
    void getForbiddenResponseOnNoOwnerUpdateItem() throws Exception {
        when(itemService.updateItem(any(Long.class), any(ItemDto.class), any(Long.class)))
                .thenThrow(new ItemSecurityException("Пользователь не может работать с вещью."));

        mockMvc.perform(patch("/items/" + itemDto.getId())
                        .content(mapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(headerName, userId))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.error").value("Пользователь не может работать с вещью."));

        verify(itemService, times(1)).updateItem(any(Long.class), any(ItemDto.class), any(Long.class));
    }

    @Test
    void getSuccessItemById() throws Exception {
        when(itemService.getItemById(any(Long.class), any(Long.class)))
                .thenReturn(itemWithBookingsAndCommentsDto);

        mockMvc.perform(get("/items/" + itemWithBookingsAndCommentsDto.getId())
                        .accept(MediaType.APPLICATION_JSON)
                        .header(headerName, userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(itemWithBookingsAndCommentsDto.getId()))
                .andExpect(jsonPath("$.name").value(itemWithBookingsAndCommentsDto.getName()))
                .andExpect(jsonPath("$.description").value(itemWithBookingsAndCommentsDto.getDescription()))
                .andExpect(jsonPath("$.available").value(itemWithBookingsAndCommentsDto.getAvailable()))
                .andExpect(jsonPath("$.lastBooking.id").value(lastBooking.getId()))
                .andExpect(jsonPath("$.lastBooking.start").value(lastBooking.getStart().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)))
                .andExpect(jsonPath("$.lastBooking.end").value(lastBooking.getEnd().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)))
                .andExpect(jsonPath("$.lastBooking.bookerId").value(lastBooking.getBookerId()))
                .andExpect(jsonPath("$.nextBooking.id").value(nextBooking.getId()))
                .andExpect(jsonPath("$.nextBooking.start").value(nextBooking.getStart().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)))
                .andExpect(jsonPath("$.nextBooking.end").value(nextBooking.getEnd().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)))
                .andExpect(jsonPath("$.nextBooking.bookerId").value(nextBooking.getBookerId()))
                .andExpect(jsonPath("$.comments.length()").value(2))
                .andExpect(jsonPath("$.comments.[0].id").value(commentDto1.getId()))
                .andExpect(jsonPath("$.comments.[0].text").value(commentDto1.getText()))
                .andExpect(jsonPath("$.comments.[0].authorName").value(commentDto1.getAuthorName()))
                .andExpect(jsonPath("$.comments.[0].created").value(commentDto1.getCreated().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)))
                .andExpect(jsonPath("$.comments.[1].id").value(commentDto2.getId()))
                .andExpect(jsonPath("$.comments.[1].text").value(commentDto2.getText()))
                .andExpect(jsonPath("$.comments.[1].authorName").value(commentDto2.getAuthorName()))
                .andExpect(jsonPath("$.comments.[1].created").value(commentDto2.getCreated().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)));

        verify(itemService, times(1)).getItemById(any(Long.class), any(Long.class));
    }

    @Test
    void getSuccessAllItemsForUserId() throws Exception {
        when(itemService.getAllItemsForUser(any(Long.class), any(PageRequest.class)))
                .thenReturn(List.of(itemWithBookingsAndCommentsDto));

        mockMvc.perform(get("/items")
                        .accept(MediaType.APPLICATION_JSON)
                        .header(headerName, userId)
                        .param(paramFromName, paramFromValue)
                        .param(paramSizeName, paramSizeValue))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].id").value(itemWithBookingsAndCommentsDto.getId()))
                .andExpect(jsonPath("$.[0].name").value(itemWithBookingsAndCommentsDto.getName()))
                .andExpect(jsonPath("$.[0].description").value(itemWithBookingsAndCommentsDto.getDescription()))
                .andExpect(jsonPath("$.[0].available").value(itemWithBookingsAndCommentsDto.getAvailable()))
                .andExpect(jsonPath("$.[0].lastBooking.id").value(lastBooking.getId()))
                .andExpect(jsonPath("$.[0].lastBooking.start").value(lastBooking.getStart().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)))
                .andExpect(jsonPath("$.[0].lastBooking.end").value(lastBooking.getEnd().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)))
                .andExpect(jsonPath("$.[0].lastBooking.bookerId").value(lastBooking.getBookerId()))
                .andExpect(jsonPath("$.[0].nextBooking.id").value(nextBooking.getId()))
                .andExpect(jsonPath("$.[0].nextBooking.start").value(nextBooking.getStart().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)))
                .andExpect(jsonPath("$.[0].nextBooking.end").value(nextBooking.getEnd().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)))
                .andExpect(jsonPath("$.[0].nextBooking.bookerId").value(nextBooking.getBookerId()))
                .andExpect(jsonPath("$.[0].comments.length()").value(2))
                .andExpect(jsonPath("$.[0].comments.[0].id").value(commentDto1.getId()))
                .andExpect(jsonPath("$.[0].comments.[0].text").value(commentDto1.getText()))
                .andExpect(jsonPath("$.[0].comments.[0].authorName").value(commentDto1.getAuthorName()))
                .andExpect(jsonPath("$.[0].comments.[0].created").value(commentDto1.getCreated().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)))
                .andExpect(jsonPath("$.[0].comments.[1].id").value(commentDto2.getId()))
                .andExpect(jsonPath("$.[0].comments.[1].text").value(commentDto2.getText()))
                .andExpect(jsonPath("$.[0].comments.[1].authorName").value(commentDto2.getAuthorName()))
                .andExpect(jsonPath("$.[0].comments.[1].created").value(commentDto2.getCreated().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)));


        verify(itemService, times(1)).getAllItemsForUser(any(Long.class), any(PageRequest.class));
    }

    @Test
    void getSuccessSearchItemsWithText() throws Exception {
        when(itemService.getItemsWithKeyWord(any(String.class), any(PageRequest.class)))
                .thenReturn(List.of(itemDto, itemDto5));

        mockMvc.perform(get("/items/search")
                        .accept(MediaType.APPLICATION_JSON)
                        .header(headerName, userId)
                        .param(paramTextName, paramTextValue)
                        .param(paramFromName, paramFromValue)
                        .param(paramSizeName, paramSizeValue))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$.[0].id").value(itemDto.getId()))
                .andExpect(jsonPath("$.[0].name").value(itemDto.getName()))
                .andExpect(jsonPath("$.[0].description").value(itemDto.getDescription()))
                .andExpect(jsonPath("$.[0].available").value(itemDto.getAvailable()))
                .andExpect(jsonPath("$.[0].requestId").value(itemDto.getRequestId()))
                .andExpect(jsonPath("$.[1].id").value(itemDto5.getId()))
                .andExpect(jsonPath("$.[1].name").value(itemDto5.getName()))
                .andExpect(jsonPath("$.[1].description").value(itemDto5.getDescription()))
                .andExpect(jsonPath("$.[1].available").value(itemDto5.getAvailable()))
                .andExpect(jsonPath("$.[1].requestId").value(itemDto5.getRequestId()));

        verify(itemService, times(1)).getItemsWithKeyWord(any(String.class), any(PageRequest.class));
    }

    @Test
    void getSuccessAddNewCommentForItem() throws Exception {
        when(itemService.addNewCommentByItemId(any(Long.class), any(CommentDto.class), any(Long.class)))
                .thenReturn(commentDto1);

        mockMvc.perform(post("/items/" + itemDto.getId() + "/comment")
                    .content(mapper.writeValueAsString(commentDto1))
                    .characterEncoding(StandardCharsets.UTF_8)
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .header(headerName, userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(commentDto1.getId()))
                .andExpect(jsonPath("$.text").value(commentDto1.getText()))
                .andExpect(jsonPath("$.authorName").value(commentDto1.getAuthorName()))
                .andExpect(jsonPath("$.created").value(commentDto1.getCreated().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)));


        verify(itemService, times(1)).addNewCommentByItemId(any(Long.class), any(CommentDto.class), any(Long.class));
    }

    @Test
    void getBadResponseOnAddNewCommentForItemWithInvalidText() throws Exception {
        when(itemService.addNewCommentByItemId(any(Long.class), any(CommentDto.class), any(Long.class)))
                .thenReturn(commentDto3);

        mockMvc.perform(post("/items/" + itemDto.getId() + "/comment")
                        .content(mapper.writeValueAsString(commentDto3))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(headerName, userId))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Не указан сам комментарий."));


        verify(itemService, times(0)).addNewCommentByItemId(any(Long.class), any(CommentDto.class), any(Long.class));
    }

    @Test
    void getBadResponseOnAddNewCommentForNoBookingItem() throws Exception {
        when(itemService.addNewCommentByItemId(any(Long.class), any(CommentDto.class), any(Long.class)))
                .thenThrow(new CommentForNotExistBookingException("Пользователь не брал в аренду вещь."));

        mockMvc.perform(post("/items/" + itemDto.getId() + "/comment")
                        .content(mapper.writeValueAsString(commentDto1))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(headerName, userId))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Пользователь не брал в аренду вещь."));


        verify(itemService, times(1)).addNewCommentByItemId(any(Long.class), any(CommentDto.class), any(Long.class));
    }
}