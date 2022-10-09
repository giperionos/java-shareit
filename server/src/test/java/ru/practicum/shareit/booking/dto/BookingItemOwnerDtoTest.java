package ru.practicum.shareit.booking.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class BookingItemOwnerDtoTest {

    @Autowired
    private JacksonTester<BookingItemOwnerDto> json;

    private UserDto userDto = new UserDto(1L, "User_name_1", "User1@email.ru");

    private ItemDto itemDto = new ItemDto(
            1L,
            "item_name_1",
            "item_desc_1",
            Boolean.TRUE,
            1L
    );

    private BookingItemOwnerDto bookingItemOwnerDto = new BookingItemOwnerDto(
            1L,
            BookingStatus.WAITING,
            userDto,
            itemDto
    );

    @Test
    void testBookingItemOwnerDto() throws Exception {
        JsonContent<BookingItemOwnerDto> result = json.write(bookingItemOwnerDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.status").isEqualTo(bookingItemOwnerDto.getStatus().toString());
        assertThat(result).extractingJsonPathNumberValue("$.booker.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.booker.name").isEqualTo(userDto.getName());
        assertThat(result).extractingJsonPathStringValue("$.booker.email").isEqualTo(userDto.getEmail());
        assertThat(result).extractingJsonPathNumberValue("$.item.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.item.name").isEqualTo(itemDto.getName());
        assertThat(result).extractingJsonPathStringValue("$.item.description").isEqualTo(itemDto.getDescription());
        assertThat(result).extractingJsonPathBooleanValue("$.item.available").isEqualTo(itemDto.getAvailable());
        assertThat(result).extractingJsonPathNumberValue("$.item.requestId").isEqualTo(1);
    }
}