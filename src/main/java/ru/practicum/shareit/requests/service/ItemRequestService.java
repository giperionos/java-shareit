package ru.practicum.shareit.requests.service;

import ru.practicum.shareit.requests.dto.ItemRequestDto;
import ru.practicum.shareit.requests.dto.ItemRequestWithItemInfoDto;

import java.util.List;

public interface ItemRequestService {
    ItemRequestDto createItemRequest(Long userId, ItemRequestDto itemRequestDto);

    List<ItemRequestWithItemInfoDto> getItemRequestsByOwnerId(Long ownerId);

    List<ItemRequestWithItemInfoDto> getAllItemRequests(Long userId, Integer from, Integer size);

    ItemRequestWithItemInfoDto getItemRequestById(Long userId, Long requestId);
}
