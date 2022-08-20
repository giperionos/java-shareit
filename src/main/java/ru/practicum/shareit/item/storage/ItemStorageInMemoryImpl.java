package ru.practicum.shareit.item.storage;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.exceptions.ItemUnknownException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class ItemStorageInMemoryImpl implements ItemStorage {

    private static Long ID = 0L;
    private Map<Long, Item> items = new HashMap<>();

    @Override
    public Item createItem(Item item) {

        item.setId(++ID);

        items.put(item.getId(), item);

        return item;
    }

    @Override
    public Item updateItem(Item item) {

        //нужно проверить что такая вещи есть
        if (items.get(item.getId()) == null) {
            throw new ItemUnknownException(String.format("Не найдена вещь с id = %d", item.getId()));
        }

        items.put(item.getId(), item);

        return item;
    }

    @Override
    public Item getItemById(Long itemId) {

        Item foundedItem = items.get(itemId);

        if (foundedItem == null) {
            throw new ItemUnknownException(String.format("Не найдена вещь с id = %d", itemId));
        }

        return foundedItem;
    }

    @Override
    public Item getAvailableItemById(Long itemId) {
        Item foundedItem = getItemById(itemId);

        if (!foundedItem.getAvailable()) {
            throw new ItemUnknownException(String.format("Не найдена вещь с id = %d", itemId));
        }

        return foundedItem;
    }

    @Override
    public List<Item> getAllUserItems(Long userId) {
        return items.values().stream().filter((item) -> item.getOwnerId().longValue() == userId.longValue()).collect(Collectors.toList());
    }

    @Override
    public List<Item> getAvailableItemsWithKeyWord(String keyWord) {

        return items.values()
                .stream()
                .filter((Item item) -> {
                    StringBuilder sb = new StringBuilder();
                    sb.append(item.getName());
                    sb.append(" ");
                    sb.append(item.getDescription());

                    String textForSearch = sb.toString();

                    if (textForSearch.toLowerCase().contains(keyWord.toLowerCase())) {
                        return true;
                    }

                    return false;
                })
                .filter((Item::getAvailable))
                .collect(Collectors.toList());
    }
}
