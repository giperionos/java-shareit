package ru.practicum.shareit.item;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.requests.ItemRequest;
import ru.practicum.shareit.requests.ItemRequestRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class ItemRepositoryTest {

    @Autowired
    UserRepository userRepository;

    @Autowired
    ItemRepository itemRepository;

    @Autowired
    ItemRequestRepository itemRequestRepository;

    private User user1;
    private User user2;

    private Item item1;
    private Item item2;

    private ItemRequest itemRequest3;
    private Item item3;

    private PageRequest oneElementOnPageRequest = PageRequest.of(0, 1, Sort.by("id").ascending());
    private PageRequest tenElementOnPageRequest = PageRequest.of(0, 10, Sort.by("id").ascending());
    private String keyWordItem = "item";
    private String keyWordItem2 = "item_2";

    @BeforeEach
    void beforeEach() {
        user1 = new User();
        user1.setName("User_name_1");
        user1.setEmail("User1@email.ru");
        user1 = userRepository.save(user1);

        user2 = new User();
        user2.setName("User_name_2");
        user2.setEmail("User2@email.ru");
        user2 = userRepository.save(user2);

        item1 = new Item();
        item1.setName("item_1_name");
        item1.setDescription("item_1_desc");
        item1.setAvailable(Boolean.TRUE);
        item1.setOwner(user1);
        item1 = itemRepository.save(item1);

        item2 = new Item();
        item2.setName("item_2_name");
        item2.setDescription("item_2_desc");
        item2.setAvailable(Boolean.TRUE);
        item2.setOwner(user1);
        item2 = itemRepository.save(item2);

        itemRequest3 = new ItemRequest();
        itemRequest3.setDescription("request3_desc");
        itemRequest3.setRequester(user2);
        itemRequest3 = itemRequestRepository.save(itemRequest3);

        item3 = new Item();
        item3.setName("item_3_name");
        item3.setDescription("item_3_desc");
        item3.setAvailable(Boolean.TRUE);
        item3.setOwner(user1);
        item3.setRequest(itemRequest3);
        item3 = itemRepository.save(item3);
    }

    @AfterEach
    void afterEach() {
        itemRepository.deleteAll();
        itemRequestRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void getAllItemsForOwner1With1ElementOnPage() {
        List<Item> result = itemRepository.findItemsByOwnerId(user1.getId(), oneElementOnPageRequest);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(item1.getId(), result.get(0).getId());
        assertEquals(item1.getName(), result.get(0).getName());
        assertEquals(item1.getDescription(), result.get(0).getDescription());
        assertEquals(item1.getAvailable(), result.get(0).getAvailable());
        assertEquals(item1.getOwner().getId(), result.get(0).getOwner().getId());
        assertEquals(item1.getId(), result.get(0).getId());
    }

    @Test
    void getAllItemsForOwner1() {
        List<Item> result = itemRepository.findALlItemsByOwnerId(user1.getId());

        assertNotNull(result);
        assertEquals(3, result.size());

        assertEquals(item1.getId(), result.get(0).getId());
        assertEquals(item1.getName(), result.get(0).getName());
        assertEquals(item1.getDescription(), result.get(0).getDescription());
        assertEquals(item1.getAvailable(), result.get(0).getAvailable());
        assertEquals(item1.getOwner().getId(), result.get(0).getOwner().getId());
        assertEquals(item1.getId(), result.get(0).getId());

        assertEquals(item2.getId(), result.get(1).getId());
        assertEquals(item2.getName(), result.get(1).getName());
        assertEquals(item2.getDescription(), result.get(1).getDescription());
        assertEquals(item2.getAvailable(), result.get(1).getAvailable());
        assertEquals(item2.getOwner().getId(), result.get(1).getOwner().getId());
        assertEquals(item2.getId(), result.get(1).getId());

        assertEquals(item3.getId(), result.get(2).getId());
        assertEquals(item3.getName(), result.get(2).getName());
        assertEquals(item3.getDescription(), result.get(2).getDescription());
        assertEquals(item3.getAvailable(), result.get(2).getAvailable());
        assertEquals(item3.getOwner().getId(), result.get(2).getOwner().getId());
        assertEquals(item3.getId(), result.get(2).getId());
    }

    @Test
    void shouldGet3ItemsByKeyWordItem() {
        List<Item> result = itemRepository.findItemsByKeyWord(keyWordItem, tenElementOnPageRequest);

        assertNotNull(result);
        assertEquals(3, result.size());

        assertEquals(item1.getId(), result.get(0).getId());
        assertEquals(item1.getName(), result.get(0).getName());
        assertEquals(item1.getDescription(), result.get(0).getDescription());
        assertEquals(item1.getAvailable(), result.get(0).getAvailable());
        assertEquals(item1.getOwner().getId(), result.get(0).getOwner().getId());
        assertEquals(item1.getId(), result.get(0).getId());

        assertEquals(item2.getId(), result.get(1).getId());
        assertEquals(item2.getName(), result.get(1).getName());
        assertEquals(item2.getDescription(), result.get(1).getDescription());
        assertEquals(item2.getAvailable(), result.get(1).getAvailable());
        assertEquals(item2.getOwner().getId(), result.get(1).getOwner().getId());
        assertEquals(item2.getId(), result.get(1).getId());

        assertEquals(item3.getId(), result.get(2).getId());
        assertEquals(item3.getName(), result.get(2).getName());
        assertEquals(item3.getDescription(), result.get(2).getDescription());
        assertEquals(item3.getAvailable(), result.get(2).getAvailable());
        assertEquals(item3.getOwner().getId(), result.get(2).getOwner().getId());
        assertEquals(item3.getId(), result.get(2).getId());
    }

    @Test
    void shouldGetItem2ByKeyWordItem2() {
        List<Item> result = itemRepository.findItemsByKeyWord(keyWordItem2, tenElementOnPageRequest);

        assertNotNull(result);
        assertEquals(1, result.size());

        assertEquals(item2.getId(), result.get(0).getId());
        assertEquals(item2.getName(), result.get(0).getName());
        assertEquals(item2.getDescription(), result.get(0).getDescription());
        assertEquals(item2.getAvailable(), result.get(0).getAvailable());
        assertEquals(item2.getOwner().getId(), result.get(0).getOwner().getId());
        assertEquals(item2.getId(), result.get(0).getId());
    }

    @Test
    void shouldGetItem3ByItemRequest() {
        List<Item> result = itemRepository.findAllByRequest_Id(itemRequest3.getId());

        assertNotNull(result);
        assertEquals(1, result.size());

        assertEquals(item3.getId(), result.get(0).getId());
        assertEquals(item3.getName(), result.get(0).getName());
        assertEquals(item3.getDescription(), result.get(0).getDescription());
        assertEquals(item3.getAvailable(), result.get(0).getAvailable());
        assertEquals(item3.getOwner().getId(), result.get(0).getOwner().getId());
        assertEquals(item3.getId(), result.get(0).getId());
    }
}