package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.User;

import java.util.List;

public interface UserService {

    User createUser(User user);

    User updateUserById(Long userId, User user);

    User getUserById(Long userId);

    List<User> getAllUsers();

    void deleteUserById(Long userId);
}
