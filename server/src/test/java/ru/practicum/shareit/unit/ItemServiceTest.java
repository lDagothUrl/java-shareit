package ru.practicum.shareit.unit;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingDtoDefault;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.MemoryBooking;
import ru.practicum.shareit.exception.model.*;
import ru.practicum.shareit.item.model.comment.Comment;
import ru.practicum.shareit.item.model.comment.CommentDto;
import ru.practicum.shareit.item.model.item.Item;
import ru.practicum.shareit.item.model.item.ItemDto;
import ru.practicum.shareit.item.repository.MemoryComment;
import ru.practicum.shareit.item.repository.MemoryItem;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.request.repository.MemoryRequest;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.MemoryUser;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static ru.practicum.shareit.booking.model.BookingMapper.bookingFromDto;
import static ru.practicum.shareit.booking.model.BookingStatus.*;
import static ru.practicum.shareit.item.model.comment.CommentMapper.commentToDto;
import static ru.practicum.shareit.item.model.item.ItemMapper.itemToDto;

@ExtendWith(MockitoExtension.class)
public class ItemServiceTest {
    @Mock
    private MemoryItem memoryItem;
    @Mock
    private MemoryUser memoryUser;
    @Mock
    private MemoryBooking memoryBooking;
    @Mock
    private MemoryComment memoryComment;
    @Mock
    private MemoryRequest memoryRequest;
    @InjectMocks
    private ItemServiceImpl itemService;

    private User user;
    private Item item;
    private Comment comment;
    private Booking booking;
    private Request request;

