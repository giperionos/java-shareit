package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.shareit.common.Create;
import ru.practicum.shareit.common.Update;

import javax.validation.constraints.Future;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BookingCreateDto {

    private Long id;

    @NotNull(groups = {Create.class}, message = "Не указана дата и время начала брони.")
    @FutureOrPresent(groups = {Create.class, Update.class}, message = "Дата и время начала брони не должны быть в прошлом.")
    private LocalDateTime start;

    @NotNull(groups = {Create.class}, message = "Не указана дата и время окончания брони.")
    @Future(groups = {Create.class, Update.class}, message = "Дата и время окончания брони должны быть в будущем.")
    private LocalDateTime end;

    @NotNull(groups = {Create.class}, message = "Не указана вещь для брони.")
    private Long itemId;

    private Long userId;
}
