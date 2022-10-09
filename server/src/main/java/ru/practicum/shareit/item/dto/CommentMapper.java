package ru.practicum.shareit.item.dto;

import ru.practicum.shareit.item.Comment;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.user.User;

public class CommentMapper {
    public static CommentDto toCommentDto(Comment comment) {
        return new CommentDto(
            comment.getId(),
            comment.getText(),
            comment.getAuthor().getName(),
            comment.getCreated()
        );
    }

    public static Comment toComment(Item item, CommentDto commentDto, User user) {
        return new Comment(
            commentDto.getId(),
            commentDto.getText(),
            item,
            user,
            commentDto.getCreated()
        );
    }
}
