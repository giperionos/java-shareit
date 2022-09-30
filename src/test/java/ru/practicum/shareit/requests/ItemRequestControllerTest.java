package ru.practicum.shareit.requests;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.requests.dto.ItemRequestDto;
import ru.practicum.shareit.requests.dto.ItemRequestWithItemInfoDto;
import ru.practicum.shareit.requests.exceptions.ItemRequestUnknownException;
import ru.practicum.shareit.requests.service.ItemRequestService;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemRequestController.class)
@AutoConfigureMockMvc
class ItemRequestControllerTest {

    @Autowired
    ObjectMapper mapper;

    @MockBean
    ItemRequestService itemRequestService;

    @Autowired
    MockMvc mockMvc;

    Long userId = 1L;
    Long ownerId = 2L;

    String paramFromName = "from";
    String paramFromValue = "0";

    String paramSizeName = "size";
    String paramSizeValue = "2";

    String headerName = "X-Sharer-User-Id";

    String emptyDescription = "";
    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss");

    private ItemRequestDto requestDto = new ItemRequestDto(
            1L,
            "request_1_desc",
            LocalDateTime.parse(LocalDateTime.now().plusHours(1L).format(formatter), formatter)
    );

    private ItemRequestDto requestDto2 = new ItemRequestDto(
            2L,
            "request_2_desc",
            LocalDateTime.parse(LocalDateTime.now().plusHours(2L).format(formatter), formatter)
    );

    private ItemRequestDto badDescriptionRequestDto = new ItemRequestDto(
            3L,
            emptyDescription,
            LocalDateTime.parse(LocalDateTime.now().plusHours(3L).format(formatter), formatter)
    );

    private ItemDto itemDto = new ItemDto(
            1L,
            "item_name_1",
            "item_desc_1",
            Boolean.TRUE,
            requestDto.getId()
    );

    private ItemRequestWithItemInfoDto requestWithItemInfo = new ItemRequestWithItemInfoDto(
            requestDto.getId(),
            requestDto.getDescription(),
            requestDto.getCreated(),
            List.of(itemDto)
    );

    private ItemRequestWithItemInfoDto requestWithItemInfo2 = new ItemRequestWithItemInfoDto(
            requestDto2.getId(),
            requestDto2.getDescription(),
            requestDto2.getCreated(),
            Collections.EMPTY_LIST
    );

    @Test
    void createItemRequest() throws Exception {
        when(itemRequestService.createItemRequest(any(Long.class), any(ItemRequestDto.class)))
                .thenReturn(requestDto);

        mockMvc.perform(post("/requests")
                    .content(mapper.writeValueAsString(requestDto))
                    .characterEncoding(StandardCharsets.UTF_8)
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .header(headerName, userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(requestDto.getId()))
                .andExpect(jsonPath("$.description").value(requestDto.getDescription()))
                .andExpect(jsonPath("$.created").value(requestDto.getCreated().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)));

        verify(itemRequestService, times(1)).createItemRequest(any(Long.class), any(ItemRequestDto.class));
    }