    @BeforeEach
    public void setUp() {
        user = new User(1, "user1", "user1@email.com");
        item = new Item(1, "Test item", "Test item description", true, user, null);
        comment = new Comment(1, "Test comment", item, user,
                LocalDateTime.now().plusMinutes(5));
        booking = new Booking(
                1,
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2),
                item,
                new User(2, "user2", "user2@email.com"),
                BookingStatus.APPROVED
        );
        request = new Request(
                1,
                "Test item request",
                new User(3, "user3", "user3@email.com"),
                LocalDateTime.now()
        );
        item.setRequest(request);
    }

    @Test
    public void shouldGetItemById() {
        Mockito
                .when(memoryItem.findById(anyInt()))
                .thenReturn(Optional.of(item));
        Mockito
                .when(memoryBooking.findFirstByItemIdAndStatusNotAndStartBeforeOrderByStartDesc(
                        anyInt(),
                        any(BookingStatus.class),
                        any(LocalDateTime.class)
                ))
                .thenReturn(null);
        Mockito
                .when(memoryBooking.findFirstByItemIdAndStatusNotAndStartAfterOrderByStartAsc(
                        anyInt(),
                        any(BookingStatus.class),
                        any(LocalDateTime.class)
                ))
                .thenReturn(booking);
        Mockito
                .when(memoryComment.findByItemIdOrderByCreatedDesc(anyInt()))
                .thenReturn(List.of(comment));

        ItemDto itemDtoOutgoing = itemService.getItem(1, 1);

        assertThat(itemDtoOutgoing.getId(), equalTo(item.getId()));
        assertThat(itemDtoOutgoing.getName(), equalTo(item.getName()));
        assertThat(itemDtoOutgoing.getDescription(), equalTo(item.getDescription()));
        assertThat(itemDtoOutgoing.getIsAvailable(), equalTo(item.getIsAvailable()));
        assertThat(itemDtoOutgoing.getRequestId(), equalTo(item.getRequest().getId()));
        assertThat(itemDtoOutgoing.getLastBooking(), nullValue());
        assertThat(itemDtoOutgoing.getNextBooking().getId(), equalTo(booking.getId()));
        assertThat(itemDtoOutgoing.getComments().size(), equalTo(1));
        assertThat(itemDtoOutgoing.getComments().get(0).getId(), equalTo(comment.getId()));
    }

    @Test
    public void shouldNotGetItemByIdWhenItemNotFound() {
        Mockito
                .when(memoryItem.findById(anyInt()))
                .thenReturn(Optional.empty());

        NotFoundException e = Assertions.assertThrows(
                NotFoundException.class,
                () -> itemService.getItem(1, 1)
        );

        assertThat(e.getMessage(), equalTo("Not found itemId: 1"));
    }

    @Test
    public void shouldGetItemsByUserId() {
        Mockito
                .when(memoryUser.existsById(anyInt()))
                .thenReturn(true);
        Mockito
                .when(memoryItem.findByOwnerId(anyInt(), any(Pageable.class)))
                .thenReturn(List.of(item));
        Mockito
                .when(memoryBooking.findAllByItemOwnerIdInAndStatusNotOrderByStart(
                        List.of(1), REJECTED
                ))
                .thenReturn(List.of());
        Mockito
                .when(memoryComment.findByItemIdOrderByCreatedDesc(anyInt()))
                .thenReturn(List.of(comment));

        List<ItemDto> items = itemService.getItems(1, 0, 5);
        ItemDto itemDtoOutgoing = items.get(0);
        Mockito
                .when(memoryBooking.findAllByItemOwnerIdInAndStatusNotOrderByStart(
                        List.of(1), REJECTED
                ))
                .thenReturn(List.of(booking, booking, booking));
        itemService.getItems(1, 0, 5);

        assertThat(items.size(), equalTo(1));
        assertThat(itemDtoOutgoing.getId(), equalTo(item.getId()));
        assertThat(itemDtoOutgoing.getName(), equalTo(item.getName()));
        assertThat(itemDtoOutgoing.getDescription(), equalTo(item.getDescription()));
        assertThat(itemDtoOutgoing.getIsAvailable(), equalTo(item.getIsAvailable()));
        assertThat(itemDtoOutgoing.getRequestId(), equalTo(item.getRequest().getId()));
        assertThat(itemDtoOutgoing.getLastBooking(), nullValue());
        assertThat(itemDtoOutgoing.getComments().size(), equalTo(1));
        assertThat(itemDtoOutgoing.getComments().get(0).getId(), equalTo(comment.getId()));
    }

    @Test
    public void shouldNotGetItemsByUserIdWhenUserNotFound() {
        Mockito
                .when(memoryUser.existsById(anyInt()))
                .thenReturn(false);

        NotFoundUserException e = Assertions.assertThrows(
                NotFoundUserException.class,
                () -> itemService.getItems(1, 0, 5)
        );

        assertThat(e.getMessage(), equalTo("Not found userId: 1"));
    }

    @Test
    public void shouldAddItem() {
        Mockito
                .when(memoryUser.findById(anyInt()))
                .thenReturn(Optional.of(user));
        Mockito
                .when(memoryRequest.findById(anyInt()))
                .thenReturn(Optional.of(request));
        Mockito
                .when(memoryItem.save(any(Item.class)))
                .then(returnsFirstArg());

        ItemDto itemDtoOutgoing = itemService.postItem(itemToDto(item, null, null, null), 1);

        assertThat(itemDtoOutgoing.getId(), equalTo(item.getId()));
        assertThat(itemDtoOutgoing.getName(), equalTo(item.getName()));
        assertThat(itemDtoOutgoing.getDescription(), equalTo(item.getDescription()));
        assertThat(itemDtoOutgoing.getIsAvailable(), equalTo(item.getIsAvailable()));
        assertThat(itemDtoOutgoing.getRequestId(), equalTo(item.getRequest().getId()));
        assertThat(itemDtoOutgoing.getLastBooking(), nullValue());
        assertThat(itemDtoOutgoing.getNextBooking(), nullValue());
        assertThat(itemDtoOutgoing.getComments(), nullValue());
    }

    @Test
    public void shouldNotAddItemWhenUserNotFound() {
        Mockito
                .when(memoryUser.findById(anyInt()))
                .thenReturn(Optional.empty());

        NotFoundUserException e = Assertions.assertThrows(
                NotFoundUserException.class,
                () -> itemService.postItem(itemToDto(item, null, null, null), 1)
        );

        assertThat(e.getMessage(), equalTo("Not found userId: 1"));
    }

    @Test
    public void shouldNotAddItemWhenItemRequestNotFound() {
        Mockito
                .when(memoryUser.findById(anyInt()))
                .thenReturn(Optional.of(user));
        Mockito
                .when(memoryRequest.findById(anyInt()))
                .thenReturn(Optional.empty());

        NotFoundException e = Assertions.assertThrows(
                NotFoundException.class,
                () -> itemService.postItem(itemToDto(item, null, null, null), 1)
        );

        assertThat(e.getMessage(), equalTo("Not found request requestId: 1"));
    }

    @Test
    public void shouldUpdateItem() {
        Mockito
                .when(memoryItem.findById(anyInt()))
                .thenReturn(Optional.of(item));
        Mockito
                .when(memoryRequest.findById(anyInt()))
                .thenReturn(Optional.of(request));
        Mockito
                .when(memoryItem.save(any(Item.class)))
                .then(returnsFirstArg());

        ItemDto itemDtoOutgoing = itemService.putItem(1, itemToDto(new Item(1, null, null, null, user, request), null, null, null), 1);

        assertThat(itemDtoOutgoing.getId(), equalTo(item.getId()));
        assertThat(itemDtoOutgoing.getName(), equalTo(item.getName()));
        assertThat(itemDtoOutgoing.getDescription(), equalTo(item.getDescription()));
        assertThat(itemDtoOutgoing.getIsAvailable(), equalTo(item.getIsAvailable()));
        assertThat(itemDtoOutgoing.getRequestId(), equalTo(item.getRequest().getId()));
        assertThat(itemDtoOutgoing.getLastBooking(), nullValue());
        assertThat(itemDtoOutgoing.getNextBooking(), nullValue());
        assertThat(itemDtoOutgoing.getComments(), nullValue());
    }

    @Test
    public void shouldNotUpdateItemWhenItemNotFound() {
        Mockito
                .when(memoryItem.findById(anyInt()))
                .thenReturn(Optional.empty());

        NotFoundException e = Assertions.assertThrows(
                NotFoundException.class,
                () -> itemService.putItem(1, itemToDto(item, null, null, null), 1)
        );

        assertThat(e.getMessage(), equalTo("Not found itemId: 1"));
    }

    @Test
    public void shouldNotUpdateItemWhenUserNotOwner() {
        Mockito
                .when(memoryItem.findById(anyInt()))
                .thenReturn(Optional.of(item));
        NotFoundException e = Assertions.assertThrows(
                NotFoundException.class,
                () -> itemService.putItem(2, itemToDto(item, null, null, null), 1)
        );

        assertThat(e.getMessage(), equalTo("Not found request requestId: 1"));
    }

    @Test
    public void shouldNotUpdateItemWhenItemRequestNotFound() {
        Mockito
                .when(memoryItem.findById(anyInt()))
                .thenReturn(Optional.of(item));
        Mockito
                .when(memoryRequest.findById(anyInt()))
                .thenReturn(Optional.empty());

        NotFoundException e = Assertions.assertThrows(
                NotFoundException.class,
                () -> itemService.putItem(1, itemToDto(item, null, null, null), 1)
        );

        assertThat(e.getMessage(), equalTo("Not found request requestId: 1"));
    }

    @Test
    public void shouldNotUpdateOwnerItemException() {
        Mockito
                .when(memoryItem.findById(anyInt()))
                .thenReturn(Optional.of(item));

        OwnerItemException e = Assertions.assertThrows(
                OwnerItemException.class,
                () -> itemService.putItem(1, itemToDto(item, null, null, null), 2)
        );

        assertThat(e.getMessage(), equalTo("No access userId: 2 itemId: 1"));
    }

    @Test
    public void shouldDeleteItem() {
        Mockito
                .when(memoryUser.existsById(anyInt()))
                .thenReturn(true);

        itemService.deleteItem(1, 1);

        Mockito.verify(memoryItem).deleteById(anyInt());
    }

    @Test
    public void shouldNotDeleteItemWhenUserNotFound() {
        Mockito
                .when(memoryUser.existsById(anyInt()))
                .thenReturn(false);

        NotFoundUserException e = Assertions.assertThrows(
                NotFoundUserException.class,
                () -> itemService.deleteItem(1, 1)
        );

        assertThat(e.getMessage(), equalTo("Not found userId: 1"));
    }

    @Test
    public void shouldFindItems() {
        Mockito
                .when(memoryItem.findByText(any(String.class), any(Pageable.class)))
                .thenReturn(List.of(item));

        List<ItemDto> items = itemService.getItem("Test", 0, 5);
        ItemDto itemDtoOutgoing = items.get(0);

        assertThat(itemDtoOutgoing.getId(), equalTo(item.getId()));
        assertThat(itemDtoOutgoing.getName(), equalTo(item.getName()));
        assertThat(itemDtoOutgoing.getDescription(), equalTo(item.getDescription()));
        assertThat(itemDtoOutgoing.getIsAvailable(), equalTo(item.getIsAvailable()));
        assertThat(itemDtoOutgoing.getRequestId(), equalTo(item.getRequest().getId()));
        assertThat(itemDtoOutgoing.getLastBooking(), nullValue());
        assertThat(itemDtoOutgoing.getNextBooking(), nullValue());
        assertThat(itemDtoOutgoing.getComments(), nullValue());
    }

    @Test
    public void shouldNotFindItemsWhenBlankText() {
        List<ItemDto> items = itemService.getItem("", 0, 5);

        assertThat(0, equalTo(items.size()));
    }

    @Test
    public void shouldAddComment() {
        Mockito
                .when(memoryUser.findById(anyInt()))
                .thenReturn(Optional.of(user));
        Mockito
                .when(memoryItem.findById(anyInt()))
                .thenReturn(Optional.of(item));
        Mockito
                .when(
                        memoryBooking.existsByBookerIdAndItemIdAndEndBefore(
                                anyInt(),
                                anyInt(),
                                any(LocalDateTime.class)
                        )
                )
                .thenReturn(true);
        Mockito
                .when(
                        memoryBooking.findByBookerIdAndItemId(
                                anyInt(),
                                anyInt())
                )
                .thenReturn(List.of(
                        bookingFromDto(new BookingDtoDefault(
                                1,
                                LocalDateTime.now().plusDays(1),
                                LocalDateTime.now().plusDays(2),
                                1,
                                1,
                                APPROVED
                        ), user, item)
                ));
        Mockito
                .when(memoryComment.save(any(Comment.class)))
                .thenReturn(comment);

        CommentDto commentDtoOutgoing = itemService.postComment(
                1,
                1,
                commentToDto(comment, user.getName())
        );

        assertThat(commentDtoOutgoing.getId(), equalTo(comment.getId()));
        assertThat(commentDtoOutgoing.getText(), equalTo(comment.getText()));
        assertThat(commentDtoOutgoing.getAuthorName(), equalTo(comment.getAuthor().getName()));
        assertThat(commentDtoOutgoing.getCreated(), equalTo(comment.getCreated()));
    }

    @Test
    public void shouldNotAddCommentWhenUserNotFound() {
        Mockito
                .when(memoryUser.findById(anyInt()))
                .thenReturn(Optional.empty());

        NotFoundUserException e = Assertions.assertThrows(
                NotFoundUserException.class,
                () -> itemService.postComment(1, 1, commentToDto(comment, user.getName()))
        );

        assertThat(e.getMessage(), equalTo("Not found userId: 1"));
    }

    @Test
    public void shouldNotAddCommentWhenItemNotFound() {
        Mockito
                .when(memoryUser.findById(anyInt()))
                .thenReturn(Optional.of(user));
        Mockito
                .when(memoryItem.findById(anyInt()))
                .thenReturn(Optional.empty());

        NotFoundItemException e = Assertions.assertThrows(
                NotFoundItemException.class,
                () -> itemService.postComment(1, 1, commentToDto(comment, user.getName()))
        );

        assertThat(e.getMessage(), equalTo("Not found itemId: 1"));
    }

    @Test
    public void shouldNotAddCommentWhenBookingNotEnded() {
        Mockito
                .when(memoryUser.findById(anyInt()))
                .thenReturn(Optional.of(user));
        Mockito
                .when(memoryItem.findById(anyInt()))
                .thenReturn(Optional.of(item));
        Mockito
                .when(
                        memoryBooking.existsByBookerIdAndItemIdAndEndBefore(
                                anyInt(),
                                anyInt(),
                                any(LocalDateTime.class)
                        )
                )
                .thenReturn(false);
        Mockito
                .when(
                        memoryBooking.findByBookerIdAndItemId(
                                anyInt(),
                                anyInt())
                )
                .thenReturn(List.of(
                        bookingFromDto(new BookingDtoDefault(
                                1,
                                LocalDateTime.now().plusDays(1),
                                LocalDateTime.now().plusDays(2),
                                1,
                                1,
                                APPROVED
                        ), user, item)
                ));

        BookingTimeException e = Assertions.assertThrows(
                BookingTimeException.class,
                () -> itemService.postComment(1, 1, commentToDto(comment, user.getName()))
        );

        assertThat(e.getMessage(), equalTo("Error booking"));
    }
}