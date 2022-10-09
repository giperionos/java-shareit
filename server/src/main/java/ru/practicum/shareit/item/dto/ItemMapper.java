package ru.practicum.shareit.item.dto;

import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.item.Comment;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.requests.ItemRequest;
import ru.practicum.shareit.user.User;

import java.util.List;
import java.util.stream.Collectors;

public class ItemMapper {

    public static ItemDto toItemDto(Item item) {
        return new ItemDto(
            item.getId(),
            item.getName(),
            item.getDescription(),
            item.getAvailable(),
            item.getRequest() == null ? null : item.getRequest().getId()
        );
    }

    public static Item toItem(ItemDto itemDto, User user, ItemRequest request) {
        return new Item(
            itemDto.getId(),
            itemDto.getName(),
            itemDto.getDescription(),
            itemDto.getAvailable(),
            user,
            request
        );
    }

    public static ItemWithBookingsAndCommentsDto toItemWithBookingsDto(Item item, Booking lastBooking, Booking nextBooking, List<Comment> comments) {
        return new ItemWithBookingsAndCommentsDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                lastBooking == null ? null : new ItemBookingInfoDto(
                        lastBooking.getId(),
                        lastBooking.getStart(),
                        lastBooking.getEnd(),
                        lastBooking.getBooker().getId()
                ),
                nextBooking == null ? null : new ItemBookingInfoDto(
                        nextBooking.getId(),
                        nextBooking.getStart(),
                        nextBooking.getEnd(),
                        nextBooking.getBooker().getId()
                ),
                comments.stream().map(CommentMapper::toCommentDto).collect(Collectors.toList())
        );
    }
}
