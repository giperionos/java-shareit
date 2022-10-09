package ru.practicum.shareit.item.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class ItemBookingInfoDtoTest {

    @Autowired
    private JacksonTester<ItemBookingInfoDto> json;

    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss");

    private ItemBookingInfoDto itemBookingInfoDto = new ItemBookingInfoDto(
            1L,
            LocalDateTime.parse(LocalDateTime.now().plusHours(1L).format(formatter), formatter),
            LocalDateTime.parse(LocalDateTime.now().plusHours(3L).format(formatter), formatter),
            1L
    );

    @Test
    void testItemBookingInfoDto() throws Exception {
        JsonContent<ItemBookingInfoDto> result = json.write(itemBookingInfoDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.start").isEqualTo(itemBookingInfoDto.getStart().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        assertThat(result).extractingJsonPathStringValue("$.end").isEqualTo(itemBookingInfoDto.getEnd().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        assertThat(result).extractingJsonPathNumberValue("$.bookerId").isEqualTo(1);
    }
}