package ru.practicum.shareit.requests.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.item.dto.ItemDto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class ItemRequestWithItemInfoDtoTest {

    @Autowired
    private JacksonTester<ItemRequestWithItemInfoDto> json;

    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss");

    private Long nullRequestId = null;

    private ItemDto itemDto = new ItemDto(
            1L,
            "item_name_1",
            "item_desc_1",
            Boolean.TRUE,
            nullRequestId
    );

    private ItemRequestWithItemInfoDto requestWithItemInfo = new ItemRequestWithItemInfoDto(
            1L,
            "request_1_desc",
            LocalDateTime.parse(LocalDateTime.now().plusHours(1L).format(formatter), formatter),
            List.of(itemDto)
    );

    @Test
    void testItemRequestWithItemInfoDto() throws Exception {
        JsonContent<ItemRequestWithItemInfoDto> result = json.write(requestWithItemInfo);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo(requestWithItemInfo.getDescription());
        assertThat(result).extractingJsonPathStringValue("$.created").isEqualTo(requestWithItemInfo.getCreated().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        assertThat(result).extractingJsonPathNumberValue("$.items[0].id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.items[0].name").isEqualTo(itemDto.getName());
        assertThat(result).extractingJsonPathStringValue("$.items[0].description").isEqualTo(itemDto.getDescription());
        assertThat(result).extractingJsonPathBooleanValue("$.items[0].available").isEqualTo(itemDto.getAvailable());
    }
}