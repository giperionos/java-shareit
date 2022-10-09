package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.item.Comment;
import ru.practicum.shareit.item.CommentRepository;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.exceptions.CommentForNotExistBookingException;
import ru.practicum.shareit.item.exceptions.ItemSecurityException;
import ru.practicum.shareit.item.exceptions.ItemUnknownException;
import ru.practicum.shareit.requests.ItemRequest;
import ru.practicum.shareit.requests.ItemRequestRepository;
import ru.practicum.shareit.requests.exceptions.ItemRequestUnknownException;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.exceptions.UserUnknownException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final ItemRequestRepository itemRequestRepository;

    @Override
    public ItemDto createItem(ItemDto itemDto, Long userId) {

        //сначала нужно убедиться, что такой пользователь существует
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserUnknownException(String.format("Пользователь с %d не найден.", userId)));

        ItemRequest request = null;

        //если вещь создается по запросу
        if (itemDto.getRequestId() != null) {

            //проверить, что запрос с таким id есть
            request = itemRequestRepository.findById(itemDto.getRequestId())
                    .orElseThrow(() -> new ItemRequestUnknownException(
                            String.format("Запрос вещи с %d не найден.", itemDto.getRequestId())
                    ));
        }
        Item item = ItemMapper.toItem(itemDto, user, request);

        return ItemMapper.toItemDto(itemRepository.save(item));
    }

    @Override
    public ItemDto updateItem(Long itemId, ItemDto itemDto, Long userId) {

        //сначала нужно убедиться, что такой пользователь существует
        User currentUser = userRepository.findById(userId)
                    .orElseThrow(() -> new UserUnknownException(
                            String.format("Пользователь с %d не найден.", userId))
                    );


        //также нужно проверить, то эта вещь принадлежит этому владельцу
        //сначала проверить, что такая вещь вообще есть
        Item itemForUpdate = itemRepository.findById(itemId)
                .orElseThrow(() -> new ItemUnknownException(
                        String.format("Не найдена вещь с id = %d", itemId))
                );

        //если id пользователя из вещи не совпадает с id пользователя,
        //который пришел из контролера,
        //значит пользователь с фронта пытается редактировать не свою вещь
        if (itemForUpdate.getOwner().getId().longValue() != currentUser.getId().longValue()) {
            throw new ItemSecurityException(String.format("Пользователь с id = %d не может работать с вещью с id = %d",
                    currentUser.getId(), itemForUpdate.getId()));
        }

        //если все ок, значит можно редактировать
        //обновить нужно только те поля, что пришли
        if (itemDto.getName() != null) {
            itemForUpdate.setName(itemDto.getName());
        }

        if (itemDto.getDescription() != null) {
            itemForUpdate.setDescription(itemDto.getDescription());
        }

        if (itemDto.getAvailable() != null) {
            itemForUpdate.setAvailable(itemDto.getAvailable());
        }

        //теперь обновить в БД
        return ItemMapper.toItemDto(itemRepository.save(itemForUpdate));
    }

    @Override
    public ItemWithBookingsAndCommentsDto getItemById(Long itemId, Long userId) {
        //сначала нужно убедиться, что такой пользователь существует
        userRepository.findById(userId)
                .orElseThrow(() -> new UserUnknownException(String.format("Пользователь с %d не найден.", userId)));

        //проверить, что такая вещь есть
        Item foundedItem = itemRepository.findById(itemId)
                .orElseThrow(() -> new ItemUnknownException(
                        String.format("Не найдена вещь с id = %d", itemId))
                );

        //получить список текущих броней для этой вещи, чтобы проверить далее,
        //для проверки достаточно одной
        List<Booking> activeBookings = bookingRepository
                .findAllByItem_IdInAndStartBeforeAndEndAfter(
                        List.of(itemId), LocalDateTime.now(), LocalDateTime.now(), PageRequest.of(0, 1));

        //Получить комментарии к данной вещи
        List<Comment> comments = getCommentsForItem(itemId);

        //Если вещь запрашивает не ее владелец
        if (foundedItem.getOwner().getId().longValue() != userId.longValue()) {

            //то нужно проверить на доступность, а также что вещь сейчас не забронирована
            if (foundedItem.getAvailable() && (activeBookings == null || activeBookings.isEmpty())) {
                return ItemMapper.toItemWithBookingsDto(foundedItem, null, null, comments);
            } else {
                throw new ItemUnknownException(String.format("Не найдена вещь с id = %d", itemId));
            }
        }

        //Если вещь запрашивает владелец, значит нужно дополнить информации о бронировании
        return ItemMapper.toItemWithBookingsDto(foundedItem, getLastBookingForItem(itemId), getNextBookingForItem(itemId), comments);
    }

    @Override
    public List<ItemWithBookingsAndCommentsDto> getAllItemsForUser(Long userId, Integer from, Integer size) {

        int page = from / size;
        final PageRequest pageRequest = PageRequest.of(page, size, Sort.by("id").ascending());

        //сначала нужно убедиться, что такой пользователь существует
        userRepository.findById(userId)
                .orElseThrow(() -> new UserUnknownException(String.format("Пользователь с %d не найден.", userId)));

        //получить все вещи пользователя
        return itemRepository.findItemsByOwnerId(userId, pageRequest)
                .stream()
                .map((item) -> ItemMapper.toItemWithBookingsDto(
                        item,
                        getLastBookingForItem(item.getId()),
                        getNextBookingForItem(item.getId()),
                        getCommentsForItem(item.getId()))
                ).collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> getItemsWithKeyWord(String keyWord, Integer from, Integer size) {

        if (keyWord == null || keyWord.isBlank()) {
            return new ArrayList<>();
        }

        int page = from / size;
        final PageRequest pageRequest = PageRequest.of(page, size);

        return itemRepository.findItemsByKeyWord(keyWord.toLowerCase(), pageRequest).stream()
                .filter((Item::getAvailable))
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public CommentDto addNewCommentByItemId(Long itemId, CommentDto commentDto, Long userId) {
        //сначала нужно убедиться, что такой пользователь существует
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserUnknownException(String.format("Пользователь с %d не найден.", userId)));

        //проверить, что такая вещь есть
        Item foundedItem = itemRepository.findById(itemId)
                .orElseThrow(() -> new ItemUnknownException(String.format("Не найдена вещь с id = %d", itemId)));

        //проверить, что пользователь, который пишет комментарий, действительно брал вещь в аренду
        List<Booking> booking = bookingRepository.findBookingByBooker_IdAndItem_IdAndEndBefore(
                user.getId(), foundedItem.getId(), LocalDateTime.now());

        if (booking == null || booking.isEmpty()) {
            throw new CommentForNotExistBookingException(
                    String.format("Пользователь с id = %s не брал в аренду вещь с id = %s",userId, itemId)
            );
        }

        //если все ок, значит этот пользователь брал в аренду эту вещь,
        //значит можно оставить комментарий
        Comment currentComment = CommentMapper.toComment(foundedItem, commentDto, user);

        return CommentMapper.toCommentDto(commentRepository.save(currentComment));
    }

    private Booking getLastBookingForItem(Long itemId) {
        //last - последнее завершенное или текущее
        Booking lastBooking;
        List<Booking> lastItemBookings = bookingRepository.findAllByItem_IdInAndStatusInAndEndBefore(
                List.of(itemId), List.of(BookingStatus.CANCELED, BookingStatus.APPROVED), LocalDateTime.now(), PageRequest.of(0, 1));
        if (lastItemBookings == null || lastItemBookings.isEmpty()) {
            List<Booking> currentItemBookings = bookingRepository
                    .findAllByItem_IdInAndStartBeforeAndEndAfter(
                            List.of(itemId), LocalDateTime.now(), LocalDateTime.now(), PageRequest.of(0, 1));

            if (currentItemBookings == null || currentItemBookings.isEmpty()) {
                lastBooking = null;
            } else {
                lastBooking = currentItemBookings.get(0);
            }
        } else {
            lastBooking = lastItemBookings.get(0);
        }
        return lastBooking;
    }

    private Booking getNextBookingForItem(Long itemId) {
        Booking nextBooking;

        List<Booking> nextBookings = bookingRepository.findAllByItem_IdInAndStartAfter(
                List.of(itemId), LocalDateTime.now(), PageRequest.of(0, 1));

        if (nextBookings == null || nextBookings.isEmpty()) {
            nextBooking = null;
        } else {
            nextBooking = nextBookings.get(0);
        }
        return nextBooking;
    }

    private List<Comment> getCommentsForItem(Long itemId) {
        return commentRepository.findAllByItem_IdOrderByCreatedDesc(itemId);
    }
}
