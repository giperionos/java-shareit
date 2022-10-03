package ru.practicum.shareit.item.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageRequest;
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
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ItemServiceImplTest {

    ItemService itemService;

    ItemRepository itemRepository;
    UserRepository userRepository;
    BookingRepository bookingRepository;
    CommentRepository commentRepository;
    ItemRequestRepository itemRequestRepository;

    private Long unknownUserId = 100L;
    private Boolean available = Boolean.TRUE;
    private Boolean unavailable = Boolean.FALSE;
    private ItemRequest nullRequest = null;
    private Long nullRequestId = null;
    private String keyWord = "item";
    private String unknownKeyWord = "unknown";
    private String nullKeyWord = null;
    private String blankKeyWord = "";
    private Integer from = 0;
    private Integer size = 10;
    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss");

    private User user1;
    private User user2;
    private User user3;

    private Item item1;
    private Item item2;
    private Item item3;
    private Item item4;

    private ItemDto item1Dto;
    private ItemDto item2Dto;
    private ItemDto item3Dto;
    private ItemDto item4Dto;

    private ItemWithBookingsAndCommentsDto item4FullInfoDto;
    private Booking lastBooking;
    private Booking nextBooking;
    private ItemBookingInfoDto lastBookingDto;
    private ItemBookingInfoDto nextBookingDto;

    private Comment comment1;
    private Comment comment2;
    private CommentDto commentDto1;
    private CommentDto commentDto2;

    private ItemRequest itemRequest;

    @BeforeEach
    void beforeEach() {
        itemRepository = mock(ItemRepository.class);
        userRepository = mock(UserRepository.class);
        bookingRepository = mock(BookingRepository.class);
        commentRepository = mock(CommentRepository.class);
        itemRequestRepository = mock(ItemRequestRepository.class);

        itemService = new ItemServiceImpl(
                itemRepository,
                userRepository,
                bookingRepository,
                commentRepository,
                itemRequestRepository
        );

        user1 = new User(1L,"User_name_1", "User1@email.ru");
        user2 = new User(2L,"User_name_2", "User2@email.ru");
        user3 = new User(3L,"User_name_3", "User3@email.ru");

        itemRequest = new ItemRequest(
                1L,
                "Description_1",
                user1,
                LocalDateTime.parse(LocalDateTime.now().plusHours(1L).format(formatter), formatter)
        );

        item1 = new Item(
                1L,
                "item_name_1",
                "item_desc_1",
                available,
                user1,
                nullRequest
        );

        item1Dto = new ItemDto(
                1L,
                "item_name_1",
                "item_desc_1",
                available,
                nullRequestId
        );

        item2 = new Item(
                2L,
                "item_name_2",
                "item_desc_2",
                unavailable,
                user1,
                nullRequest
        );

        item2Dto = new ItemDto(
                2L,
                "item_name_2",
                "item_desc_2",
                unavailable,
                nullRequestId
        );

        item3 = new Item(
                3L,
                "item_name_3",
                "item_desc_3",
                available,
                user2,
                itemRequest
        );

        item3Dto = new ItemDto(
                3L,
                "item_name_3",
                "item_desc_3",
                available,
                itemRequest.getId()
        );

        item4 = new Item(
                4L,
                "item_name_4",
                "item_desc_4",
                available,
                user3,
                nullRequest
        );

        item4Dto = new ItemDto(
                4L,
                "item_name_4",
                "item_desc_4",
                available,
                nullRequestId
        );

        lastBookingDto = new ItemBookingInfoDto(
             1L,
             LocalDateTime.parse(LocalDateTime.now().minusDays(3L).format(formatter), formatter),
             LocalDateTime.parse(LocalDateTime.now().minusDays(2L).format(formatter), formatter),
             user1.getId()
        );

        lastBooking = new Booking(
                1L,
                LocalDateTime.parse(LocalDateTime.now().minusDays(3L).format(formatter), formatter),
                LocalDateTime.parse(LocalDateTime.now().minusDays(2L).format(formatter), formatter),
                item4,
                user1,
                BookingStatus.CANCELED
        );

        nextBookingDto = new ItemBookingInfoDto(
                2L,
                LocalDateTime.parse(LocalDateTime.now().plusDays(2L).format(formatter), formatter),
                LocalDateTime.parse(LocalDateTime.now().plusDays(3L).format(formatter), formatter),
                user2.getId()
        );

        nextBooking = new Booking(
                2L,
                LocalDateTime.parse(LocalDateTime.now().plusDays(2L).format(formatter), formatter),
                LocalDateTime.parse(LocalDateTime.now().plusDays(3L).format(formatter), formatter),
                item4,
                user2,
                BookingStatus.APPROVED
        );

        commentDto1 = new CommentDto(
                1L,
                "Comment_1_text",
                "User_name_1",
                LocalDateTime.parse(LocalDateTime.now().minusDays(1L).format(formatter), formatter)
        );

        comment1 = new Comment(
                1L,
                "Comment_1_text",
                item4,
                user1,
                LocalDateTime.parse(LocalDateTime.now().minusDays(1L).format(formatter), formatter)
        );

        commentDto2 = new CommentDto(
                2L,
                "Comment_2_text",
                "User_name_2",
                LocalDateTime.parse(LocalDateTime.now().minusHours(1L).format(formatter), formatter)
        );

        comment2 = new Comment(
                2L,
                "Comment_2_text",
                item4,
                user2,
                LocalDateTime.parse(LocalDateTime.now().minusHours(1L).format(formatter), formatter)
        );

        item4FullInfoDto = new ItemWithBookingsAndCommentsDto(
                4L,
                "item_name_4",
                "item_desc_4",
                available,
                lastBookingDto,
                nextBookingDto,
                List.of(commentDto1,commentDto2)
        );
    }

    @Test
    void testSuccessCreateItem() {
        when(userRepository.findById(any(Long.class)))
                .thenReturn(Optional.of(user1));

        when(itemRequestRepository.findById(any(Long.class)))
                .thenReturn(Optional.empty());

        when(itemRepository.save(any(Item.class)))
                .thenReturn(item1);

        final ItemDto itemDtoResult = itemService.createItem(item1Dto, user1.getId());

        assertNotNull(itemDtoResult);
        assertEquals(item1Dto.getId(), itemDtoResult.getId());
        assertEquals(item1Dto.getName(), itemDtoResult.getName());
        assertEquals(item1Dto.getDescription(), itemDtoResult.getDescription());
        assertEquals(item1Dto.getAvailable(), itemDtoResult.getAvailable());
        assertEquals(item1Dto.getRequestId(), itemDtoResult.getRequestId());

        verify(userRepository, times(1)).findById(any(Long.class));
        verify(itemRequestRepository, times(0)).findById(any(Long.class));
        verify(itemRepository, times(1)).save(any(Item.class));
    }

    @Test
    void testSuccessCreateItemByRequest() {
        when(userRepository.findById(any(Long.class)))
                .thenReturn(Optional.of(user2));

        when(itemRequestRepository.findById(any(Long.class)))
                .thenReturn(Optional.of(itemRequest));

        when(itemRepository.save(any(Item.class)))
                .thenReturn(item3);

        final ItemDto itemDtoResult = itemService.createItem(item3Dto, user2.getId());

        assertNotNull(itemDtoResult);
        assertEquals(item3Dto.getId(), itemDtoResult.getId());
        assertEquals(item3Dto.getName(), itemDtoResult.getName());
        assertEquals(item3Dto.getDescription(), itemDtoResult.getDescription());
        assertEquals(item3Dto.getAvailable(), itemDtoResult.getAvailable());
        assertEquals(item3Dto.getRequestId(), itemDtoResult.getRequestId());

        verify(userRepository, times(1)).findById(any(Long.class));
        verify(itemRequestRepository, times(1)).findById(any(Long.class));
        verify(itemRepository, times(1)).save(any(Item.class));
    }

    @Test
    void testGetExceptionCreateItemWithUnknownUserId() {
        when(userRepository.findById(unknownUserId))
                .thenReturn(Optional.empty());

        when(itemRequestRepository.findById(any(Long.class)))
                .thenReturn(Optional.empty());

        final UserUnknownException exception = assertThrows(
                UserUnknownException.class,
                () -> itemService.createItem(ItemMapper.toItemDto(item1), unknownUserId)
        );

        final String expectedMessage = String.format("Пользователь с %d не найден.", unknownUserId);

        assertEquals(expectedMessage, exception.getMessage());

        verify(userRepository, times(1)).findById(unknownUserId);
        verify(itemRequestRepository, times(0)).findById(any(Long.class));
        verify(itemRepository, times(0)).save(any(Item.class));
    }

    @Test
    void testGetExceptionCreateItemWithUnknownRequest() {
        when(userRepository.findById(any(Long.class)))
                .thenReturn(Optional.of(user1));

        when(itemRequestRepository.findById(any(Long.class)))
                .thenReturn(Optional.empty());

        final ItemRequestUnknownException exception = assertThrows(
                ItemRequestUnknownException.class,
                () -> itemService.createItem(ItemMapper.toItemDto(item3), user1.getId())
        );

        final String expectedMessage = String.format("Запрос вещи с %d не найден.", item3.getRequest().getId());

        assertEquals(expectedMessage, exception.getMessage());

        verify(userRepository, times(1)).findById(any(Long.class));
        verify(itemRequestRepository, times(1)).findById(any(Long.class));
        verify(itemRepository, times(0)).save(any(Item.class));
    }

    @Test
    void testSuccessUpdateItem() {
        when(userRepository.findById(any(Long.class)))
                .thenReturn(Optional.of(user1));

        when(itemRepository.findById(any(Long.class)))
                .thenReturn(Optional.of(item1));

        when(itemRepository.save(any(Item.class)))
                .thenReturn(item1);

        final ItemDto itemDtoResult = itemService.updateItem(item1Dto.getId(), item1Dto, user1.getId());

        assertNotNull(itemDtoResult);
        assertEquals(item1Dto.getId(), itemDtoResult.getId());
        assertEquals(item1Dto.getName(), itemDtoResult.getName());
        assertEquals(item1Dto.getDescription(), itemDtoResult.getDescription());
        assertEquals(item1Dto.getAvailable(), itemDtoResult.getAvailable());
        assertEquals(item1Dto.getRequestId(), itemDtoResult.getRequestId());

        verify(userRepository, times(1)).findById(any(Long.class));
        verify(itemRepository, times(1)).findById(any(Long.class));
        verify(itemRepository, times(1)).save(any(Item.class));
    }

    @Test
    void testSuccessUpdateOnlyNameInItem() {
        when(userRepository.findById(any(Long.class)))
                .thenReturn(Optional.of(user1));

        ItemDto itemDtoWithOnlyName = new ItemDto();
        String newName = "Rename That Name";

        itemDtoWithOnlyName.setName(newName);
        item1.setName(newName);

        when(itemRepository.findById(any(Long.class)))
                .thenReturn(Optional.of(item1));

        when(itemRepository.save(any(Item.class)))
                .thenReturn(item1);

        final ItemDto itemDtoResult = itemService.updateItem(item1Dto.getId(), item1Dto, user1.getId());

        assertNotNull(itemDtoResult);
        assertEquals(item1Dto.getId(), itemDtoResult.getId());
        assertEquals(item1Dto.getName(), itemDtoResult.getName());
        assertEquals(item1Dto.getDescription(), itemDtoResult.getDescription());
        assertEquals(item1Dto.getAvailable(), itemDtoResult.getAvailable());
        assertEquals(item1Dto.getRequestId(), itemDtoResult.getRequestId());

        verify(userRepository, times(1)).findById(any(Long.class));
        verify(itemRepository, times(1)).findById(any(Long.class));
        verify(itemRepository, times(1)).save(any(Item.class));
    }

    @Test
    void testSuccessUpdateOnlyDescriptionInItem() {
        when(userRepository.findById(any(Long.class)))
                .thenReturn(Optional.of(user1));

        ItemDto itemDtoWithOnlyDescription = new ItemDto();
        String newDescription = "Rename That Description";

        itemDtoWithOnlyDescription.setDescription(newDescription);
        item1.setDescription(newDescription);

        when(itemRepository.findById(any(Long.class)))
                .thenReturn(Optional.of(item1));

        when(itemRepository.save(any(Item.class)))
                .thenReturn(item1);

        final ItemDto itemDtoResult = itemService.updateItem(item1Dto.getId(), item1Dto, user1.getId());

        assertNotNull(itemDtoResult);
        assertEquals(item1Dto.getId(), itemDtoResult.getId());
        assertEquals(item1Dto.getName(), itemDtoResult.getName());
        assertEquals(item1Dto.getDescription(), itemDtoResult.getDescription());
        assertEquals(item1Dto.getAvailable(), itemDtoResult.getAvailable());
        assertEquals(item1Dto.getRequestId(), itemDtoResult.getRequestId());

        verify(userRepository, times(1)).findById(any(Long.class));
        verify(itemRepository, times(1)).findById(any(Long.class));
        verify(itemRepository, times(1)).save(any(Item.class));
    }

    @Test
    void testSuccessUpdateOnlyAvailableInItem() {
        when(userRepository.findById(any(Long.class)))
                .thenReturn(Optional.of(user1));

        ItemDto itemDtoWithOnlyDescription = new ItemDto();

        itemDtoWithOnlyDescription.setAvailable(unavailable);
        item1.setAvailable(unavailable);

        when(itemRepository.findById(any(Long.class)))
                .thenReturn(Optional.of(item1));

        when(itemRepository.save(any(Item.class)))
                .thenReturn(item1);

        final ItemDto itemDtoResult = itemService.updateItem(item1Dto.getId(), item1Dto, user1.getId());

        assertNotNull(itemDtoResult);
        assertEquals(item1Dto.getId(), itemDtoResult.getId());
        assertEquals(item1Dto.getName(), itemDtoResult.getName());
        assertEquals(item1Dto.getDescription(), itemDtoResult.getDescription());
        assertEquals(item1Dto.getAvailable(), itemDtoResult.getAvailable());
        assertEquals(item1Dto.getRequestId(), itemDtoResult.getRequestId());

        verify(userRepository, times(1)).findById(any(Long.class));
        verify(itemRepository, times(1)).findById(any(Long.class));
        verify(itemRepository, times(1)).save(any(Item.class));
    }

    @Test
    void testGetExceptionOnUpdateItemWithUnknownUserId() {
        when(userRepository.findById(unknownUserId))
                .thenReturn(Optional.empty());

        when(itemRepository.findById(any(Long.class)))
                .thenReturn(Optional.empty());

        when(itemRepository.save(any(Item.class)))
                .thenReturn(item1);

        final UserUnknownException exception = assertThrows(
                UserUnknownException.class,
                () -> itemService.updateItem(item1Dto.getId(), item1Dto, unknownUserId)
        );

        final String expectedMessage = String.format("Пользователь с %d не найден.", unknownUserId);

        assertEquals(expectedMessage, exception.getMessage());

        verify(userRepository, times(1)).findById(any(Long.class));
        verify(itemRepository, times(0)).findById(any(Long.class));
        verify(itemRepository, times(0)).save(any(Item.class));
    }

    @Test
    void testGetExceptionOnUpdateItemWithUnknownItemId() {
        when(userRepository.findById(any(Long.class)))
                .thenReturn(Optional.of(user1));

        when(itemRepository.findById(any(Long.class)))
                .thenReturn(Optional.empty());

        when(itemRepository.save(any(Item.class)))
                .thenReturn(item1);

        final ItemUnknownException exception = assertThrows(
                ItemUnknownException.class,
                () -> itemService.updateItem(item1Dto.getId(), item1Dto, user1.getId())
        );

        final String expectedMessage =  String.format("Не найдена вещь с id = %d", item1Dto.getId());

        assertEquals(expectedMessage, exception.getMessage());

        verify(userRepository, times(1)).findById(any(Long.class));
        verify(itemRepository, times(1)).findById(any(Long.class));
        verify(itemRepository, times(0)).save(any(Item.class));
    }

    @Test
    void testGetExceptionOnUpdateItemByNotOwner() {
        when(userRepository.findById(any(Long.class)))
                .thenReturn(Optional.of(user2));

        when(itemRepository.findById(any(Long.class)))
                .thenReturn(Optional.of(item1));

        when(itemRepository.save(any(Item.class)))
                .thenReturn(item1);

        final ItemSecurityException exception = assertThrows(
                ItemSecurityException.class,
                () -> itemService.updateItem(item1Dto.getId(), item1Dto, user2.getId())
        );

        final String expectedMessage = String.format("Пользователь с id = %d не может работать с вещью с id = %d",
                user2.getId(), item1.getId());

        assertEquals(expectedMessage, exception.getMessage());

        verify(userRepository, times(1)).findById(any(Long.class));
        verify(itemRepository, times(1)).findById(any(Long.class));
        verify(itemRepository, times(0)).save(any(Item.class));
    }

    @Test
    void testSuccessGetItemByIdForOwner() {
        when(userRepository.findById(any(Long.class)))
                .thenReturn(Optional.of(user3));

        when(itemRepository.findById(any(Long.class)))
                .thenReturn(Optional.of(item4));

        when(bookingRepository.findAllByItem_IdInAndStartBeforeAndEndAfter(anyList(), any(LocalDateTime.class), any(LocalDateTime.class), any(PageRequest.class)))
                .thenReturn(List.of(lastBooking, nextBooking));

        when(commentRepository.findAllByItem_IdOrderByCreatedDesc(item4.getId()))
                .thenReturn(List.of(comment1, comment2));

        when(bookingRepository.findAllByItem_IdInAndStatusInAndEndBefore(anyList(), anyList(), any(LocalDateTime.class), any(PageRequest.class)))
                .thenReturn(List.of(lastBooking));

        when(bookingRepository.findAllByItem_IdInAndStartAfter(anyList(), any(LocalDateTime.class), any(PageRequest.class)))
                .thenReturn(List.of(nextBooking));

        final ItemWithBookingsAndCommentsDto result = itemService.getItemById(item4.getId(), user3.getId());

        assertNotNull(result);
        assertEquals(item4FullInfoDto.getId(), result.getId());
        assertEquals(item4FullInfoDto.getName(), result.getName());
        assertEquals(item4FullInfoDto.getDescription(), result.getDescription());
        assertEquals(item4FullInfoDto.getAvailable(), result.getAvailable());

        assertNotNull(result.getLastBooking());
        assertEquals(item4FullInfoDto.getLastBooking().getId(), result.getLastBooking().getId());
        assertEquals(item4FullInfoDto.getLastBooking().getStart(), result.getLastBooking().getStart());
        assertEquals(item4FullInfoDto.getLastBooking().getEnd(), result.getLastBooking().getEnd());
        assertEquals(item4FullInfoDto.getLastBooking().getBookerId(), result.getLastBooking().getBookerId());

        assertNotNull(result.getNextBooking());
        assertEquals(item4FullInfoDto.getNextBooking().getId(), result.getNextBooking().getId());
        assertEquals(item4FullInfoDto.getNextBooking().getStart(), result.getNextBooking().getStart());
        assertEquals(item4FullInfoDto.getNextBooking().getEnd(), result.getNextBooking().getEnd());
        assertEquals(item4FullInfoDto.getNextBooking().getBookerId(), result.getNextBooking().getBookerId());

        assertNotNull(result.getComments());
        assertEquals(2, result.getComments().size());

        assertEquals(item4FullInfoDto.getComments().get(0).getId(), result.getComments().get(0).getId());
        assertEquals(item4FullInfoDto.getComments().get(0).getText(), result.getComments().get(0).getText());
        assertEquals(item4FullInfoDto.getComments().get(0).getAuthorName(), result.getComments().get(0).getAuthorName());
        assertEquals(item4FullInfoDto.getComments().get(0).getCreated(), result.getComments().get(0).getCreated());

        assertEquals(item4FullInfoDto.getComments().get(1).getId(), result.getComments().get(1).getId());
        assertEquals(item4FullInfoDto.getComments().get(1).getText(), result.getComments().get(1).getText());
        assertEquals(item4FullInfoDto.getComments().get(1).getAuthorName(), result.getComments().get(1).getAuthorName());
        assertEquals(item4FullInfoDto.getComments().get(1).getCreated(), result.getComments().get(1).getCreated());

        verify(userRepository, times(1)).findById(any(Long.class));
        verify(itemRepository, times(1)).findById(any(Long.class));
        verify(bookingRepository, times(1)).findAllByItem_IdInAndStartBeforeAndEndAfter(anyList(), any(LocalDateTime.class), any(LocalDateTime.class), any(PageRequest.class));
        verify(bookingRepository, times(1)).findAllByItem_IdInAndStatusInAndEndBefore(anyList(), anyList(), any(LocalDateTime.class), any(PageRequest.class));
        verify(bookingRepository, times(1)).findAllByItem_IdInAndStartAfter(anyList(), any(LocalDateTime.class), any(PageRequest.class));
        verify(commentRepository, times(1)).findAllByItem_IdOrderByCreatedDesc(any(Long.class));
    }

    @Test
    void testGetExceptionOnGetUnavailableItemByIdForSomeUser() {
        when(userRepository.findById(any(Long.class)))
                .thenReturn(Optional.of(user1));

        when(itemRepository.findById(any(Long.class)))
                .thenReturn(Optional.of(item4));

        when(bookingRepository.findAllByItem_IdInAndStartBeforeAndEndAfter(anyList(), any(LocalDateTime.class), any(LocalDateTime.class), any(PageRequest.class)))
                .thenReturn(List.of(lastBooking, nextBooking));

        when(commentRepository.findAllByItem_IdOrderByCreatedDesc(item4.getId()))
                .thenReturn(List.of(comment1, comment2));

        when(bookingRepository.findAllByItem_IdInAndStatusInAndEndBefore(anyList(), anyList(), any(LocalDateTime.class), any(PageRequest.class)))
                .thenReturn(List.of(lastBooking));

        when(bookingRepository.findAllByItem_IdInAndStartAfter(anyList(), any(LocalDateTime.class), any(PageRequest.class)))
                .thenReturn(List.of(nextBooking));

        final ItemUnknownException exception = assertThrows(
                ItemUnknownException.class,
                () -> itemService.getItemById(item4.getId(), user1.getId())
        );

        final String expectedMessage = String.format("Не найдена вещь с id = %d", item4.getId());

        assertEquals(expectedMessage, exception.getMessage());

        verify(userRepository, times(1)).findById(any(Long.class));
        verify(itemRepository, times(1)).findById(any(Long.class));
        verify(bookingRepository, times(1)).findAllByItem_IdInAndStartBeforeAndEndAfter(anyList(), any(LocalDateTime.class), any(LocalDateTime.class), any(PageRequest.class));
        verify(bookingRepository, times(0)).findAllByItem_IdInAndStatusInAndEndBefore(anyList(), anyList(), any(LocalDateTime.class), any(PageRequest.class));
        verify(bookingRepository, times(0)).findAllByItem_IdInAndStartAfter(anyList(), any(LocalDateTime.class), any(PageRequest.class));
        verify(commentRepository, times(1)).findAllByItem_IdOrderByCreatedDesc(any(Long.class));
    }

    @Test
    void testSuccessGetAvailableItemByIdForSomeUser() {
        when(userRepository.findById(any(Long.class)))
                .thenReturn(Optional.of(user1));

        when(itemRepository.findById(any(Long.class)))
                .thenReturn(Optional.of(item4));

        when(bookingRepository.findAllByItem_IdInAndStartBeforeAndEndAfter(anyList(), any(LocalDateTime.class), any(LocalDateTime.class), any(PageRequest.class)))
                .thenReturn(Collections.emptyList());

        when(commentRepository.findAllByItem_IdOrderByCreatedDesc(item4.getId()))
                .thenReturn(List.of(comment1, comment2));

        when(bookingRepository.findAllByItem_IdInAndStatusInAndEndBefore(anyList(), anyList(), any(LocalDateTime.class), any(PageRequest.class)))
                .thenReturn(List.of(lastBooking));

        when(bookingRepository.findAllByItem_IdInAndStartAfter(anyList(), any(LocalDateTime.class), any(PageRequest.class)))
                .thenReturn(List.of(nextBooking));


        final ItemWithBookingsAndCommentsDto result = itemService.getItemById(item4.getId(), user1.getId());

        assertNotNull(result);
        assertEquals(item4FullInfoDto.getId(), result.getId());
        assertEquals(item4FullInfoDto.getName(), result.getName());
        assertEquals(item4FullInfoDto.getDescription(), result.getDescription());
        assertEquals(item4FullInfoDto.getAvailable(), result.getAvailable());

        assertNull(result.getLastBooking());
        assertNull(result.getNextBooking());

        assertNotNull(result.getComments());
        assertEquals(2, result.getComments().size());

        assertEquals(item4FullInfoDto.getComments().get(0).getId(), result.getComments().get(0).getId());
        assertEquals(item4FullInfoDto.getComments().get(0).getText(), result.getComments().get(0).getText());
        assertEquals(item4FullInfoDto.getComments().get(0).getAuthorName(), result.getComments().get(0).getAuthorName());
        assertEquals(item4FullInfoDto.getComments().get(0).getCreated(), result.getComments().get(0).getCreated());

        assertEquals(item4FullInfoDto.getComments().get(1).getId(), result.getComments().get(1).getId());
        assertEquals(item4FullInfoDto.getComments().get(1).getText(), result.getComments().get(1).getText());
        assertEquals(item4FullInfoDto.getComments().get(1).getAuthorName(), result.getComments().get(1).getAuthorName());
        assertEquals(item4FullInfoDto.getComments().get(1).getCreated(), result.getComments().get(1).getCreated());


        verify(userRepository, times(1)).findById(any(Long.class));
        verify(itemRepository, times(1)).findById(any(Long.class));
        verify(bookingRepository, times(1)).findAllByItem_IdInAndStartBeforeAndEndAfter(anyList(), any(LocalDateTime.class), any(LocalDateTime.class), any(PageRequest.class));
        verify(bookingRepository, times(0)).findAllByItem_IdInAndStatusInAndEndBefore(anyList(), anyList(), any(LocalDateTime.class), any(PageRequest.class));
        verify(bookingRepository, times(0)).findAllByItem_IdInAndStartAfter(anyList(), any(LocalDateTime.class), any(PageRequest.class));
        verify(commentRepository, times(1)).findAllByItem_IdOrderByCreatedDesc(any(Long.class));
    }

    @Test
    void testGetExceptionOnGetItemByUnknownUserId() {
        when(userRepository.findById(any(Long.class)))
                .thenReturn(Optional.empty());

        when(itemRepository.findById(any(Long.class)))
                .thenReturn(Optional.of(item4));

        when(bookingRepository.findAllByItem_IdInAndStartBeforeAndEndAfter(anyList(), any(LocalDateTime.class), any(LocalDateTime.class), any(PageRequest.class)))
                .thenReturn(List.of(lastBooking, nextBooking));

        when(commentRepository.findAllByItem_IdOrderByCreatedDesc(item4.getId()))
                .thenReturn(List.of(comment1, comment2));

        when(bookingRepository.findAllByItem_IdInAndStatusInAndEndBefore(anyList(), anyList(), any(LocalDateTime.class), any(PageRequest.class)))
                .thenReturn(List.of(lastBooking));

        when(bookingRepository.findAllByItem_IdInAndStartAfter(anyList(), any(LocalDateTime.class), any(PageRequest.class)))
                .thenReturn(List.of(nextBooking));

        final UserUnknownException exception = assertThrows(
                UserUnknownException.class,
                () -> itemService.getItemById(item4.getId(), user1.getId())
        );

        final String expectedMessage = String.format("Пользователь с %d не найден.", user1.getId());

        assertEquals(expectedMessage, exception.getMessage());

        verify(userRepository, times(1)).findById(any(Long.class));
        verify(itemRepository, times(0)).findById(any(Long.class));
        verify(bookingRepository, times(0)).findAllByItem_IdInAndStartBeforeAndEndAfter(anyList(), any(LocalDateTime.class), any(LocalDateTime.class), any(PageRequest.class));
        verify(bookingRepository, times(0)).findAllByItem_IdInAndStatusInAndEndBefore(anyList(), anyList(), any(LocalDateTime.class), any(PageRequest.class));
        verify(bookingRepository, times(0)).findAllByItem_IdInAndStartAfter(anyList(), any(LocalDateTime.class), any(PageRequest.class));
        verify(commentRepository, times(0)).findAllByItem_IdOrderByCreatedDesc(any(Long.class));
    }

    @Test
    void testGetExceptionOnGetItemByUnknownItemId() {
        when(userRepository.findById(any(Long.class)))
                .thenReturn(Optional.of(user1));

        when(itemRepository.findById(any(Long.class)))
                .thenReturn(Optional.empty());

        when(bookingRepository.findAllByItem_IdInAndStartBeforeAndEndAfter(anyList(), any(LocalDateTime.class), any(LocalDateTime.class), any(PageRequest.class)))
                .thenReturn(List.of(lastBooking, nextBooking));

        when(commentRepository.findAllByItem_IdOrderByCreatedDesc(item4.getId()))
                .thenReturn(List.of(comment1, comment2));

        when(bookingRepository.findAllByItem_IdInAndStatusInAndEndBefore(anyList(), anyList(), any(LocalDateTime.class), any(PageRequest.class)))
                .thenReturn(List.of(lastBooking));

        when(bookingRepository.findAllByItem_IdInAndStartAfter(anyList(), any(LocalDateTime.class), any(PageRequest.class)))
                .thenReturn(List.of(nextBooking));

        final ItemUnknownException exception = assertThrows(
                ItemUnknownException.class,
                () -> itemService.getItemById(item4.getId(), user1.getId())
        );

        final String expectedMessage = String.format("Не найдена вещь с id = %d", item4.getId());

        assertEquals(expectedMessage, exception.getMessage());

        verify(userRepository, times(1)).findById(any(Long.class));
        verify(itemRepository, times(1)).findById(any(Long.class));
        verify(bookingRepository, times(0)).findAllByItem_IdInAndStartBeforeAndEndAfter(anyList(), any(LocalDateTime.class), any(LocalDateTime.class), any(PageRequest.class));
        verify(bookingRepository, times(0)).findAllByItem_IdInAndStatusInAndEndBefore(anyList(), anyList(), any(LocalDateTime.class), any(PageRequest.class));
        verify(bookingRepository, times(0)).findAllByItem_IdInAndStartAfter(anyList(), any(LocalDateTime.class), any(PageRequest.class));
        verify(commentRepository, times(0)).findAllByItem_IdOrderByCreatedDesc(any(Long.class));
    }

    @Test
    void testSuccessGetAllItemsForUser() {
        when(userRepository.findById(any(Long.class)))
                .thenReturn(Optional.of(user3));

        when(itemRepository.findItemsByOwnerId(any(Long.class), any(PageRequest.class)))
                .thenReturn(List.of(item4));

        when(commentRepository.findAllByItem_IdOrderByCreatedDesc(item4.getId()))
                .thenReturn(List.of(comment1, comment2));

        when(bookingRepository.findAllByItem_IdInAndStatusInAndEndBefore(anyList(), anyList(), any(LocalDateTime.class), any(PageRequest.class)))
                .thenReturn(List.of(lastBooking));

        when(bookingRepository.findAllByItem_IdInAndStartAfter(anyList(), any(LocalDateTime.class), any(PageRequest.class)))
                .thenReturn(List.of(nextBooking));

        final List<ItemWithBookingsAndCommentsDto> result = itemService.getAllItemsForUser(user3.getId(), from, size);

        assertNotNull(result);
        assertEquals(1, result.size());

        assertEquals(item4FullInfoDto.getId(), result.get(0).getId());
        assertEquals(item4FullInfoDto.getName(), result.get(0).getName());
        assertEquals(item4FullInfoDto.getDescription(), result.get(0).getDescription());
        assertEquals(item4FullInfoDto.getAvailable(), result.get(0).getAvailable());

        assertNotNull(result.get(0).getLastBooking());
        assertEquals(item4FullInfoDto.getLastBooking().getId(), result.get(0).getLastBooking().getId());
        assertEquals(item4FullInfoDto.getLastBooking().getStart(), result.get(0).getLastBooking().getStart());
        assertEquals(item4FullInfoDto.getLastBooking().getEnd(), result.get(0).getLastBooking().getEnd());
        assertEquals(item4FullInfoDto.getLastBooking().getBookerId(), result.get(0).getLastBooking().getBookerId());

        assertNotNull(result.get(0).getNextBooking());
        assertEquals(item4FullInfoDto.getNextBooking().getId(), result.get(0).getNextBooking().getId());
        assertEquals(item4FullInfoDto.getNextBooking().getStart(), result.get(0).getNextBooking().getStart());
        assertEquals(item4FullInfoDto.getNextBooking().getEnd(), result.get(0).getNextBooking().getEnd());
        assertEquals(item4FullInfoDto.getNextBooking().getBookerId(), result.get(0).getNextBooking().getBookerId());

        assertNotNull(result.get(0).getComments());
        assertEquals(2, result.get(0).getComments().size());

        assertEquals(item4FullInfoDto.getComments().get(0).getId(), result.get(0).getComments().get(0).getId());
        assertEquals(item4FullInfoDto.getComments().get(0).getText(), result.get(0).getComments().get(0).getText());
        assertEquals(item4FullInfoDto.getComments().get(0).getAuthorName(), result.get(0).getComments().get(0).getAuthorName());
        assertEquals(item4FullInfoDto.getComments().get(0).getCreated(), result.get(0).getComments().get(0).getCreated());

        assertEquals(item4FullInfoDto.getComments().get(1).getId(), result.get(0).getComments().get(1).getId());
        assertEquals(item4FullInfoDto.getComments().get(1).getText(), result.get(0).getComments().get(1).getText());
        assertEquals(item4FullInfoDto.getComments().get(1).getAuthorName(), result.get(0).getComments().get(1).getAuthorName());
        assertEquals(item4FullInfoDto.getComments().get(1).getCreated(), result.get(0).getComments().get(1).getCreated());


        verify(userRepository, times(1)).findById(any(Long.class));
        verify(itemRepository, times(1)).findItemsByOwnerId(any(Long.class), any(PageRequest.class));
        verify(bookingRepository, times(1)).findAllByItem_IdInAndStatusInAndEndBefore(anyList(), anyList(), any(LocalDateTime.class), any(PageRequest.class));
        verify(bookingRepository, times(1)).findAllByItem_IdInAndStartAfter(anyList(), any(LocalDateTime.class), any(PageRequest.class));
        verify(commentRepository, times(1)).findAllByItem_IdOrderByCreatedDesc(any(Long.class));
    }

    @Test
    void testGetExceptionGetAllItemsForUserByUnknownUserId() {
        when(userRepository.findById(any(Long.class)))
                .thenReturn(Optional.empty());

        final UserUnknownException exception = assertThrows(
                UserUnknownException.class,
                () -> itemService.getItemById(item4.getId(), user1.getId())
        );

        final String expectedMessage = String.format("Пользователь с %d не найден.", user1.getId());

        assertEquals(expectedMessage, exception.getMessage());

        verify(userRepository, times(1)).findById(any(Long.class));
        verify(itemRepository, times(0)).findItemsByOwnerId(any(Long.class), any(PageRequest.class));
        verify(bookingRepository, times(0)).findAllByItem_IdInAndStatusInAndEndBefore(anyList(), anyList(), any(LocalDateTime.class), any(PageRequest.class));
        verify(bookingRepository, times(0)).findAllByItem_IdInAndStartAfter(anyList(), any(LocalDateTime.class), any(PageRequest.class));
        verify(commentRepository, times(0)).findAllByItem_IdOrderByCreatedDesc(any(Long.class));
    }

    @Test
    void testSuccessGetEmptyListItemsWithUnknownKeyWord() {
        when(itemRepository.findItemsByKeyWord(any(String.class), any(PageRequest.class)))
                .thenReturn(Collections.emptyList());

        List<ItemDto> result = itemService.getItemsWithKeyWord(unknownKeyWord, from, size);

        assertNotNull(result);
        assertEquals(0, result.size());

        verify(itemRepository, times(1)).findItemsByKeyWord(any(String.class), any(PageRequest.class));
    }

    @Test
    void testSuccessGetEmptyListItemsWithNullOrBlankKeyWord() {
        when(itemRepository.findItemsByKeyWord(any(String.class), any(PageRequest.class)))
                .thenReturn(Collections.emptyList());

        List<ItemDto> result = itemService.getItemsWithKeyWord(nullKeyWord, from, size);

        assertNotNull(result);
        assertEquals(0, result.size());

        List<ItemDto> result2 = itemService.getItemsWithKeyWord(blankKeyWord, from, size);

        assertNotNull(result2);
        assertEquals(0, result2.size());

        verify(itemRepository, times(0)).findItemsByKeyWord(any(String.class), any(PageRequest.class));
    }

    @Test
    void testSuccessListItemsWithKeyWord() {
        when(itemRepository.findItemsByKeyWord(any(String.class), any(PageRequest.class)))
                .thenReturn(List.of(item1, item2, item3));

        List<ItemDto> result = itemService.getItemsWithKeyWord(keyWord, from, size);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(item1Dto.getId(), result.get(0).getId());
        assertEquals(item1Dto.getName(), result.get(0).getName());
        assertEquals(item1Dto.getDescription(), result.get(0).getDescription());
        assertEquals(item1Dto.getAvailable(), result.get(0).getAvailable());
        assertEquals(item1Dto.getRequestId(), result.get(0).getRequestId());

        assertEquals(item3Dto.getId(), result.get(1).getId());
        assertEquals(item3Dto.getName(), result.get(1).getName());
        assertEquals(item3Dto.getDescription(), result.get(1).getDescription());
        assertEquals(item3Dto.getAvailable(), result.get(1).getAvailable());
        assertEquals(item3Dto.getRequestId(), result.get(1).getRequestId());

        verify(itemRepository, times(1)).findItemsByKeyWord(any(String.class), any(PageRequest.class));
    }

    @Test
    void testSuccessAddNewCommentByItemId() {
        when(userRepository.findById(any(Long.class)))
                .thenReturn(Optional.of(user1));

        when(itemRepository.findById(any(Long.class)))
                .thenReturn(Optional.of(item4));

        when(bookingRepository.findBookingByBooker_IdAndItem_IdAndEndBefore(any(Long.class), any(Long.class), any(LocalDateTime.class)))
                .thenReturn(List.of(new Booking()));

        when(commentRepository.save(any(Comment.class)))
                .thenReturn(comment1);

        final CommentDto commentDtoResult = itemService.addNewCommentByItemId(item4.getId(), commentDto1, user1.getId());

        assertNotNull(commentDtoResult);
        assertEquals(commentDto1.getId(), commentDtoResult.getId());
        assertEquals(commentDto1.getText(), commentDtoResult.getText());
        assertEquals(commentDto1.getAuthorName(), commentDtoResult.getAuthorName());
        assertEquals(commentDto1.getCreated(), commentDtoResult.getCreated());

        verify(userRepository,times(1)).findById(any(Long.class));
        verify(itemRepository,times(1)).findById(any(Long.class));
        verify(bookingRepository,times(1)).findBookingByBooker_IdAndItem_IdAndEndBefore(any(Long.class), any(Long.class), any(LocalDateTime.class));
        verify(commentRepository,times(1)).save(any(Comment.class));
    }

    @Test
    void testGetExceptionOnAddNewCommentByItemIdWithUnknownUserId() {
        when(userRepository.findById(any(Long.class)))
                .thenReturn(Optional.empty());

        final UserUnknownException exception = assertThrows(
                UserUnknownException.class,
                () -> itemService.addNewCommentByItemId(item4.getId(), commentDto1, user1.getId())
        );

        final String expectedMessage = String.format("Пользователь с %d не найден.", user1.getId());

        assertEquals(expectedMessage, exception.getMessage());

        verify(userRepository,times(1)).findById(any(Long.class));
        verify(itemRepository,times(0)).findById(any(Long.class));
        verify(bookingRepository,times(0)).findBookingByBooker_IdAndItem_IdAndEndBefore(any(Long.class), any(Long.class), any(LocalDateTime.class));
        verify(commentRepository,times(0)).save(any(Comment.class));
    }

    @Test
    void testGetExceptionOnAddNewCommentByUnknownItemId() {
        when(userRepository.findById(any(Long.class)))
                .thenReturn(Optional.of(user1));

        when(itemRepository.findById(any(Long.class)))
                .thenReturn(Optional.empty());

        final ItemUnknownException exception = assertThrows(
                ItemUnknownException.class,
                () -> itemService.addNewCommentByItemId(item4.getId(), commentDto1, user1.getId())
        );

        final String expectedMessage = String.format("Не найдена вещь с id = %d", item4.getId());

        assertEquals(expectedMessage, exception.getMessage());

        verify(userRepository,times(1)).findById(any(Long.class));
        verify(itemRepository,times(1)).findById(any(Long.class));
        verify(bookingRepository,times(0)).findBookingByBooker_IdAndItem_IdAndEndBefore(any(Long.class), any(Long.class), any(LocalDateTime.class));
        verify(commentRepository,times(0)).save(any(Comment.class));
    }

    @Test
    void testGetExceptionOnAddNewCommentByItemIdWithAnotherUser() {
        when(userRepository.findById(any(Long.class)))
                .thenReturn(Optional.of(user1));

        when(itemRepository.findById(any(Long.class)))
                .thenReturn(Optional.of(item4));

        when(bookingRepository.findBookingByBooker_IdAndItem_IdAndEndBefore(any(Long.class), any(Long.class), any(LocalDateTime.class)))
                .thenReturn(Collections.emptyList());

        final CommentForNotExistBookingException exception = assertThrows(
                CommentForNotExistBookingException.class,
                () -> itemService.addNewCommentByItemId(item4.getId(), commentDto1, user1.getId())
        );

        final String expectedMessage = String.format("Пользователь с id = %s не брал в аренду вещь с id = %s",user1.getId(), item4.getId());

        assertEquals(expectedMessage, exception.getMessage());

        verify(userRepository,times(1)).findById(any(Long.class));
        verify(itemRepository,times(1)).findById(any(Long.class));
        verify(bookingRepository,times(1)).findBookingByBooker_IdAndItem_IdAndEndBefore(any(Long.class), any(Long.class), any(LocalDateTime.class));
        verify(commentRepository,times(0)).save(any(Comment.class));
    }
}