package ru.practicum.shareit.requests.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.requests.ItemRequest;
import ru.practicum.shareit.requests.ItemRequestRepository;
import ru.practicum.shareit.requests.dto.ItemRequestDto;
import ru.practicum.shareit.requests.dto.ItemRequestMapper;
import ru.practicum.shareit.requests.dto.ItemRequestWithItemInfoDto;
import ru.practicum.shareit.requests.exceptions.ItemRequestUnknownException;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.exceptions.UserUnknownException;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {

    private final ItemRequestRepository itemRequestRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Override
    public ItemRequestDto createItemRequest(Long userId, ItemRequestDto itemRequestDto) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserUnknownException(String.format("Пользователь с %d не найден.", userId)));

        ItemRequest itemRequest = ItemRequestMapper.toItemRequest(user, itemRequestDto);

        return ItemRequestMapper.toItemRequestDto(itemRequestRepository.save(itemRequest));
    }

    @Override
    public List<ItemRequestWithItemInfoDto> getItemRequestsByOwnerId(Long ownerId) {

        User user = userRepository.findById(ownerId)
                .orElseThrow(() -> new UserUnknownException(String.format("Пользователь с %d не найден.", ownerId)));

        List<ItemRequest> ownerRequests = itemRequestRepository.getAllByRequester_IdOrderByCreatedDesc(user.getId());

        return ownerRequests.stream().map((itemRequest ->
            ItemRequestMapper.toItemRequestWithItemInfoDto(
                    itemRequest, itemRepository.findAllByRequest_Id(itemRequest.getId())
            )
        )).collect(Collectors.toList());
    }

    @Override
    public List<ItemRequestWithItemInfoDto> getAllItemRequests(Long userId, PageRequest pageRequest) {

        //запрашивать может любой пользователь, но все равно проверить, что такой есть
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserUnknownException(String.format("Пользователь с %d не найден.", userId)));

        //получить все запросы
        List<ItemRequest> requests = itemRequestRepository.findAll(pageRequest).toList();

        return requests.stream()
                .filter((itemRequest) -> itemRequest.getRequester().getId().longValue() != userId.longValue())
                .map((itemRequest -> ItemRequestMapper.toItemRequestWithItemInfoDto(
                        itemRequest, itemRepository.findAllByRequest_Id(itemRequest.getId())
                )
        )).collect(Collectors.toList());
    }

    @Override
    public ItemRequestWithItemInfoDto getItemRequestById(Long userId, Long requestId) {

        //запрашивать может любой пользователь, но все равно проверить, что такой есть
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserUnknownException(String.format("Пользователь с %d не найден.", userId)));

        ItemRequest request = itemRequestRepository.findById(requestId)
                .orElseThrow(() -> new ItemRequestUnknownException(String.format("Запрос вещи с %d не найден.", requestId)));

        return ItemRequestMapper.toItemRequestWithItemInfoDto(request, itemRepository.findAllByRequest_Id(request.getId()));
    }
}
