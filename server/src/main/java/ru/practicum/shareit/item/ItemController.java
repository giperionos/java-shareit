package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
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
    public ItemDto createItem(@RequestBody ItemDto itemDto,
                              @RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemService.createItem(itemDto, userId);
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@RequestBody ItemDto itemDto,
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
    public List<ItemWithBookingsAndCommentsDto> getAllItemsForUserId(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                                     @RequestParam(name = "from", defaultValue = "0")  Integer from,
                                                                     @RequestParam(name = "size", defaultValue = "10")  Integer size) {
        return itemService.getAllItemsForUser(userId, from, size);
    }

    @GetMapping("/search")
    public List<ItemDto> searchItemsWithText(@RequestHeader("X-Sharer-User-Id") Long userId,
                                             @RequestParam(name = "text") String text,
                                             @RequestParam(name = "from", defaultValue = "0")  Integer from,
                                             @RequestParam(name = "size", defaultValue = "10")  Integer size) {
        return itemService.getItemsWithKeyWord(text, from, size);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto addNewCommentForItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                                           @PathVariable Long itemId,
                                           @RequestBody CommentDto commentDto) {
        return itemService.addNewCommentByItemId(itemId, commentDto, userId);
    }
}
