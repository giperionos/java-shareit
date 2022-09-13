package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.exceptions.UserAlreadyExistEmailException;
import ru.practicum.shareit.user.exceptions.UserUnknownException;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private static final String CONSTRAINT_UNIQUE_EMAIL_NAME = "uq_user_email";

    @Override
    public UserDto createUser(UserDto userDto) {

        User user = UserMapper.toUser(userDto);

        try {
            return UserMapper.toUserDto(userRepository.save(user));
        } catch (Exception exception) {
            if (exception.getMessage().toLowerCase().contains(CONSTRAINT_UNIQUE_EMAIL_NAME)) {
                throw new UserAlreadyExistEmailException(String.format("Пользователь с таким email = %s уже существует.", user.getEmail()));
            } else {
                throw exception;
            }
        }
    }

    @Override
    public UserDto updateUserById(Long userId, UserDto userDto) {

        //получить пользователя из хранилища
        User userForUpdate = userRepository.findById(userId)
                .orElseThrow(() -> new UserUnknownException(String.format("Пользователь с %d не найден.", userId)));

        //обновить нужно только те поля, что пришли
        if (userDto.getName() != null) {
            userForUpdate.setName(userDto.getName());
        }

        if (userDto.getEmail() != null) {
            userForUpdate.setEmail(userDto.getEmail());
        }

        try {
            return  UserMapper.toUserDto(userRepository.save(userForUpdate));
        } catch (Exception exception) {
            if (exception.getMessage().toLowerCase().contains(CONSTRAINT_UNIQUE_EMAIL_NAME)) {
                throw new UserAlreadyExistEmailException(String.format("Пользователь с таким email = %s уже существует.", userDto.getEmail()));
            } else {
                throw exception;
            }
        }
    }

    @Override
    public UserDto getUserById(Long userId) {
        User foundedUser = userRepository.findById(userId)
                .orElseThrow(() -> new UserUnknownException(String.format("Пользователь с %d не найден.", userId)));

        return UserMapper.toUserDto(foundedUser);
    }

    @Override
    public List<UserDto> getAllUsers() {
        return userRepository.findAll().stream().map(UserMapper::toUserDto).collect(Collectors.toList());
    }

    @Override
    public void deleteUserById(Long userId) {
        User userForDelete = userRepository.findById(userId)
                .orElseThrow(() -> new UserUnknownException(String.format("Пользователь с %d не найден.", userId)));

        userRepository.delete(userForDelete);
    }
}
