package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingFullInfoDto;
import ru.practicum.shareit.booking.dto.BookingItemOwnerDto;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookingController.class)
@AutoConfigureMockMvc
class BookingControllerTest {

    @Autowired
    ObjectMapper mapper;

    @MockBean
    BookingService bookingService;

    @Autowired
    MockMvc mockMvc;

    String headerName = "X-Sharer-User-Id";
    Long userId = 1L;
    Long unknownUserId = 99L;

    String paramFromName = "from";
    String paramFromValue = "0";

    String paramSizeName = "size";
    String paramSizeValue = "2";

    String paramApprovedName = "approved";
    String paramApprovedValue = Boolean.TRUE.toString();

    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss");

    Boolean available = Boolean.TRUE;

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

    private ItemDto itemDto = new ItemDto(
            1L,
            "Item_Name_1",
            "Item_Desc_1",
            available,
            1L
    );

    private BookingItemOwnerDto bookingItemOwnerDto = new BookingItemOwnerDto(
            1L,
            BookingStatus.WAITING,
            userDto,
            itemDto
    );

    private BookingFullInfoDto bookingFullInfoDto = new BookingFullInfoDto(
            1L,
            LocalDateTime.parse(LocalDateTime.now().plusHours(1L).format(formatter), formatter),
            LocalDateTime.parse(LocalDateTime.now().plusDays(1L).format(formatter), formatter),
            BookingStatus.WAITING,
            userDto,
            itemDto
    );


    @Test
    void getSuccessCreateBooking() throws Exception {
        when(bookingService.createBooking(any(BookingCreateDto.class), any(Long.class)))
                .thenReturn(bookingCreateDto);

        mockMvc.perform(post("/bookings")
                        .content(mapper.writeValueAsString(bookingCreateDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(headerName, userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(bookingCreateDto.getId()))
                .andExpect(jsonPath("$.start").value(bookingCreateDto.getStart().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)))
                .andExpect(jsonPath("$.end").value(bookingCreateDto.getEnd().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)))
                .andExpect(jsonPath("$.itemId").value(bookingCreateDto.getItemId()))
                .andExpect(jsonPath("$.userId").value(bookingCreateDto.getUserId()));

        verify(bookingService, times(1)).createBooking(any(BookingCreateDto.class), any(Long.class));
    }

    @Test
    void getSuccessResolveBooking() throws Exception {
        when(bookingService.resolveBooking(any(Long.class), any(Long.class), any(Boolean.class)))
                .thenReturn(bookingItemOwnerDto);

        mockMvc.perform(patch("/bookings/" + bookingItemOwnerDto.getId())
                        .header(headerName, userId)
                        .param(paramApprovedName, paramApprovedValue))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(bookingItemOwnerDto.getId()))
                .andExpect(jsonPath("$.status").value(bookingItemOwnerDto.getStatus().toString()))
                .andExpect(jsonPath("$.booker.id").value(userDto.getId()))
                .andExpect(jsonPath("$.booker.name").value(userDto.getName()))
                .andExpect(jsonPath("$.booker.email").value(userDto.getEmail()))
                .andExpect(jsonPath("$.item.id").value(itemDto.getId()))
                .andExpect(jsonPath("$.item.name").value(itemDto.getName()))
                .andExpect(jsonPath("$.item.description").value(itemDto.getDescription()))
                .andExpect(jsonPath("$.item.available").value(itemDto.getAvailable()))
                .andExpect(jsonPath("$.item.requestId").value(itemDto.getRequestId()));

        verify(bookingService, times(1)).resolveBooking(any(Long.class), any(Long.class), any(Boolean.class));
    }

    @Test
    void getSuccessBookingDetailInfoById() throws Exception {
        when(bookingService.getBookingDetailInfoById(any(Long.class), any(Long.class)))
                .thenReturn(bookingFullInfoDto);

        mockMvc.perform(get("/bookings/" + bookingFullInfoDto.getId())
                        .accept(MediaType.APPLICATION_JSON)
                        .header(headerName, userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(bookingItemOwnerDto.getId()))
                .andExpect(jsonPath("$.start").value(bookingCreateDto.getStart().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)))
                .andExpect(jsonPath("$.end").value(bookingCreateDto.getEnd().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)))
                .andExpect(jsonPath("$.status").value(bookingItemOwnerDto.getStatus().toString()))
                .andExpect(jsonPath("$.booker.id").value(userDto.getId()))
                .andExpect(jsonPath("$.booker.name").value(userDto.getName()))
                .andExpect(jsonPath("$.booker.email").value(userDto.getEmail()))
                .andExpect(jsonPath("$.item.id").value(itemDto.getId()))
                .andExpect(jsonPath("$.item.name").value(itemDto.getName()))
                .andExpect(jsonPath("$.item.description").value(itemDto.getDescription()))
                .andExpect(jsonPath("$.item.available").value(itemDto.getAvailable()))
                .andExpect(jsonPath("$.item.requestId").value(itemDto.getRequestId()));

