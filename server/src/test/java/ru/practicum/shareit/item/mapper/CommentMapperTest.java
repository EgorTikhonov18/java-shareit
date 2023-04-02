package ru.practicum.shareit.item.mapper;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.TestHelper;
import ru.practicum.shareit.item.CommentDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.user.User;

import static org.junit.jupiter.api.Assertions.*;

public class CommentMapperTest {
    final TestHelper testHelper = new TestHelper();

    @Test
    public void toCommentDtoTest() {
        User author = testHelper.getAuthor();
        Comment comment = testHelper.getComment();
        comment.setAuthor(author);

        CommentDto commentDto = CommentDtoMapper.mapRow(comment);

        assertEquals(1L, commentDto.getId());
        Assertions.assertNull(commentDto.getItem());
        assertEquals("author", commentDto.getAuthorName());
        Assertions.assertNotNull(commentDto.getCreated());
        assertEquals("commentText", commentDto.getText());
    }
}