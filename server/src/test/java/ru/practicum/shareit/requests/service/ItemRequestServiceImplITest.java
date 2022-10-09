package ru.practicum.shareit.requests.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.requests.ItemRequest;
import ru.practicum.shareit.requests.dto.ItemRequestDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertEquals;

@Transactional
@SpringBootTest(
        properties = "spring.profiles.active=test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemRequestServiceImplITest {

    private final EntityManager em;
    private final ItemRequestService itemRequestService;
    private final UserService userService;

    private UserDto userDto;
    private ItemRequestDto itemRequestDto;

    @BeforeEach
    void beforeEach() {
        userDto = new UserDto();
        userDto.setName("User_name_1");
        userDto.setEmail("User1@email.ru");

        itemRequestDto = new ItemRequestDto();
        itemRequestDto.setDescription("itemRequest_description");
    }

    @Test
    void createItemRequest() {
        final UserDto createdUserDto = userService.createUser(userDto);
        final ItemRequestDto createdRequestDto = itemRequestService.createItemRequest(createdUserDto.getId(), itemRequestDto);

        TypedQuery<ItemRequest> query = em.createQuery("Select r from ItemRequest r where r.id = :id", ItemRequest.class);
        ItemRequest request = query.setParameter("id", createdRequestDto.getId()).getSingleResult();

        assertThat(createdRequestDto.getId(), notNullValue());
        assertThat(createdRequestDto.getCreated(), notNullValue());
        assertThat(createdRequestDto.getDescription(), equalTo(itemRequestDto.getDescription()));

        assertEquals(createdUserDto.getId().longValue(), request.getRequester().getId().longValue());
    }
}