    @Test
    void getItemRequestsByOwnerId() throws Exception {
        when(itemRequestService.getItemRequestsByOwnerId(any(Long.class)))
                .thenReturn(List.of(requestWithItemInfo));

        mockMvc.perform(get("/requests")
                        .accept(MediaType.APPLICATION_JSON)
                        .header(headerName, ownerId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$.[0].id").value(requestWithItemInfo.getId()))
                .andExpect(jsonPath("$.[0].description").value(requestWithItemInfo.getDescription()))
                .andExpect(jsonPath("$.[0].created").value(requestWithItemInfo.getCreated().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)))
                .andExpect(jsonPath("$.[0].items.length()").value(requestWithItemInfo.getItems().size()))
                .andExpect(jsonPath("$.[0].items.[0].id").value(itemDto.getId()))
                .andExpect(jsonPath("$.[0].items.[0].name").value(itemDto.getName()))
                .andExpect(jsonPath("$.[0].items.[0].description").value(itemDto.getDescription()))
                .andExpect(jsonPath("$.[0].items.[0].available").value(itemDto.getAvailable()))
                .andExpect(jsonPath("$.[0].items.[0].requestId").value(itemDto.getRequestId()));

        verify(itemRequestService, times(1)).getItemRequestsByOwnerId(any(Long.class));
    }

    @Test
    void getAllItemRequests() throws Exception {
        when(itemRequestService.getAllItemRequests(any(Long.class), any(PageRequest.class)))
                .thenReturn(List.of(requestWithItemInfo, requestWithItemInfo2));

        mockMvc.perform(get("/requests/all")
                        .accept(MediaType.APPLICATION_JSON)
                        .header(headerName, userId)
                        .param(paramFromName, paramFromValue)
                        .param(paramSizeName, paramSizeValue))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$.[0].id").value(requestWithItemInfo.getId()))
                .andExpect(jsonPath("$.[0].description").value(requestWithItemInfo.getDescription()))
                .andExpect(jsonPath("$.[0].created").value(requestWithItemInfo.getCreated().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)))
                .andExpect(jsonPath("$.[0].items.length()").value(requestWithItemInfo.getItems().size()))
                .andExpect(jsonPath("$.[0].items.[0].id").value(itemDto.getId()))
                .andExpect(jsonPath("$.[0].items.[0].name").value(itemDto.getName()))
                .andExpect(jsonPath("$.[0].items.[0].description").value(itemDto.getDescription()))
                .andExpect(jsonPath("$.[0].items.[0].available").value(itemDto.getAvailable()))
                .andExpect(jsonPath("$.[0].items.[0].requestId").value(itemDto.getRequestId()))
                .andExpect(jsonPath("$.[1].id").value(requestWithItemInfo2.getId()))
                .andExpect(jsonPath("$.[1].description").value(requestWithItemInfo2.getDescription()))
                .andExpect(jsonPath("$.[1].created").value(requestWithItemInfo2.getCreated().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)))
                .andExpect(jsonPath("$.[1].items.length()").value(requestWithItemInfo2.getItems().size()));

        verify(itemRequestService, times(1)).getAllItemRequests(any(Long.class), any(PageRequest.class));
    }

    @Test
    void getItemRequestById() throws Exception {
        when(itemRequestService.getItemRequestById(any(Long.class), any(Long.class)))
                .thenReturn(requestWithItemInfo);

        mockMvc.perform(get("/requests/" + requestWithItemInfo.getId())
                        .accept(MediaType.APPLICATION_JSON)
                        .header(headerName, userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(requestWithItemInfo.getId()))
                .andExpect(jsonPath("$.description").value(requestWithItemInfo.getDescription()))
                .andExpect(jsonPath("$.created").value(requestWithItemInfo.getCreated().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)))
                .andExpect(jsonPath("$.items.length()").value(requestWithItemInfo.getItems().size()))
                .andExpect(jsonPath("$.items.[0].id").value(itemDto.getId()))
                .andExpect(jsonPath("$.items.[0].name").value(itemDto.getName()))
                .andExpect(jsonPath("$.items.[0].description").value(itemDto.getDescription()))
                .andExpect(jsonPath("$.items.[0].available").value(itemDto.getAvailable()))
                .andExpect(jsonPath("$.items.[0].requestId").value(itemDto.getRequestId()));

        verify(itemRequestService, times(1)).getItemRequestById(any(Long.class), any(Long.class));
    }

    @Test
    void getBadRsOnGetItemRequestByUnknownId() throws Exception {
        when(itemRequestService.getItemRequestById(any(Long.class), any(Long.class)))
                .thenThrow(new ItemRequestUnknownException("Запрос вещи не найден."));

        mockMvc.perform(get("/requests/" + requestWithItemInfo.getId())
                        .accept(MediaType.APPLICATION_JSON)
                        .header(headerName, userId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Запрос вещи не найден."));

        verify(itemRequestService, times(1)).getItemRequestById(any(Long.class), any(Long.class));
    }
}