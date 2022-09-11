package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.common.Create;
import ru.practicum.shareit.common.Update;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithBookingsAndCommentsDto;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;

    @PostMapping
    public ItemDto createItem(@Validated(Create.class) @RequestBody ItemDto itemDto,
                              @RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemService.createItem(itemDto, userId);
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@Validated(Update.class) @RequestBody ItemDto itemDto,
                              @RequestHeader("X-Sharer-User-Id") Long userId,
                              @PathVariable Long itemId) {
        return itemService.updateItem(itemId, itemDto, userId);
    }

    @GetMapping("/{itemId}")
    public ItemWithBookingsAndCommentsDto getItemById(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                      @PathVariable Long itemId) {
        return itemService.getItemById(itemId, userId);
    }

    @GetMapping
    public List<ItemWithBookingsAndCommentsDto> getAllItemsForUserId(@RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemService.getAllItemsForUser(userId);
    }

    @GetMapping("/search")
    public List<ItemDto> searchItemsWithText(@RequestHeader("X-Sharer-User-Id") Long userId,
                                             @RequestParam(name = "text") String text) {
        return itemService.getItemsWithKeyWord(text);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto addNewCommentForItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                                           @PathVariable Long itemId,
                                           @Validated(Create.class) @RequestBody CommentDto commentDto) {
        return itemService.addNewCommentByItemId(itemId, commentDto, userId);
    }
}
