package ru.practicum.shareit.item.service;

import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithBookingsAndCommentsDto;

import java.util.List;

public interface ItemService {

    ItemDto createItem(ItemDto itemDto, Long userId);

    ItemDto updateItem(Long itemId, ItemDto itemDto, Long userId);

    ItemWithBookingsAndCommentsDto getItemById(Long itemId, Long userId);

    List<ItemWithBookingsAndCommentsDto> getAllItemsForUser(Long userId, PageRequest pageRequest);

    List<ItemDto> getItemsWithKeyWord(String keyWord, PageRequest pageRequest);

    CommentDto addNewCommentByItemId(Long itemId, CommentDto commentDto, Long userId);
}
