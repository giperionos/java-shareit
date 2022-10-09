package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UserDto;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

@Transactional
@SpringBootTest(
        properties = "spring.profiles.active=test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class UserServiceImplITest {

    private final EntityManager em;
    private final UserService userService;

    private UserDto userDto;

    @BeforeEach
    void beforeEach() {
        userDto = new UserDto();
        userDto.setName("User_name_1");
        userDto.setEmail("User1@email.ru");
    }

    @Test
    void createUser() {
        final UserDto createdUserDto = userService.createUser(userDto);

        TypedQuery<User> query = em.createQuery("Select u from User u where u.email = :email", User.class);
        User user = query.setParameter("email", userDto.getEmail()).getSingleResult();

        assertThat(user.getId(), notNullValue());
        assertThat(user.getId(), equalTo(createdUserDto.getId()));
        assertThat(user.getName(), equalTo(createdUserDto.getName()));
        assertThat(user.getEmail(), equalTo(createdUserDto.getEmail()));
    }

    @Test
    void updateUserById() {
        final UserDto createdUserDto = userService.createUser(userDto);

        userDto.setEmail("User1_update@email.ru");

        final UserDto updatedUserDto = userService.updateUserById(createdUserDto.getId(), userDto);

        assertThat(updatedUserDto.getName(), equalTo(userDto.getName()));
        assertThat(updatedUserDto.getEmail(), equalTo(userDto.getEmail()));
        assertNotEquals(updatedUserDto.getEmail(), createdUserDto.getEmail());
    }
}