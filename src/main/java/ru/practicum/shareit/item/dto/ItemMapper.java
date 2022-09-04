package ru.practicum.shareit.item.dto;

import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.requests.dto.ItemRequestMapper;
import ru.practicum.shareit.user.User;

public class ItemMapper {

    public static ItemDto toItemDto(Item item) {
        return new ItemDto(
            item.getId(),
            item.getName(),
            item.getDescription(),
            item.getAvailable(),
            item.getRequest() != null ? ItemRequestMapper.toItemRequestDto(item.getRequest()) : null
        );
    }

    public static Item toItem(ItemDto itemDto, User user) {
        return new Item(
            itemDto.getId(),
            itemDto.getName(),
            itemDto.getDescription(),
            itemDto.getAvailable(),
            user,
            itemDto.getRequest() != null ? ItemRequestMapper.toItemRequest(user, itemDto.getRequest()) : null
        );
    }
}
