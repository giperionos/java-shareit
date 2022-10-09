package ru.practicum.shareit.requests.dto;

import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.requests.ItemRequest;
import ru.practicum.shareit.user.User;

import java.util.List;
import java.util.stream.Collectors;

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

    public static ItemRequestWithItemInfoDto toItemRequestWithItemInfoDto(ItemRequest itemRequest, List<Item> items) {
        return new ItemRequestWithItemInfoDto(
            itemRequest.getId(),
            itemRequest.getDescription(),
            itemRequest.getCreated(),
            items == null ? null : items.stream().map(ItemMapper::toItemDto).collect(Collectors.toList())
        );
    }
}
