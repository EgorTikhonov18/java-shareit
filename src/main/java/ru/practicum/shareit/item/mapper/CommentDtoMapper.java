package ru.practicum.shareit.item.mapper;

import ru.practicum.shareit.item.CommentDto;
import ru.practicum.shareit.item.model.Comment;

public class CommentDtoMapper {
    public static CommentDto mapRow(Comment comment) {
        return CommentDto.builder()
                .id(comment.getId())
                .item(comment.getItem())
                .authorName(comment.getAuthor().getName())
                .created(comment.getCreateDate())
                .text(comment.getText())
                .build();
    }
}