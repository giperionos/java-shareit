package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ItemWithBookingsAndCommentsDto {
    private Long id;
    private String name;
    private String description;
    private Boolean available;
    private ItemBookingInfoDto lastBooking;
    private ItemBookingInfoDto nextBooking;
    private List<CommentDto> comments;
}


