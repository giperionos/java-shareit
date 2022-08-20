package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.exceptions.ItemSecurityException;
import ru.practicum.shareit.item.exceptions.ItemUnknownException;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.exceptions.UserUnknownException;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ItemServiceImpl implements ItemService {

    private final ItemStorage itemStorage;
    private final UserStorage userStorage;

    @Override
    public Item createItem(Item item) {

        //сначала нужно убедиться, что такой пользователь существует
        try {
            userStorage.getUserById(item.getOwnerId());
        } catch (UserUnknownException e) {
            log.info("Ошибка при добавлении вещи: {}", e.getMessage());
            throw e;
        }

        return itemStorage.createItem(item);
    }

    @Override
    public Item updateItem(Long itemId, Item item) {
        //сначала нужно убедиться, что такой пользователь существует
        User currentUser;

        try {
            currentUser = userStorage.getUserById(item.getOwnerId());
        } catch (UserUnknownException e) {
            log.info("Ошибка при обновлении информации о вещи: {}", e.getMessage());
            throw e;
        }

        //также нужно проверить, то эта вещь принадлежит этому владельцу
        //сначала проверить, что такая вещь вообще есть
        Item foundedItem;

        try {
            foundedItem = itemStorage.getItemById(itemId);
        } catch (ItemUnknownException e) {
            log.info("Ошибка при обновлении информации о вещи: {}", e.getMessage());
            throw e;
        }

        //если id пользователя из вещи не совпадает с id пользователя,
        //который пришел из контролера,
        //значит пользователь с фронта пытается редактировать не свою вещь
        if (foundedItem.getOwnerId().longValue() != currentUser.getId().longValue()) {
            throw new ItemSecurityException(String.format("Пользователь с id = %d не может работать с вещью с id = %d",
                    currentUser.getId(), foundedItem.getId()));
        }

        //если все ок, значит можно редактировать
        //создать новый объект вещи, как копию из хранилища,
        //чтобы не менять поля того объекта, что уже в мапе
        Item itemForUpdate = new Item(
            foundedItem.getId(),
            foundedItem.getName(),
            foundedItem.getDescription(),
            foundedItem.getAvailable(),
            foundedItem.getOwnerId(),
            foundedItem.getRequestId()
        );

        //обновить нужно только те поля, что пришли
        if (item.getName() != null) {
            itemForUpdate.setName(item.getName());
        }

        if (item.getDescription() != null) {
            itemForUpdate.setDescription(item.getDescription());
        }

        if (item.getAvailable() != null) {
            itemForUpdate.setAvailable(item.getAvailable());
        }

        //теперь обновить в мапе
        return itemStorage.updateItem(itemForUpdate);
    }

    @Override
    public Item getItemById(Long itemId) {
        return itemStorage.getAvailableItemById(itemId);
    }

    @Override
    public List<Item> getAllItemsForUser(Long userId) {
        //сначала нужно убедиться, что такой пользователь существует
        try {
            userStorage.getUserById(userId);
        } catch (UserUnknownException e) {
            log.info("Ошибка при получении списка вещей пользователя: {}", e.getMessage());
            throw e;
        }

        //получить все вещи пользователя
        return itemStorage.getAllUserItems(userId);
    }

    @Override
    public List<Item> getItemsWithKeyWord(String keyWord) {
        if (keyWord == null || keyWord.isBlank()) {
            return new ArrayList<>();
        }

        return itemStorage.getAvailableItemsWithKeyWord(keyWord);
    }
}
