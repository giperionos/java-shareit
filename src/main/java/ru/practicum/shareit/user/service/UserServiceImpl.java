package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserStorage userStorage;

    @Override
    public User createUser(User user) {
        return userStorage.addUser(user);
    }

    @Override
    public User updateUserById(Long userId, User user) {
        //получить из памяти пользователя
        User foundedUser = userStorage.getUserById(userId);

        //создать новый объект пользователя, как копию предыдущего,
        //чтобы не менять поля того объекта, что уже в мапе
        User userForUpdate = new User(foundedUser.getId(), foundedUser.getName(), foundedUser.getEmail());

        //обновить нужно только те поля, что пришли
        if (user.getName() != null) {
            userForUpdate.setName(user.getName());
        }

        if (user.getEmail() != null) {
            userForUpdate.setEmail(user.getEmail());
        }

        return userStorage.updateUser(userForUpdate);
    }

    @Override
    public User getUserById(Long userId) {
        return userStorage.getUserById(userId);
    }

    @Override
    public List<User> getAllUsers() {
        return userStorage.getAllUsers();
    }

    @Override
    public void deleteUserById(Long userId) {
        userStorage.deleteUserById(userId);
    }
}
