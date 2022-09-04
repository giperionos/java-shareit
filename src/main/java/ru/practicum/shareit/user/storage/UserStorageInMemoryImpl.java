package ru.practicum.shareit.user.storage;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.exceptions.UserAlreadyExistEmailException;
import ru.practicum.shareit.user.exceptions.UserUnknownException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class UserStorageInMemoryImpl implements UserStorage {

    private static Long ID = 0L;
    private Map<Long, User> users = new HashMap<>();

    @Override
    public User addUser(User user) {
        //сначала нужно проверить
        checkEmailDuplicationWhenCreate(user);

        user.setId(++ID);

        users.put(user.getId(), user);

        return user;
    }

    @Override
    public User updateUser(User user) {
        checkEmailDuplicationWhenUpdate(user);

        users.put(user.getId(), user);

        return user;
    }

    @Override
    public void deleteUserById(Long userId) {
        User deletedUser = users.remove(userId);

        if (deletedUser == null) {
            throw new UserUnknownException(String.format("Пользователь с %d не найден.", userId));
        }
    }

    @Override
    public User getUserById(Long userId) {

        User foundedUser = users.get(userId);

        if (foundedUser == null) {
            throw new UserUnknownException(String.format("Пользователь с %d не найден.", userId));
        }

        return foundedUser;
    }

    @Override
    public List<User> getAllUsers() {
        return new ArrayList<>(users.values());
    }

    private void checkEmailDuplicationWhenCreate(User user) {
        for (User userMap: users.values()) {
            if (userMap.getEmail().equals(user.getEmail())) {
                throw new UserAlreadyExistEmailException(String.format("Пользователь с таким email = %s уже существует.", user.getEmail()));
            }
        }
    }

    private void checkEmailDuplicationWhenUpdate(User user) {
        for (User userMap: users.values()) {
            //сравнить также id, чтобы проверка не сработала по обновляемому пользователю на самого себя
            if (userMap.getEmail().equals(user.getEmail()) && userMap.getId().longValue() != user.getId().longValue()) {
                throw new UserAlreadyExistEmailException(String.format("Пользователь с таким email = %s уже существует.", user.getEmail()));
            }
        }
    }
}