        verify(bookingService, times(1)).getBookingDetailInfoById(any(Long.class), any(Long.class));
    }

    @Test
    void getSuccessAllBookingsByUserIdAndState() throws Exception {
        when(bookingService.getAllBookingsByUserIdAndState(any(Long.class), any(String.class), any(Integer.class), any(Integer.class)))
                .thenReturn(List.of(bookingFullInfoDto));

        mockMvc.perform(get("/bookings")
                        .accept(MediaType.APPLICATION_JSON)
                        .header(headerName, userId)
                        .param(paramFromName, paramFromValue)
                        .param(paramSizeName, paramSizeValue))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$.[0].id").value(bookingItemOwnerDto.getId()))
                .andExpect(jsonPath("$.[0].start").value(bookingCreateDto.getStart().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)))
                .andExpect(jsonPath("$.[0].end").value(bookingCreateDto.getEnd().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)))
                .andExpect(jsonPath("$.[0].status").value(bookingItemOwnerDto.getStatus().toString()))
                .andExpect(jsonPath("$.[0].booker.id").value(userDto.getId()))
                .andExpect(jsonPath("$.[0].booker.name").value(userDto.getName()))
                .andExpect(jsonPath("$.[0].booker.email").value(userDto.getEmail()))
                .andExpect(jsonPath("$.[0].item.id").value(itemDto.getId()))
                .andExpect(jsonPath("$.[0].item.name").value(itemDto.getName()))
                .andExpect(jsonPath("$.[0].item.description").value(itemDto.getDescription()))
                .andExpect(jsonPath("$.[0].item.available").value(itemDto.getAvailable()))
                .andExpect(jsonPath("$.[0].item.requestId").value(itemDto.getRequestId()));

        verify(bookingService, times(1)).getAllBookingsByUserIdAndState(any(Long.class), any(String.class), any(Integer.class), any(Integer.class));
    }

    @Test
    void getSuccessAllBookingsByOwnerIdAndState() throws Exception {
        when(bookingService.getAllBookingsByOwnerIdAndState(any(Long.class), any(String.class), any(Integer.class), any(Integer.class)))
                .thenReturn(List.of(bookingFullInfoDto));

        mockMvc.perform(get("/bookings/owner")
                        .accept(MediaType.APPLICATION_JSON)
                        .header(headerName, userId)
                        .param(paramFromName, paramFromValue)
                        .param(paramSizeName, paramSizeValue))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$.[0].id").value(bookingItemOwnerDto.getId()))
                .andExpect(jsonPath("$.[0].start").value(bookingCreateDto.getStart().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)))
                .andExpect(jsonPath("$.[0].end").value(bookingCreateDto.getEnd().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)))
                .andExpect(jsonPath("$.[0].status").value(bookingItemOwnerDto.getStatus().toString()))
                .andExpect(jsonPath("$.[0].booker.id").value(userDto.getId()))
                .andExpect(jsonPath("$.[0].booker.name").value(userDto.getName()))
                .andExpect(jsonPath("$.[0].booker.email").value(userDto.getEmail()))
                .andExpect(jsonPath("$.[0].item.id").value(itemDto.getId()))
                .andExpect(jsonPath("$.[0].item.name").value(itemDto.getName()))
                .andExpect(jsonPath("$.[0].item.description").value(itemDto.getDescription()))
                .andExpect(jsonPath("$.[0].item.available").value(itemDto.getAvailable()))
                .andExpect(jsonPath("$.[0].item.requestId").value(itemDto.getRequestId()));

        verify(bookingService, times(1)).getAllBookingsByOwnerIdAndState(any(Long.class), any(String.class), any(Integer.class), any(Integer.class));
    }
}