package ru.practicum.shareit.user.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.exceptions.UserAlreadyExistEmailException;
import ru.practicum.shareit.user.exceptions.UserUnknownException;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class UserServiceImplTest {

    UserService userService;

    UserRepository userRepository;

    private Long userId = 1L;
    private Long userId2 = 2L;
    private Long unknownUserId = 100L;
    private UserDto userDto;
    private UserDto userDto2;
    private User user;
    private User user2;

    @BeforeEach
    void beforeEach() {
        userRepository = mock(UserRepository.class);
        userService = new UserServiceImpl(userRepository);
        userDto = new UserDto(userId, "User_name_1", "User1@email.ru");
        user = UserMapper.toUser(userDto);
        userDto2 = new UserDto(userId2, "User_name_2", "User2@email.ru");
        user2 = UserMapper.toUser(userDto2);
    }

    @Test
    void testSuccessCreateUser() {
        when(userRepository.save(any(User.class)))
                .thenReturn(user);

        final UserDto userDto1 = userService.createUser(userDto);

        assertNotNull(userDto1);
        assertEquals(userDto.getName(), userDto1.getName());
        assertEquals(userDto.getEmail(), userDto1.getEmail());

        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void testGetExceptionCreateUserWithAlreadyExistEmail() {
        when(userRepository.save(any(User.class)))
                .thenThrow(new DataIntegrityViolationException("uq_user_email exception"));

        final UserAlreadyExistEmailException exception = assertThrows(
                UserAlreadyExistEmailException.class,
                () -> userService.createUser(userDto)
        );

        String expectedMessage = String.format("Пользователь с таким email = %s уже существует.", userDto.getEmail());
        assertEquals(expectedMessage, exception.getMessage());

        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void testSuccessUpdateNameAndEmailUserById() {
        when(userRepository.findById(userId))
                .thenReturn(Optional.of(user));

        String updatedName = "User Update Name";
        String updatedEmail = "User1_update@email.ru";

        userDto.setName(updatedName);
        userDto.setEmail(updatedEmail);

        when(userRepository.save(user))
                .thenReturn(user);

        final UserDto userDto1 = userService.updateUserById(userId, userDto);

        assertEquals(updatedName, userDto1.getName());
        assertEquals(updatedEmail, userDto1.getEmail());

        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void testSuccessUpdateOnlyNameUserById() {
        when(userRepository.findById(userId))
                .thenReturn(Optional.of(user));

        String updatedName = "User Update Name";
        final String previousEmail = userDto.getEmail();

        userDto.setName(updatedName);
        userDto.setEmail(null);

        when(userRepository.save(user))
                .thenReturn(user);

        final UserDto userDto1 = userService.updateUserById(userId, userDto);

        assertEquals(updatedName, userDto1.getName());
        assertEquals(previousEmail, userDto1.getEmail());

        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void testSuccessUpdateOnlyEmailUserById() {
        when(userRepository.findById(userId))
                .thenReturn(Optional.of(user));

        final String updatedEmail = "User1_update@email.ru";
        final String previousName = userDto.getName();

        userDto.setName(null);
        userDto.setEmail(updatedEmail);

        when(userRepository.save(user))
                .thenReturn(user);

        final UserDto userDto1 = userService.updateUserById(userId, userDto);

        assertEquals(previousName, userDto1.getName());
        assertEquals(updatedEmail, userDto1.getEmail());

        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void testGetExceptionUpdateUserByIdWithAlreadyExistEmail() {
        when(userRepository.findById(userId))
                .thenReturn(Optional.of(user));

        when(userRepository.save(user))
                .thenThrow(new DataIntegrityViolationException("uq_user_email exception"));

        final String expectedMessage = String.format("Пользователь с таким email = %s уже существует.", userDto.getEmail());
        final UserAlreadyExistEmailException exception = assertThrows(
                UserAlreadyExistEmailException.class,
                () -> userService.updateUserById(userId, userDto)
        );

        assertEquals(expectedMessage, exception.getMessage());

        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void testGetExceptionUpdateUserByUnknownId() {
        when(userRepository.findById(userId))
                .thenReturn(Optional.empty());

        when(userRepository.save(user))
                .thenReturn(user);

        final String expectedMessage = String.format("Пользователь с %d не найден.", userId);
        final UserUnknownException exception = assertThrows(
                UserUnknownException.class,
                () -> userService.updateUserById(userId, userDto)
        );

        assertEquals(expectedMessage, exception.getMessage());

        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, times(0)).save(user);
    }

    @Test
    void testSuccessGetUserById() {
        when(userRepository.findById(any(Long.class)))
                .thenReturn(Optional.of(user));

        final UserDto userDto1 = userService.getUserById(userId);

        assertNotNull(userDto1);
        assertEquals(userDto.getName(), userDto1.getName());
        assertEquals(userDto.getEmail(), userDto1.getEmail());

        verify(userRepository, times(1)).findById(any(Long.class));
    }

    @Test
    void testGetExceptionOnGetUserByUnknownId() {
        when(userRepository.findById(any(Long.class)))
                .thenReturn(Optional.empty());

        final UserUnknownException exception = assertThrows(
                UserUnknownException.class,
                () -> userService.getUserById(unknownUserId)
        );

        String expectedMessage = String.format("Пользователь с %d не найден.", unknownUserId);
        assertEquals(expectedMessage, exception.getMessage());

        verify(userRepository, times(1)).findById(any(Long.class));
    }

    @Test
    void testSuccessGetAllUsers() {
        when(userRepository.findAll())
                .thenReturn(List.of(user, user2));

        List<UserDto> resultList = userService.getAllUsers();

        assertEquals(2, resultList.size());
        assertEquals(userDto.getName(), resultList.get(0).getName());
        assertEquals(userDto.getEmail(), resultList.get(0).getEmail());

        assertEquals(userDto2.getName(), resultList.get(1).getName());
        assertEquals(userDto2.getEmail(), resultList.get(1).getEmail());

        verify(userRepository, times(1)).findAll();
    }

    @Test
    void testSuccessDeleteUserById() {
        when(userRepository.findById(any(Long.class)))
                .thenReturn(Optional.of(user));

        doNothing().when(userRepository).delete(any(User.class));

        assertDoesNotThrow(() -> userService.deleteUserById(unknownUserId));
        verify(userRepository, times(1)).findById(any(Long.class));
        verify(userRepository, times(1)).delete(any(User.class));
    }

    @Test
    void testGetExceptionOnDeleteUserByUnknownId() {
        when(userRepository.findById(any(Long.class)))
                .thenReturn(Optional.empty());

        final UserUnknownException exception = assertThrows(
                UserUnknownException.class,
                () -> userService.deleteUserById(unknownUserId)
        );

        String expectedMessage = String.format("Пользователь с %d не найден.", unknownUserId);
        assertEquals(expectedMessage, exception.getMessage());

        verify(userRepository, times(1)).findById(any(Long.class));
        verify(userRepository, times(0)).delete(any(User.class));
    }
}