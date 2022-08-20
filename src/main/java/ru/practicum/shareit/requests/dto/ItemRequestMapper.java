package ru.practicum.shareit.requests.dto;

import ru.practicum.shareit.requests.ItemRequest;

public class ItemRequestMapper {

    public static ItemRequestDto toItemRequestDto(ItemRequest itemRequest) {
        return new ItemRequestDto(
            itemRequest.getId(),
            itemRequest.getDescription(),
            itemRequest.getCreated()
        );
    }

    public static ItemRequest toItemRequest(Long userId, ItemRequestDto itemRequestDto) {
        return new ItemRequest(
            itemRequestDto.getId(),
            itemRequestDto.getDescription(),
            userId,
            itemRequestDto.getCreated()
        );
    }
}
