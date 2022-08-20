package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.Item;

import java.util.List;

public interface ItemService {

    Item createItem(Item item);

    Item updateItem(Long itemId, Item toItem);

    Item getItemById(Long itemId);

    List<Item> getAllItemsForUser(Long userId);

    List<Item> getItemsWithKeyWord(String keyWord);
}
