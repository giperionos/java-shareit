package ru.practicum.shareit.requests;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class ItemRequestRepositoryTest {

    @Autowired
    ItemRequestRepository itemRequestRepository;

    @Autowired
    UserRepository userRepository;

    private User user1;
    private User user2;

    private ItemRequest request1;
    private ItemRequest request2;
    private ItemRequest request3;

    @BeforeEach
    void beforeEach() {
        user1 = new User();
        user1.setName("User_name_1");
        user1.setEmail("User1@email.ru");

        user2 = new User();
        user2.setName("User_name_2");
        user2.setEmail("User2@email.ru");

        user1 = userRepository.save(user1);
        user2 = userRepository.save(user2);

        request1 = new ItemRequest();
        request1.setDescription("request1_desc");
        request1.setRequester(user1);

        request2 = new ItemRequest();
        request2.setDescription("request2_desc");
        request2.setRequester(user1);

        request3 = new ItemRequest();
        request3.setDescription("request3_desc");
        request3.setRequester(user2);

        request1 = itemRequestRepository.save(request1);
        request2 = itemRequestRepository.save(request2);
        request3 = itemRequestRepository.save(request3);
    }

    @AfterEach
    void afterEach() {
        itemRequestRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void getAllRequestsForUser1() {
        List<ItemRequest> result = itemRequestRepository.getAllByRequester_IdOrderByCreatedDesc(user1.getId());

        assertNotNull(result);
        assertEquals(2, result.size());

        assertEquals(request2.getId(), result.get(0).getId());
        assertEquals(request2.getDescription(), result.get(0).getDescription());
        assertEquals(request2.getRequester().getId(), result.get(0).getRequester().getId());

        assertEquals(request1.getId(), result.get(1).getId());
        assertEquals(request1.getDescription(), result.get(1).getDescription());
        assertEquals(request1.getRequester().getId(), result.get(1).getRequester().getId());
    }

    @Test
    void getAllRequestsForUser2() {
        List<ItemRequest> result = itemRequestRepository.getAllByRequester_IdOrderByCreatedDesc(user2.getId());

        assertNotNull(result);
        assertEquals(1, result.size());

        assertEquals(request3.getId(), result.get(0).getId());
        assertEquals(request3.getDescription(), result.get(0).getDescription());
        assertEquals(request3.getRequester().getId(), result.get(0).getRequester().getId());
    }
}