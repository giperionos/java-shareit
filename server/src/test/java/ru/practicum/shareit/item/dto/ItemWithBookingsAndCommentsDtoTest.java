package ru.practicum.shareit.item.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class ItemWithBookingsAndCommentsDtoTest {

    @Autowired
    private JacksonTester<ItemWithBookingsAndCommentsDto> json;

    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss");

    private ItemBookingInfoDto lastBooking = new ItemBookingInfoDto(
            1L,
            LocalDateTime.parse(LocalDateTime.now().minusHours(10L).format(formatter), formatter),
            LocalDateTime.parse(LocalDateTime.now().minusHours(1L).format(formatter), formatter),
            1L
    );

    private ItemBookingInfoDto nextBooking = new ItemBookingInfoDto(
            2L,
            LocalDateTime.parse(LocalDateTime.now().plusHours(1L).format(formatter), formatter),
            LocalDateTime.parse(LocalDateTime.now().plusHours(10L).format(formatter), formatter),
            2L
    );

    private CommentDto commentDto = new CommentDto(
            1L,
            "comment_text",
            "authorName",
            LocalDateTime.parse(LocalDateTime.now().format(formatter), formatter)
    );

    private ItemWithBookingsAndCommentsDto itemWithBookingsAndCommentsDto = new ItemWithBookingsAndCommentsDto(
            1L,
            "name",
            "description",
            Boolean.TRUE,
            lastBooking,
            nextBooking,
            List.of(commentDto)
    );

    @Test
    void testItemWithBookingsAndCommentsDto() throws Exception {
        JsonContent<ItemWithBookingsAndCommentsDto> result = json.write(itemWithBookingsAndCommentsDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo(itemWithBookingsAndCommentsDto.getName());
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo(itemWithBookingsAndCommentsDto.getDescription());
        assertThat(result).extractingJsonPathBooleanValue("$.available").isEqualTo(itemWithBookingsAndCommentsDto.getAvailable());
        assertThat(result).extractingJsonPathNumberValue("$.lastBooking.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.lastBooking.start").isEqualTo(itemWithBookingsAndCommentsDto.getLastBooking().getStart().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        assertThat(result).extractingJsonPathStringValue("$.lastBooking.end").isEqualTo(itemWithBookingsAndCommentsDto.getLastBooking().getEnd().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        assertThat(result).extractingJsonPathNumberValue("$.lastBooking.bookerId").isEqualTo(1);
        assertThat(result).extractingJsonPathNumberValue("$.nextBooking.id").isEqualTo(2);
        assertThat(result).extractingJsonPathStringValue("$.nextBooking.start").isEqualTo(itemWithBookingsAndCommentsDto.getNextBooking().getStart().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        assertThat(result).extractingJsonPathStringValue("$.nextBooking.end").isEqualTo(itemWithBookingsAndCommentsDto.getNextBooking().getEnd().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        assertThat(result).extractingJsonPathNumberValue("$.nextBooking.bookerId").isEqualTo(2);
        assertThat(result).extractingJsonPathNumberValue("$.comments[0].id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.comments[0].text").isEqualTo(itemWithBookingsAndCommentsDto.getComments().get(0).getText());
        assertThat(result).extractingJsonPathStringValue("$.comments[0].authorName").isEqualTo(itemWithBookingsAndCommentsDto.getComments().get(0).getAuthorName());
        assertThat(result).extractingJsonPathStringValue("$.comments[0].created").isEqualTo(itemWithBookingsAndCommentsDto.getComments().get(0).getCreated().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
    }
}