package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithBookingsAndCommentsDto;

import java.util.List;

public interface ItemService {

    ItemDto createItem(ItemDto itemDto, Long userId);

    ItemDto updateItem(Long itemId, ItemDto itemDto, Long userId);

    ItemWithBookingsAndCommentsDto getItemById(Long itemId, Long userId);

    List<ItemWithBookingsAndCommentsDto> getAllItemsForUser(Long userId, Integer from, Integer size);

    List<ItemDto> getItemsWithKeyWord(String keyWord, Integer from, Integer size);

    CommentDto addNewCommentByItemId(Long itemId, CommentDto commentDto, Long userId);
}
