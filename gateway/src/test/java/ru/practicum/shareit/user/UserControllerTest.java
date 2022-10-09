package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.dto.UserDto;


import java.nio.charset.StandardCharsets;
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
    UserClient userClient;

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
        when(userClient.createUser(any(UserDto.class)))
                .thenReturn(new ResponseEntity<>(userDto, HttpStatus.OK));

        mockMvc.perform(post("/users")
                        .content(mapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userDto.getId()))
                .andExpect(jsonPath("$.name").value(userDto.getName()))
                .andExpect(jsonPath("$.email").value(userDto.getEmail()));

        verify(userClient, times(1)).createUser(any(UserDto.class));

    }

    @Test
    void getBadResponseWhenCreateUserWithInvalidUserName() throws Exception {
        when(userClient.createUser(any(UserDto.class)))
                .thenReturn(new ResponseEntity<>(badNameUserDto, HttpStatus.BAD_REQUEST));

        mockMvc.perform(post("/users")
                        .content(mapper.writeValueAsString(badNameUserDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(userClient, times(0)).createUser(any(UserDto.class));
    }

    @Test
    void getBadResponseWhenCreateUserWithInvalidEmail() throws Exception {
        when(userClient.createUser(any(UserDto.class)))
                .thenReturn(new ResponseEntity<>(badEmailUserDto, HttpStatus.BAD_REQUEST));

        mockMvc.perform(post("/users")
                        .content(mapper.writeValueAsString(badEmailUserDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(userClient, times(0)).createUser(any(UserDto.class));
    }

    @Test
    void getSuccessRsWhenUpdateUser() throws Exception {
        when(userClient.updateUserById(any(Long.class), any(UserDto.class)))
                .thenReturn(new ResponseEntity<>(userDto, HttpStatus.OK));

        mockMvc.perform(patch("/users/" + userDto.getId())
                        .content(mapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userDto.getId()))
                .andExpect(jsonPath("$.name").value(userDto.getName()))
                .andExpect(jsonPath("$.email").value(userDto.getEmail()));

        verify(userClient, times(1)).updateUserById(any(Long.class), any(UserDto.class));
    }

    @Test
    void getBadResponseWhenUpdateUserWithInvalidEmail() throws Exception {
        when(userClient.updateUserById(any(Long.class), any(UserDto.class)))
                .thenReturn(new ResponseEntity<>(badEmailUserDto, HttpStatus.BAD_REQUEST));

        mockMvc.perform(patch("/users/" + userDto.getId())
                        .content(mapper.writeValueAsString(badEmailUserDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(userClient, times(0)).updateUserById(any(Long.class), any(UserDto.class));
    }

    @Test
    void getSuccessRsOnGetUserById() throws Exception {
        when(userClient.getUserById(any(Long.class)))
                .thenReturn(new ResponseEntity<>(userDto, HttpStatus.OK));

        mockMvc.perform(get("/users/" + userDto.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userDto.getId()))
                .andExpect(jsonPath("$.name").value(userDto.getName()))
                .andExpect(jsonPath("$.email").value(userDto.getEmail()));

        verify(userClient, times(1)).getUserById(any(Long.class));
    }


    @Test
    void getSuccessEmptyRsOnGetAllUsers() throws Exception {
        when(userClient.getAllUsers())
                .thenReturn(new ResponseEntity<>(HttpStatus.OK));

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk());

        verify(userClient, times(1)).getAllUsers();
    }

    @Test
    void getSuccessRsOnGetAllUser() throws Exception {
        when(userClient.getAllUsers())
                .thenReturn(new ResponseEntity<>(List.of(userDto, userDto2), HttpStatus.OK));

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$.[0].id").value(userDto.getId()))
                .andExpect(jsonPath("$.[0].name").value(userDto.getName()))
                .andExpect(jsonPath("$.[0].email").value(userDto.getEmail()))
                .andExpect(jsonPath("$.[1].id").value(userDto2.getId()))
                .andExpect(jsonPath("$.[1].name").value(userDto2.getName()))
                .andExpect(jsonPath("$.[1].email").value(userDto2.getEmail()));

        verify(userClient, times(1)).getAllUsers();
    }

    @Test
    void getSuccessRsOnDeleteUserById() throws Exception {
        when(userClient.deleteUserById(any(Long.class)))
                .thenReturn(new ResponseEntity<>(HttpStatus.OK));

        mockMvc.perform(delete("/users/" + userDto.getId()))
                .andExpect(status().isOk());

        verify(userClient, times(1)).deleteUserById(any(Long.class));
    }
}