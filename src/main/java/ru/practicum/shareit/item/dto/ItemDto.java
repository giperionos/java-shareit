package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.shareit.common.Create;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ItemDto {

    private Long id;

    @NotBlank(groups = Create.class, message = "Не указано название вещи.")
    private String name;

    @NotBlank(groups = Create.class, message = "Не указано описание вещи.")
    private String description;

    @NotNull(groups = Create.class, message = "Не указана доступность вещи.")
    private Boolean available;

    private Long requestId;
}
