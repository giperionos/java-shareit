package ru.practicum.shareit.item.storage;

import ru.practicum.shareit.item.Item;

import java.util.List;

public interface ItemStorage {

    Item createItem(Item item);

    Item updateItem(Item item);

    Item getItemById(Long itemId);

    Item getAvailableItemById(Long itemId);

    List<Item> getAllUserItems(Long userId);

    List<Item> getAvailableItemsWithKeyWord(String keyWord);
}
