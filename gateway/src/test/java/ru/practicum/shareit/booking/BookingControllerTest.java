package ru.practicum.shareit.booking;

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
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookingController.class)
@AutoConfigureMockMvc
class BookingControllerTest {

    @Autowired
    ObjectMapper mapper;

    @MockBean
    BookingClient bookingClient;

    @Autowired
    MockMvc mockMvc;

    String headerName = "X-Sharer-User-Id";
    Long userId = 1L;

    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss");

    private UserDto userDto = new UserDto(1L, "User_name_1", "User1@email.ru");

    private BookingCreateDto bookingCreateDto = new BookingCreateDto(
            1L,
            LocalDateTime.parse(LocalDateTime.now().plusHours(1L).format(formatter), formatter),
            LocalDateTime.parse(LocalDateTime.now().plusDays(1L).format(formatter), formatter),
            1L,
            1L
    );

    private BookingCreateDto bookingCreateDtoWithStartBeforeEnd = new BookingCreateDto(
            2L,
            LocalDateTime.parse(LocalDateTime.now().plusDays(1L).format(formatter), formatter),
            LocalDateTime.parse(LocalDateTime.now().plusHours(1L).format(formatter), formatter),
            1L,
            1L
    );

    @Test
    void getBadResponsesCreateBooking() throws Exception {
        when(bookingClient.createBooking(any(BookingCreateDto.class), any(Long.class)))
                .thenReturn(new ResponseEntity<>(bookingCreateDto, HttpStatus.OK));

        mockMvc.perform(post("/bookings")
                        .content(mapper.writeValueAsString(bookingCreateDtoWithStartBeforeEnd))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(headerName, userId))
                .andExpect(status().isBadRequest());

        verify(bookingClient, times(0)).createBooking(any(BookingCreateDto.class), any(Long.class));
    }
}