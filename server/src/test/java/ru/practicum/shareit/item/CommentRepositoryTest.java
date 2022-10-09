package ru.practicum.shareit.item;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class CommentRepositoryTest {

    @Autowired
    UserRepository userRepository;

    @Autowired
    ItemRepository itemRepository;

    @Autowired
    CommentRepository commentRepository;

    private User user1;
    private Item item1;
    private Comment comment1;
    private Comment comment2;

    @BeforeEach
    void beforeEach() {
        user1 = new User();
        user1.setName("User_name_1");
        user1.setEmail("User1@email.ru");
        user1 = userRepository.save(user1);

        item1 = new Item();
        item1.setName("item_1_name");
        item1.setDescription("item_1_desc");
        item1.setAvailable(Boolean.TRUE);
        item1.setOwner(user1);
        item1 = itemRepository.save(item1);

        comment1 = new Comment();
        comment1.setText("comment_1_text");
        comment1.setItem(item1);
        comment1.setAuthor(user1);
        comment1.setCreated(LocalDateTime.now().minusMinutes(10L));
        comment1 = commentRepository.save(comment1);

        comment2 = new Comment();
        comment2.setText("comment_2_text");
        comment2.setItem(item1);
        comment2.setAuthor(user1);
        comment2.setCreated(LocalDateTime.now());
        comment2 = commentRepository.save(comment2);
    }

    @AfterEach
    void afterEach() {
        commentRepository.deleteAll();
        itemRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void getAllCommentsForItem1() {
        List<Comment> comments = commentRepository.findAllByItem_IdOrderByCreatedDesc(item1.getId());

        assertNotNull(comments);
        assertEquals(2, comments.size());

        assertEquals(comment2.getId(), comments.get(0).getId());
        assertEquals(comment2.getText(), comments.get(0).getText());
        assertEquals(comment2.getItem().getId(), comments.get(0).getItem().getId());
        assertEquals(comment2.getAuthor().getId(), comments.get(0).getAuthor().getId());

        assertEquals(comment1.getId(), comments.get(1).getId());
        assertEquals(comment1.getText(), comments.get(1).getText());
        assertEquals(comment1.getItem().getId(), comments.get(1).getItem().getId());
        assertEquals(comment1.getAuthor().getId(), comments.get(1).getAuthor().getId());
    }
}