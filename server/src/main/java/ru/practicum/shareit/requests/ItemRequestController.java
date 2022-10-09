package ru.practicum.shareit.requests;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.requests.dto.ItemRequestDto;
import ru.practicum.shareit.requests.dto.ItemRequestWithItemInfoDto;
import ru.practicum.shareit.requests.service.ItemRequestService;

import java.util.List;

@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
public class ItemRequestController {

    private final ItemRequestService itemRequestService;

    @PostMapping
    public ItemRequestDto createItemRequest(@RequestHeader("X-Sharer-User-Id") Long userId,
                                            @RequestBody ItemRequestDto itemRequestDto) {
        return itemRequestService.createItemRequest(userId, itemRequestDto);
    }

    @GetMapping
    public List<ItemRequestWithItemInfoDto> getItemRequestsByOwnerId(@RequestHeader("X-Sharer-User-Id") Long ownerId) {
        return itemRequestService.getItemRequestsByOwnerId(ownerId);
    }

    @GetMapping("/all")
    public List<ItemRequestWithItemInfoDto> getAllItemRequests(@RequestHeader("X-Sharer-User-Id") Long userId,
                                   @RequestParam(name = "from", defaultValue = "0")  Integer from,
                                   @RequestParam(name = "size", defaultValue = "10")  Integer size) {
        return itemRequestService.getAllItemRequests(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public ItemRequestWithItemInfoDto getItemRequestById(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                         @PathVariable Long requestId) {
        return itemRequestService.getItemRequestById(userId, requestId);
    }
}
