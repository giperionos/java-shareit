package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemCreateMarker;
import ru.practicum.shareit.item.dto.ItemUpdateMarker;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;

    @PostMapping
    public ItemDto createItem(@Validated(ItemCreateMarker.class) @RequestBody ItemDto itemDto,
                              @RequestHeader("X-Sharer-User-Id") Long userId) {
        Item createdItem = itemService.createItem(ItemMapper.toItem(itemDto, userId));
        return ItemMapper.toItemDto(createdItem);
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@Validated(ItemUpdateMarker.class) @RequestBody ItemDto itemDto,
                              @RequestHeader("X-Sharer-User-Id") Long userId,
                              @PathVariable Long itemId) {
        Item updatedItem = itemService.updateItem(itemId, ItemMapper.toItem(itemDto, userId));
        return ItemMapper.toItemDto(updatedItem);
    }

    @GetMapping("/{itemId}")
    public ItemDto getItemById(@RequestHeader("X-Sharer-User-Id") Long userId,
                               @PathVariable Long itemId) {
        Item foundedItem = itemService.getItemById(itemId);
        return ItemMapper.toItemDto(foundedItem);
    }

    @GetMapping
    public List<ItemDto> getAllItemsForUserId(@RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemService.getAllItemsForUser(userId).stream().map(ItemMapper::toItemDto).collect(Collectors.toList());
    }

    @GetMapping("/search")
    public List<ItemDto> searchItemsWithText(@RequestHeader("X-Sharer-User-Id") Long userId,
                                             @RequestParam(name = "text") String text) {
        return itemService.getItemsWithKeyWord(text).stream().map(ItemMapper::toItemDto).collect(Collectors.toList());
    }
}