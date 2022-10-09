package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.common.Create;
import ru.practicum.shareit.common.Update;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Validated
public class ItemController {

    private final ItemClient itemClient;

    @PostMapping
    public ResponseEntity<Object> createItem(@Validated(Create.class) @RequestBody ItemDto itemDto,
                                             @RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemClient.createItem(itemDto, userId);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> updateItem(@Validated(Update.class) @RequestBody ItemDto itemDto,
                              @RequestHeader("X-Sharer-User-Id") Long userId,
                              @PathVariable Long itemId) {
        return itemClient.updateItem(itemId, itemDto, userId);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getItemById(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                      @PathVariable Long itemId) {
        return itemClient.getItemById(itemId, userId);
    }

    @GetMapping
    public ResponseEntity<Object> getAllItemsForUserId(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                                     @PositiveOrZero @RequestParam(name = "from", defaultValue = "0")  Integer from,
                                                                     @Positive @RequestParam(name = "size", defaultValue = "10")  Integer size) {
        return itemClient.getAllItemsForUser(userId, from, size);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchItemsWithText(@RequestHeader("X-Sharer-User-Id") Long userId,
                                             @RequestParam(name = "text") String text,
                                             @PositiveOrZero @RequestParam(name = "from", defaultValue = "0")  Integer from,
                                             @Positive @RequestParam(name = "size", defaultValue = "10")  Integer size) {
        return itemClient.getItemsWithKeyWord(userId, text, from, size);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> addNewCommentForItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                                           @PathVariable Long itemId,
                                           @Validated(Create.class) @RequestBody CommentDto commentDto) {
        return itemClient.addNewCommentByItemId(itemId, commentDto, userId);
    }
}
