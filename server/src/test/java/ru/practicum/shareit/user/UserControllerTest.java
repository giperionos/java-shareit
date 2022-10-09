package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.exceptions.UserAlreadyExistEmailException;
import ru.practicum.shareit.user.exceptions.UserUnknownException;
import ru.practicum.shareit.user.service.UserService;

import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc
class UserControllerTest {

    @Autowired
    ObjectMapper mapper;

    @MockBean
    UserService userService;

    @Autowired
    MockMvc mockMvc;

    private String emptyName = "";
    private String badEmail = "test.ru";

    private UserDto userDto = new UserDto(1L, "User_name_1", "User1@email.ru");
    private UserDto userDto2 = new UserDto(2L, "User_name_2", "User2@email.ru");
    private UserDto badNameUserDto = new UserDto(1L, emptyName, "User1@email.ru");
    private UserDto badEmailUserDto = new UserDto(1L, "User_name_1", badEmail);

    @Test
    void getSuccessCreateUser() throws Exception {
        when(userService.createUser(any(UserDto.class)))
                .thenReturn(userDto);

        mockMvc.perform(post("/users")
                    .content(mapper.writeValueAsString(userDto))
                    .characterEncoding(StandardCharsets.UTF_8)
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userDto.getId()))
                .andExpect(jsonPath("$.name").value(userDto.getName()))
                .andExpect(jsonPath("$.email").value(userDto.getEmail()));

        verify(userService, times(1)).createUser(any(UserDto.class));

    }

    @Test
    void getBadResponseWhenCreateUserWithDuplicateEmail() throws Exception {
        when(userService.createUser(any(UserDto.class)))
                .thenThrow(new UserAlreadyExistEmailException("Пользователь с таким email уже существует."));

        mockMvc.perform(post("/users")
                        .content(mapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict());

        verify(userService, times(1)).createUser(any(UserDto.class));
    }

    @Test
    void getSuccessRsWhenUpdateUser() throws Exception {
        when(userService.updateUserById(any(Long.class), any(UserDto.class)))
                .thenReturn(userDto);

        mockMvc.perform(patch("/users/" + userDto.getId())
                        .content(mapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userDto.getId()))
                .andExpect(jsonPath("$.name").value(userDto.getName()))
                .andExpect(jsonPath("$.email").value(userDto.getEmail()));

        verify(userService, times(1)).updateUserById(any(Long.class), any(UserDto.class));
    }

    @Test
    void getSuccessRsOnGetUserById() throws Exception {
        when(userService.getUserById(any(Long.class)))
                .thenReturn(userDto);

        mockMvc.perform(get("/users/" + userDto.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userDto.getId()))
                .andExpect(jsonPath("$.name").value(userDto.getName()))
                .andExpect(jsonPath("$.email").value(userDto.getEmail()));

        verify(userService, times(1)).getUserById(any(Long.class));
    }

    @Test
    void getBadRsWhenGetUserByUnknownId() throws Exception {
        when(userService.getUserById(any(Long.class)))
                .thenThrow(new UserUnknownException("Пользователь не найден."));

        mockMvc.perform(get("/users/" + userDto.getId()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Пользователь не найден."));

        verify(userService, times(1)).getUserById(any(Long.class));
    }

    @Test
    void getSuccessEmptyRsOnGetAllUsers() throws Exception {
        when(userService.getAllUsers())
                .thenReturn(Collections.emptyList());

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));

        verify(userService, times(1)).getAllUsers();
    }

    @Test
    void getSuccessRsOnGetAllUser() throws Exception {
        when(userService.getAllUsers())
                .thenReturn(List.of(userDto, userDto2));

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$.[0].id").value(userDto.getId()))
                .andExpect(jsonPath("$.[0].name").value(userDto.getName()))
                .andExpect(jsonPath("$.[0].email").value(userDto.getEmail()))
                .andExpect(jsonPath("$.[1].id").value(userDto2.getId()))
                .andExpect(jsonPath("$.[1].name").value(userDto2.getName()))
                .andExpect(jsonPath("$.[1].email").value(userDto2.getEmail()));

        verify(userService, times(1)).getAllUsers();
    }

    @Test
    void getSuccessRsOnDeleteUserById() throws Exception {
        Mockito.doNothing().when(userService).deleteUserById(any(Long.class));

        mockMvc.perform(delete("/users/" + userDto.getId()))
                .andExpect(status().isOk());

        verify(userService, times(1)).deleteUserById(any(Long.class));
    }

    @Test
    void getBadRsWhenDeleteUserByUnknownId() throws Exception {
        Mockito.doThrow(new UserUnknownException("Пользователь не найден."))
                .when(userService).deleteUserById(any(Long.class));

        mockMvc.perform(delete("/users/" + userDto.getId()))
                .andExpect(status().isNotFound());

        verify(userService, times(1)).deleteUserById(any(Long.class));
    }
}