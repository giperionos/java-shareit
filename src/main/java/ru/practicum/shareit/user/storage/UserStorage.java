package ru.practicum.shareit.user.storage;

import ru.practicum.shareit.user.User;

import java.util.List;

public interface UserStorage {

    User addUser(User user);

    User updateUser(User user);

    void deleteUserById(Long userId);

    User getUserById(Long userId);

    List<User> getAllUsers();
}
