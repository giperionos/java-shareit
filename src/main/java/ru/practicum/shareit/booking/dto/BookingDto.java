package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import javax.validation.constraints.Future;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BookingDto {

    private Long id;

    @NotNull(message = "Не указана дата и время начала брони.")
    @FutureOrPresent(message = "Дата и время начала брони не должны быть в прошлом.")
    private LocalDateTime start;

    @NotNull(message = "Не указана дата и время окончания брони.")
    @Future(message = "Дата и время окончания брони должны быть в будущем.")
    private LocalDateTime end;

    @NotNull(message = "Не указана вещь для брони.")
    private ItemDto item;

    @NotNull(message = "Не указан пользователь, осуществляющий бронирование.")
    private UserDto booker;

    @NotNull(message = "Не указан статус брони.")
    private BookingStatus status;
}
