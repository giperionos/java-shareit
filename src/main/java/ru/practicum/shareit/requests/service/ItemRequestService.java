package ru.practicum.shareit.requests.service;

import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.requests.dto.ItemRequestDto;
import ru.practicum.shareit.requests.dto.ItemRequestWithItemInfoDto;

import java.util.List;

public interface ItemRequestService {
    ItemRequestDto createItemRequest(Long userId, ItemRequestDto itemRequestDto);

    List<ItemRequestWithItemInfoDto> getItemRequestsByOwnerId(Long ownerId);

    List<ItemRequestWithItemInfoDto> getAllItemRequests(Long userId, PageRequest pageRequest);

    ItemRequestWithItemInfoDto getItemRequestById(Long userId, Long requestId);
}
