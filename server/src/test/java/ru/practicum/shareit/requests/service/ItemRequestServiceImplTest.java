package ru.practicum.shareit.requests.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.requests.ItemRequest;
import ru.practicum.shareit.requests.ItemRequestRepository;
import ru.practicum.shareit.requests.dto.ItemRequestDto;
import ru.practicum.shareit.requests.dto.ItemRequestMapper;
import ru.practicum.shareit.requests.dto.ItemRequestWithItemInfoDto;
import ru.practicum.shareit.requests.exceptions.ItemRequestUnknownException;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.exceptions.UserUnknownException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ItemRequestServiceImplTest {

    ItemRequestService itemRequestService;

    ItemRequestRepository itemRequestRepository;
    UserRepository userRepository;
    ItemRepository itemRepository;

    private Long userId = 1L;
    private Long userId2 = 2L;
    private Long requestId = 1L;
    private Long itemId = 1L;
    private Boolean available = Boolean.TRUE;
    private PageRequest pageRequest = PageRequest.of(0, 10, Sort.by("created").descending());
    private Integer from = 0;
    private Integer size = 10;
    private ItemRequestDto itemRequestDto;
    private ItemRequest itemRequest;
    private User user;
    private User user2;

    private ItemRequestWithItemInfoDto itemRequestWithItemInfoDto;
    private ItemDto itemDto;
    private Item item;

    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss");

    @BeforeEach
    void beforeEach() {
        itemRequestRepository = mock(ItemRequestRepository.class);
        userRepository = mock(UserRepository.class);
        itemRepository = mock(ItemRepository.class);

        itemRequestService = new ItemRequestServiceImpl(itemRequestRepository, userRepository, itemRepository);

        user = new User(userId, "User_name_1", "User1@email.ru");
        user2 = new User(userId2, "User_name_2", "User2@email.ru");
        itemRequestDto = new ItemRequestDto(
                1L,
                "Description_1",
                LocalDateTime.parse(LocalDateTime.now().plusHours(1L).format(formatter), formatter)
        );

        itemRequest = ItemRequestMapper.toItemRequest(user, itemRequestDto);

        itemDto = new ItemDto(itemId, "Item_1_name", "Item_desc_1", available, requestId);
        item = new Item(
                itemId,
                "Item_1_name",
                "Item_desc_1",
                available,
                user2,
                itemRequest
        );

        itemRequestWithItemInfoDto = new ItemRequestWithItemInfoDto(
                1L,
                "Description_1",
                LocalDateTime.parse(LocalDateTime.now().plusHours(1L).format(formatter), formatter),
                List.of(itemDto)
        );
    }

    @Test
    void testSuccessCreateItemRequest() {
        when(userRepository.findById(userId))
                .thenReturn(Optional.of(user));

        when(itemRequestRepository.save(any(ItemRequest.class)))
                .thenReturn(itemRequest);

        final ItemRequestDto itemRequestDto1 = itemRequestService.createItemRequest(userId, itemRequestDto);

        assertNotNull(itemRequestDto1);
        assertEquals(itemRequestDto1.getId(), itemRequestDto.getId());
        assertEquals(itemRequestDto1.getDescription(), itemRequestDto.getDescription());
        assertEquals(itemRequestDto1.getCreated(), itemRequestDto.getCreated());

        verify(userRepository, times(1)).findById(userId);
        verify(itemRequestRepository, times(1)).save(any(ItemRequest.class));
    }

    @Test
    void testGetExceptionCreateItemRequestByUnknownId() {
        when(userRepository.findById(userId))
                .thenReturn(Optional.empty());

        when(itemRequestRepository.save(any(ItemRequest.class)))
                .thenReturn(itemRequest);

        final UserUnknownException exception = assertThrows(
                UserUnknownException.class,
                () -> itemRequestService.createItemRequest(userId, itemRequestDto)
        );

        final String expectedMessage = String.format("Пользователь с %d не найден.", userId);

        assertEquals(expectedMessage, exception.getMessage());

        verify(userRepository, times(1)).findById(userId);
        verify(itemRequestRepository, times(0)).save(any(ItemRequest.class));
    }

    @Test
    void testSuccessGetItemRequestsByOwnerId() {
        when(userRepository.findById(userId))
                .thenReturn(Optional.of(user));

        when(itemRequestRepository.getAllByRequester_IdOrderByCreatedDesc(userId))
                .thenReturn(List.of(itemRequest));

        when(itemRepository.findAllByRequest_Id(requestId))
                .thenReturn(List.of(item));

        final List<ItemRequestWithItemInfoDto> requests = itemRequestService.getItemRequestsByOwnerId(userId);

        assertNotNull(requests);
        assertEquals(1, requests.size());

        assertEquals(itemRequestWithItemInfoDto.getId(), requests.get(0).getId());
        assertEquals(itemRequestWithItemInfoDto.getDescription(), requests.get(0).getDescription());
        assertEquals(itemRequestWithItemInfoDto.getCreated(), requests.get(0).getCreated());
        assertEquals(itemRequestWithItemInfoDto.getItems().size(), requests.get(0).getItems().size());
        assertEquals(itemRequestWithItemInfoDto.getItems().get(0).getId(), requests.get(0).getItems().get(0).getId());
        assertEquals(itemRequestWithItemInfoDto.getItems().get(0).getName(), requests.get(0).getItems().get(0).getName());
        assertEquals(itemRequestWithItemInfoDto.getItems().get(0).getDescription(), requests.get(0).getItems().get(0).getDescription());
        assertEquals(itemRequestWithItemInfoDto.getItems().get(0).getAvailable(), requests.get(0).getItems().get(0).getAvailable());
        assertEquals(itemRequestWithItemInfoDto.getItems().get(0).getRequestId(), requests.get(0).getItems().get(0).getRequestId());

        verify(userRepository, times(1)).findById(userId);
        verify(itemRequestRepository, times(1)).getAllByRequester_IdOrderByCreatedDesc(userId);
        verify(itemRepository, times(1)).findAllByRequest_Id(requestId);
    }

    @Test
    void testGetExceptionGetItemRequestsByUnknownOwnerId() {
        when(userRepository.findById(userId))
                .thenReturn(Optional.empty());

        when(itemRequestRepository.getAllByRequester_IdOrderByCreatedDesc(userId))
                .thenReturn(List.of(itemRequest));

        when(itemRepository.findAllByRequest_Id(requestId))
                .thenReturn(List.of(item));

        final UserUnknownException exception = assertThrows(
                UserUnknownException.class,
                () -> itemRequestService.getItemRequestsByOwnerId(userId)
        );

        final String expectedMessage = String.format("Пользователь с %d не найден.", userId);

        assertEquals(expectedMessage, exception.getMessage());

        verify(userRepository, times(1)).findById(userId);
        verify(itemRequestRepository, times(0)).getAllByRequester_IdOrderByCreatedDesc(any(Long.class));
        verify(itemRepository, times(0)).findAllByRequest_Id(any(Long.class));
    }


    @Test
    void testSuccessGetAllItemRequests() {
        when(userRepository.findById(userId2))
                .thenReturn(Optional.of(user2));

        final PageImpl<ItemRequest> page = new PageImpl<>(List.of(itemRequest));
        when(itemRequestRepository.findAll(pageRequest))
                .thenReturn(page);

        when(itemRepository.findAllByRequest_Id(requestId))
                .thenReturn(List.of(item));

        final List<ItemRequestWithItemInfoDto> requests = itemRequestService.getAllItemRequests(userId2, from, size);

        assertNotNull(requests);
        assertEquals(1, requests.size());

        assertEquals(itemRequestWithItemInfoDto.getId(), requests.get(0).getId());
        assertEquals(itemRequestWithItemInfoDto.getDescription(), requests.get(0).getDescription());
        assertEquals(itemRequestWithItemInfoDto.getCreated(), requests.get(0).getCreated());
        assertEquals(itemRequestWithItemInfoDto.getItems().size(), requests.get(0).getItems().size());
        assertEquals(itemRequestWithItemInfoDto.getItems().get(0).getId(), requests.get(0).getItems().get(0).getId());
        assertEquals(itemRequestWithItemInfoDto.getItems().get(0).getName(), requests.get(0).getItems().get(0).getName());
        assertEquals(itemRequestWithItemInfoDto.getItems().get(0).getDescription(), requests.get(0).getItems().get(0).getDescription());
        assertEquals(itemRequestWithItemInfoDto.getItems().get(0).getAvailable(), requests.get(0).getItems().get(0).getAvailable());
        assertEquals(itemRequestWithItemInfoDto.getItems().get(0).getRequestId(), requests.get(0).getItems().get(0).getRequestId());

        verify(userRepository, times(1)).findById(userId2);
        verify(itemRequestRepository, times(1)).findAll(pageRequest);
        verify(itemRepository, times(1)).findAllByRequest_Id(requestId);
    }

    @Test
    void testGetExceptionGetAllItemRequestsWithUnknownUserId() {
        when(userRepository.findById(userId))
                .thenReturn(Optional.empty());

        final PageImpl<ItemRequest> page = new PageImpl<>(List.of(itemRequest));
        when(itemRequestRepository.findAll(pageRequest))
                .thenReturn(page);

        when(itemRepository.findAllByRequest_Id(requestId))
                .thenReturn(List.of(item));

        final UserUnknownException exception = assertThrows(
                UserUnknownException.class,
                () -> itemRequestService.getAllItemRequests(userId, from, size)
        );

        final String expectedMessage = String.format("Пользователь с %d не найден.", userId);

        assertEquals(expectedMessage, exception.getMessage());

        verify(userRepository, times(1)).findById(userId);
        verify(itemRequestRepository, times(0)).findAll(pageRequest);
        verify(itemRepository, times(0)).findAllByRequest_Id(requestId);
    }

    @Test
    void testSuccessGetItemRequestById() {
        when(userRepository.findById(userId))
                .thenReturn(Optional.of(user));

        when(itemRequestRepository.findById(requestId))
                .thenReturn(Optional.of(itemRequest));

        when(itemRepository.findAllByRequest_Id(requestId))
                .thenReturn(List.of(item));

        final ItemRequestWithItemInfoDto itemRequestWithItemInfoDto1 = itemRequestService.getItemRequestById(userId, requestId);

        assertEquals(itemRequestWithItemInfoDto.getId(), itemRequestWithItemInfoDto1.getId());
        assertEquals(itemRequestWithItemInfoDto.getDescription(), itemRequestWithItemInfoDto1.getDescription());
        assertEquals(itemRequestWithItemInfoDto.getCreated(), itemRequestWithItemInfoDto1.getCreated());
        assertEquals(itemRequestWithItemInfoDto.getItems().size(), itemRequestWithItemInfoDto1.getItems().size());
        assertEquals(itemRequestWithItemInfoDto.getItems().get(0).getId(), itemRequestWithItemInfoDto1.getItems().get(0).getId());
        assertEquals(itemRequestWithItemInfoDto.getItems().get(0).getName(), itemRequestWithItemInfoDto1.getItems().get(0).getName());
        assertEquals(itemRequestWithItemInfoDto.getItems().get(0).getDescription(), itemRequestWithItemInfoDto1.getItems().get(0).getDescription());
        assertEquals(itemRequestWithItemInfoDto.getItems().get(0).getAvailable(), itemRequestWithItemInfoDto1.getItems().get(0).getAvailable());
        assertEquals(itemRequestWithItemInfoDto.getItems().get(0).getRequestId(), itemRequestWithItemInfoDto1.getItems().get(0).getRequestId());

        verify(userRepository, times(1)).findById(userId);
        verify(itemRequestRepository, times(1)).findById(requestId);
        verify(itemRepository, times(1)).findAllByRequest_Id(requestId);
    }

    @Test
    void testGetExceptionGetItemRequestByIdWithUnknownUserId() {
        when(userRepository.findById(userId))
                .thenReturn(Optional.empty());

        when(itemRequestRepository.findById(requestId))
                .thenReturn(Optional.of(itemRequest));

        when(itemRepository.findAllByRequest_Id(requestId))
                .thenReturn(List.of(item));

        final UserUnknownException exception = assertThrows(
                UserUnknownException.class,
                () -> itemRequestService.getItemRequestById(userId, requestId)
        );

        final String expectedMessage = String.format("Пользователь с %d не найден.", userId);

        assertEquals(expectedMessage, exception.getMessage());

        verify(userRepository, times(1)).findById(userId);
        verify(itemRequestRepository, times(0)).findById(requestId);
        verify(itemRepository, times(0)).findAllByRequest_Id(requestId);
    }

    @Test
    void testGetExceptionGetItemRequestByUnknownId() {
        when(userRepository.findById(userId))
                .thenReturn(Optional.of(user));

        when(itemRequestRepository.findById(requestId))
                .thenReturn(Optional.empty());

        when(itemRepository.findAllByRequest_Id(requestId))
                .thenReturn(List.of(item));

        final ItemRequestUnknownException exception = assertThrows(
                ItemRequestUnknownException.class,
                () -> itemRequestService.getItemRequestById(userId, requestId)
        );

        final String expectedMessage = String.format("Запрос вещи с %d не найден.", requestId);

        assertEquals(expectedMessage, exception.getMessage());

        verify(userRepository, times(1)).findById(userId);
        verify(itemRequestRepository, times(1)).findById(requestId);
        verify(itemRepository, times(0)).findAllByRequest_Id(requestId);
    }
}