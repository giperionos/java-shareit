package ru.practicum.shareit.requests.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ItemRequestDto {

    private Long id;

    @NotBlank(message = "Не заполнено описание вещи.")
    private String description;

    @NotNull(message = "Не заполнено дата и время создания запроса на вещь.")
    @FutureOrPresent(message = "Дата и время создания запроса на вещь не должна быть в прошлом.")
    private LocalDateTime created;
}
