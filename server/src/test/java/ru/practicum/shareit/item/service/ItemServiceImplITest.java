package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingItemOwnerDto;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.Comment;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.requests.dto.ItemRequestDto;
import ru.practicum.shareit.requests.service.ItemRequestService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import java.time.LocalDateTime;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

@Transactional
@SpringBootTest(
        properties = "spring.profiles.active=test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemServiceImplITest {

    private final EntityManager em;
    private final UserService userService;
    private final ItemService itemService;
    private final ItemRequestService itemRequestService;
    private final BookingService bookingService;

    private UserDto userDto1;
    private UserDto userDto2;

    private ItemDto item1Dto;
    private ItemDto item2Dto;

    @BeforeEach
    void beforeEach() {
        userDto1 = new UserDto();
        userDto1.setName("User_name_1");
        userDto1.setEmail("User1@email.ru");

        userDto2 = new UserDto();
        userDto2.setName("User_name_2");
        userDto2.setEmail("User2@email.ru");

        item1Dto = new ItemDto();
        item1Dto.setName("item_name_1");
        item1Dto.setDescription("item_desc_1");
        item1Dto.setAvailable(Boolean.TRUE);

        item2Dto = new ItemDto();
        item2Dto.setName("item_name_2");
        item2Dto.setDescription("item_desc_2");
        item2Dto.setAvailable(Boolean.FALSE);
    }

    @Test
    void createItem() {
        final UserDto createdUserDto1 = userService.createUser(userDto1);
        final ItemDto createdItemDto1 = itemService.createItem(item1Dto, createdUserDto1.getId());

        TypedQuery<Item> query = em.createQuery("Select i from Item i where i.id = :id", Item.class);
        Item savedItemFromDb = query.setParameter("id", createdItemDto1.getId()).getSingleResult();

        assertThat(savedItemFromDb.getId(), notNullValue());
        assertEquals(savedItemFromDb.getId().longValue(), createdItemDto1.getId().longValue());
        assertThat(savedItemFromDb.getName(), equalTo(item1Dto.getName()));
        assertThat(savedItemFromDb.getDescription(), equalTo(item1Dto.getDescription()));
        assertThat(savedItemFromDb.getAvailable(), equalTo(item1Dto.getAvailable()));
        assertThat(savedItemFromDb.getOwner().getId(), equalTo(createdUserDto1.getId()));
        assertThat(savedItemFromDb.getRequest(), nullValue());
    }

    @Test
    void createItemOnRequest() {
        final UserDto createdUserDto1 = userService.createUser(userDto1);
        final UserDto createdUserDto2 = userService.createUser(userDto2);

        ItemRequestDto itemRequestDto = new ItemRequestDto();
        itemRequestDto.setDescription("itemRequest_description");
        final ItemRequestDto createdRequestDto = itemRequestService.createItemRequest(
                createdUserDto2.getId(), itemRequestDto);

        item1Dto.setRequestId(createdRequestDto.getId());
        final ItemDto createdItemDto1 = itemService.createItem(item1Dto, createdUserDto1.getId());

        TypedQuery<Item> query = em.createQuery("Select i from Item i where i.id = :id", Item.class);
        Item savedItemFromDb = query.setParameter("id", createdItemDto1.getId()).getSingleResult();

        assertThat(savedItemFromDb.getId(), notNullValue());
        assertEquals(savedItemFromDb.getId().longValue(), createdItemDto1.getId().longValue());
        assertThat(savedItemFromDb.getName(), equalTo(item1Dto.getName()));
        assertThat(savedItemFromDb.getDescription(), equalTo(item1Dto.getDescription()));
        assertThat(savedItemFromDb.getAvailable(), equalTo(item1Dto.getAvailable()));
        assertThat(savedItemFromDb.getOwner().getId(), equalTo(createdUserDto1.getId()));
        assertThat(savedItemFromDb.getRequest().getId(), equalTo(item1Dto.getRequestId()));
    }

    @Test
    void updateItem() {
        final UserDto createdUserDto1 = userService.createUser(userDto1);
        final ItemDto createdItemDto1 = itemService.createItem(item1Dto, createdUserDto1.getId());

        ItemDto newItemDto = new ItemDto();
        newItemDto.setDescription("item_desc_1_update");
        newItemDto.setAvailable(Boolean.FALSE);

        final ItemDto updatedItemDto1 = itemService.updateItem(createdItemDto1.getId(), newItemDto, createdUserDto1.getId());

        TypedQuery<Item> query = em.createQuery("Select i from Item i where i.id = :id", Item.class);
        Item savedItemFromDb = query.setParameter("id", createdItemDto1.getId()).getSingleResult();

        assertNotNull(updatedItemDto1);

        //проверить, что поля изменились
        assertEquals(newItemDto.getDescription(), updatedItemDto1.getDescription());
        assertEquals(newItemDto.getAvailable(), updatedItemDto1.getAvailable());

        //проверить, что поля, которые не менялись не изменились
        assertEquals(item1Dto.getName(), updatedItemDto1.getName());

        assertThat(savedItemFromDb.getId(), notNullValue());
        assertEquals(savedItemFromDb.getId().longValue(), updatedItemDto1.getId().longValue());
        assertThat(savedItemFromDb.getName(), equalTo(updatedItemDto1.getName()));
        assertThat(savedItemFromDb.getDescription(), equalTo(updatedItemDto1.getDescription()));
        assertThat(savedItemFromDb.getAvailable(), equalTo(updatedItemDto1.getAvailable()));
        assertThat(savedItemFromDb.getOwner().getId(), equalTo(createdUserDto1.getId()));
    }

    @Test
    void addNewComment() {
        final UserDto createdUserDto1 = userService.createUser(userDto1);
        final UserDto createdUserDto2 = userService.createUser(userDto2);

        final ItemDto createdItemDto1 = itemService.createItem(item1Dto, createdUserDto1.getId());

        //для оставления комментария нужно, чтобы пользователь брал эту вещь в аренду ранее
        //создать запрос на аренду
        BookingCreateDto bookingCreateDto = new BookingCreateDto();
        bookingCreateDto.setStart(LocalDateTime.now().minusDays(3));
        bookingCreateDto.setEnd(LocalDateTime.now().minusDays(1));
        bookingCreateDto.setItemId(createdItemDto1.getId());

        BookingCreateDto createdBookingForUser2Dto =  bookingService.createBooking(bookingCreateDto, createdUserDto2.getId());

        //далее владелец вещи должен дать "добро"
        BookingItemOwnerDto bookingItemOwnerDto = bookingService.resolveBooking(createdBookingForUser2Dto.getId(),
                createdUserDto1.getId(), Boolean.TRUE);

        //теперь можно дать комментарий
        CommentDto commentDto = new CommentDto();
        commentDto.setText("commentDto_1_by_user_2");
        commentDto.setAuthorName(createdUserDto2.getName());

        final CommentDto addedCommentDto = itemService.addNewCommentByItemId(createdItemDto1.getId(), commentDto, createdUserDto2.getId());

        TypedQuery<Comment> query = em.createQuery("Select c from Comment c where c.id = :id", Comment.class);
        Comment savedCommentFromDb = query.setParameter("id", addedCommentDto.getId()).getSingleResult();

        assertThat(savedCommentFromDb.getId(), notNullValue());
        assertThat(savedCommentFromDb.getId(), equalTo(addedCommentDto.getId()));
        assertThat(savedCommentFromDb.getText(), equalTo(addedCommentDto.getText()));
        assertThat(createdItemDto1.getId(), equalTo(savedCommentFromDb.getItem().getId()));
        assertThat(createdUserDto2.getId(), equalTo(savedCommentFromDb.getAuthor().getId()));
    }
}