package ru.practicum.shareit.requests.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.shareit.item.dto.ItemDto;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ItemRequestWithItemInfoDto {

    private Long id;
    private String description;
    private LocalDateTime created;
    private List<ItemDto> items;
}
