package ru.practicum.shareit.requests.dto;

import ru.practicum.shareit.requests.ItemRequest;
import ru.practicum.shareit.user.User;

public class ItemRequestMapper {

    public static ItemRequestDto toItemRequestDto(ItemRequest itemRequest) {
        return new ItemRequestDto(
            itemRequest.getId(),
            itemRequest.getDescription(),
            itemRequest.getCreated()
        );
    }

    public static ItemRequest toItemRequest(User user, ItemRequestDto itemRequestDto) {
        return new ItemRequest(
            itemRequestDto.getId(),
            itemRequestDto.getDescription(),
            user,
            itemRequestDto.getCreated()
        );
    }
}
