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
import ru.practicum.shareit.booking.model.BookingDtoOutgoing;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.MemoryBooking;
import ru.practicum.shareit.booking.service.BookingServiceImpl;
import ru.practicum.shareit.exception.model.*;
import ru.practicum.shareit.item.model.item.Item;
import ru.practicum.shareit.item.repository.MemoryItem;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.MemoryUser;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static ru.practicum.shareit.booking.model.BookingMapper.bookingFromDto;
import static ru.practicum.shareit.booking.model.BookingStatus.REJECTED;
import static ru.practicum.shareit.booking.model.BookingStatus.WAITING;

@ExtendWith(MockitoExtension.class)
public class BookingServiceTest {
    @Mock
    private MemoryBooking memoryBooking;
    @Mock
    private MemoryUser memoryUser;
    @Mock
    private MemoryItem memoryItem;
    @InjectMocks
    private BookingServiceImpl bookingService;

    private User user;
    private Item item;
    private BookingDtoDefault bookingDtoDefault;

    @BeforeEach
    public void setUp() {
        user = new User(1, "user1", "user1@email.com");

        item = new Item(
                1,
                "Item1",
                "Test item 1",
                true,
                new User(2, "user2", "user2@email.com"),
                null
        );

        bookingDtoDefault = new BookingDtoDefault(
                1,
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2),
                1,
                1,
                WAITING
        );
    }

    @Test
    public void shouldAddBooking() {
        Mockito
                .when(memoryUser.findById(anyInt()))
                .thenReturn(Optional.of(user));
        Mockito
                .when(memoryItem.findById(anyInt()))
                .thenReturn(Optional.of(item));
        Mockito
                .when(memoryBooking.save(any(Booking.class)))
                .then(returnsFirstArg());

        BookingDtoOutgoing bookingDtoOutgoing = bookingService.postBooking(bookingDtoDefault, bookingDtoDefault.getBookerId());

        assertThat(bookingDtoOutgoing.getId(), equalTo(bookingDtoDefault.getId()));
        assertThat(bookingDtoOutgoing.getStart(), equalTo(bookingDtoDefault.getStart()));
        assertThat(bookingDtoOutgoing.getEnd(), equalTo(bookingDtoDefault.getEnd()));
        assertThat(bookingDtoOutgoing.getItem().getId(), equalTo(bookingDtoDefault.getItemId()));
        assertThat(bookingDtoOutgoing.getBooker().getId(), equalTo(bookingDtoDefault.getBookerId()));
        assertThat(bookingDtoOutgoing.getStatus(), equalTo(bookingDtoDefault.getStatus()));
    }

    @Test
    public void shouldNotAddBookingWhenUserNotFound() {
        Mockito
                .when(memoryUser.findById(anyInt()))
                .thenReturn(Optional.empty());

        NotFoundUserException e = Assertions.assertThrows(
                NotFoundUserException.class,
                () -> bookingService.postBooking(bookingDtoDefault, bookingDtoDefault.getBookerId())
        );

        assertThat(e.getMessage(), equalTo("Not found userId: 1"));
    }

    @Test
    public void shouldNotAddBookingWhenItemNotFound() {
        Mockito
                .when(memoryUser.findById(anyInt()))
                .thenReturn(Optional.of(user));
        Mockito
                .when(memoryItem.findById(anyInt()))
                .thenReturn(Optional.empty());

        NotFoundItemException e = Assertions.assertThrows(
                NotFoundItemException.class,
                () -> bookingService.postBooking(bookingDtoDefault, bookingDtoDefault.getBookerId())
        );

        assertThat(e.getMessage(), equalTo("Not found itemId: 1"));
    }

    @Test
    public void shouldNotAddBookingWhenOwner() {
        Mockito
                .when(memoryUser.findById(anyInt()))
                .thenReturn(Optional.of(user));

        item = new Item(
                1,
                "Item1",
                "Test item 1",
                true,
                user,
                null
        );
        Mockito
                .when(memoryItem.findById(anyInt()))
                .thenReturn(Optional.of(item));

        NotFoundException e = Assertions.assertThrows(
                NotFoundException.class,
                () -> bookingService.postBooking(bookingDtoDefault, bookingDtoDefault.getBookerId())
        );

        assertThat(e.getMessage(), equalTo("This is your thing"));
    }

    @Test
    public void shouldNotAddBookingWhenItemUnavailable() {
        Mockito
                .when(memoryUser.findById(anyInt()))
                .thenReturn(Optional.of(user));

        item = new Item(
                1,
                "Item1",
                "Test item 1",
                false,
                new User(2, "user2", "user2@email.com"),
                null
        );
        Mockito
                .when(memoryItem.findById(anyInt()))
                .thenReturn(Optional.of(item));

        NoAccessException e = Assertions.assertThrows(
                NoAccessException.class,
                () -> bookingService.postBooking(bookingDtoDefault, bookingDtoDefault.getBookerId())
        );

        assertThat(e.getMessage(), equalTo("No access itemId: 1"));
    }

    @Test
    public void shouldNotAddBookingWhenEndBeforeStart() {
        Mockito
                .when(memoryUser.findById(anyInt()))
                .thenReturn(Optional.of(user));
        Mockito
                .when(memoryItem.findById(anyInt()))
                .thenReturn(Optional.of(item));
        bookingDtoDefault = new BookingDtoDefault(
                1,
                LocalDateTime.now().plusDays(2),
                LocalDateTime.now().plusDays(1),
                1,
                1,
                WAITING
        );

        BookingTimeException e = Assertions.assertThrows(
                BookingTimeException.class,
                () -> bookingService.postBooking(bookingDtoDefault, bookingDtoDefault.getBookerId())
        );

        assertThat(e.getMessage(), equalTo("The end of the booking is later than the beginning"));
    }

    @Test
    public void shouldApproveBooking() {
        Mockito
                .when(memoryBooking.findByIdAndItemOwnerId(anyInt(), anyInt()))
                .thenReturn(Optional.of(
                        bookingFromDto(bookingDtoDefault, user, item)
                ));
        Mockito
                .when(memoryBooking.save(any(Booking.class)))
                .then(returnsFirstArg());

        BookingDtoOutgoing bookingDtoOutgoing = bookingService.putBooking(2, 1, true);

        assertThat(bookingDtoOutgoing.getId(), equalTo(bookingDtoDefault.getId()));
        assertThat(bookingDtoOutgoing.getStart(), equalTo(bookingDtoDefault.getStart()));
        assertThat(bookingDtoOutgoing.getEnd(), equalTo(bookingDtoDefault.getEnd()));
        assertThat(bookingDtoOutgoing.getItem().getId(), equalTo(bookingDtoDefault.getItemId()));
        assertThat(bookingDtoOutgoing.getBooker().getId(), equalTo(bookingDtoDefault.getBookerId()));
        assertThat(bookingDtoOutgoing.getStatus(), equalTo(BookingStatus.APPROVED));
    }

    @Test
    public void shouldRejectBooking() {
        Mockito
                .when(memoryBooking.findByIdAndItemOwnerId(anyInt(), anyInt()))
                .thenReturn(Optional.of(
                        bookingFromDto(bookingDtoDefault, user, item)
                ));
        Mockito
                .when(memoryBooking.save(any(Booking.class)))
                .then(returnsFirstArg());

        BookingDtoOutgoing bookingDtoOutgoing = bookingService.putBooking(2, 1, false);

        assertThat(bookingDtoOutgoing.getId(), equalTo(bookingDtoDefault.getId()));
        assertThat(bookingDtoOutgoing.getStart(), equalTo(bookingDtoDefault.getStart()));
        assertThat(bookingDtoOutgoing.getEnd(), equalTo(bookingDtoDefault.getEnd()));
        assertThat(bookingDtoOutgoing.getItem().getId(), equalTo(bookingDtoDefault.getItemId()));
        assertThat(bookingDtoOutgoing.getBooker().getId(), equalTo(bookingDtoDefault.getBookerId()));
        assertThat(bookingDtoOutgoing.getStatus(), equalTo(REJECTED));
    }

    @Test
    public void shouldNotApproveBookingWhenBookingNotFound() {
        Mockito
                .when(memoryBooking.findByIdAndItemOwnerId(anyInt(), anyInt()))
                .thenReturn(Optional.empty());

        NotFoundException e = Assertions.assertThrows(
                NotFoundException.class,
                () -> bookingService.putBooking(1, 1, true)
        );

        assertThat(e.getMessage(), equalTo("Not found bookingId: 1"));
    }

    @Test
    public void shouldNotApproveBookingWhenBookingStatusNotWaiting() {
        bookingDtoDefault = new BookingDtoDefault(
                1,
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2),
                1,
                1,
                BookingStatus.CANCELED
        );
        Mockito
                .when(memoryBooking.findByIdAndItemOwnerId(anyInt(), anyInt()))
                .thenReturn(Optional.of(
                        bookingFromDto(bookingDtoDefault, user, item)
                ));

        BadRequestException e = Assertions.assertThrows(
                BadRequestException.class,
                () -> bookingService.putBooking(2, 1, true)
        );

        assertThat(e.getMessage(), equalTo("BookingStatus: WAITING"));
    }

    @Test
    public void shouldGetById() {
        Mockito
                .when(memoryUser.existsById(anyInt()))
                .thenReturn(true);
        Mockito
                .when(memoryBooking.findById(anyInt()))
                .thenReturn(Optional.of(
                        bookingFromDto(bookingDtoDefault, user, item)
                ));

        BookingDtoOutgoing bookingDtoOutgoing = bookingService.getBooking(1, 1);

        assertThat(bookingDtoOutgoing.getId(), equalTo(bookingDtoDefault.getId()));
        assertThat(bookingDtoOutgoing.getStart(), equalTo(bookingDtoDefault.getStart()));
        assertThat(bookingDtoOutgoing.getEnd(), equalTo(bookingDtoDefault.getEnd()));
        assertThat(bookingDtoOutgoing.getItem().getId(), equalTo(bookingDtoDefault.getItemId()));
        assertThat(bookingDtoOutgoing.getBooker().getId(), equalTo(bookingDtoDefault.getBookerId()));
        assertThat(bookingDtoOutgoing.getStatus(), equalTo(bookingDtoDefault.getStatus()));
    }

    @Test
    public void shouldNotGetByIdWhenUserNotFound() {
        Mockito
                .when(memoryUser.existsById(anyInt()))
                .thenReturn(false);

        NotFoundException e = Assertions.assertThrows(
                NotFoundException.class,
                () -> bookingService.getBooking(1, 1)
        );

        assertThat(e.getMessage(), equalTo("Not found userId: 1"));
    }

    @Test
    public void shouldNotGetByIdWhenBookingNotFound() {
        Mockito
                .when(memoryUser.existsById(anyInt()))
                .thenReturn(true);
        Mockito
                .when(memoryBooking.findById(anyInt()))
                .thenReturn(Optional.empty());

        NotFoundException e = Assertions.assertThrows(
                NotFoundException.class,
                () -> bookingService.getBooking(1, 1)
        );

        assertThat(e.getMessage(), equalTo("Not found bookingId: 1"));
    }

    @Test
    public void shouldNotGetByIdWhenUserNotOwnerOrBooker() {
        Mockito
                .when(memoryUser.existsById(anyInt()))
                .thenReturn(true);
        Mockito
                .when(memoryBooking.findById(anyInt()))
                .thenReturn(Optional.of(
                        bookingFromDto(bookingDtoDefault, user, item)
                ));

        NotFoundException e = Assertions.assertThrows(
                NotFoundException.class,
                () -> bookingService.getBooking(3, 1)
        );

        assertThat(e.getMessage(), equalTo("Not found bookingId: 1 userId: 3"));
    }

    @Test
    public void shouldGetAllUserBookings() {
        Mockito
                .when(memoryUser.existsById(anyInt()))
                .thenReturn(true);
        Mockito
                .when(
                        memoryBooking.findByBookerIdOrderByStartDesc(
                                anyInt(),
                                any(Pageable.class))
                )
                .thenReturn(List.of(
                        bookingFromDto(bookingDtoDefault, user, item)
                ));

        List<BookingDtoOutgoing> bookings = bookingService.getUserBookings(1, "ALL", 0, 5);
        BookingDtoOutgoing bookingDtoOutgoing = bookings.get(0);

        assertThat(bookings.size(), equalTo(1));
        assertThat(bookingDtoOutgoing.getId(), equalTo(bookingDtoDefault.getId()));
        assertThat(bookingDtoOutgoing.getStart(), equalTo(bookingDtoDefault.getStart()));
        assertThat(bookingDtoOutgoing.getEnd(), equalTo(bookingDtoDefault.getEnd()));
        assertThat(bookingDtoOutgoing.getItem().getId(), equalTo(bookingDtoDefault.getItemId()));
        assertThat(bookingDtoOutgoing.getBooker().getId(), equalTo(bookingDtoDefault.getBookerId()));
        assertThat(bookingDtoOutgoing.getStatus(), equalTo(bookingDtoDefault.getStatus()));
    }

    @Test
    public void shouldGetWaitingUserBookings() {
        Mockito
                .when(memoryUser.existsById(anyInt()))
                .thenReturn(true);
        Mockito
                .when(
                        memoryBooking.findByBookerIdAndStatusOrderByStartDesc(
                                anyInt(),
                                any(BookingStatus.class),
                                any(Pageable.class))
                )
                .thenReturn(List.of(
                        bookingFromDto(bookingDtoDefault, user, item)
                ));

        List<BookingDtoOutgoing> bookings = bookingService.getUserBookings(1, "WAITING", 0, 5);
        BookingDtoOutgoing bookingDtoOutgoing = bookings.get(0);

        assertThat(bookings.size(), equalTo(1));
        assertThat(bookingDtoOutgoing.getId(), equalTo(bookingDtoDefault.getId()));
        assertThat(bookingDtoOutgoing.getStart(), equalTo(bookingDtoDefault.getStart()));
        assertThat(bookingDtoOutgoing.getEnd(), equalTo(bookingDtoDefault.getEnd()));
        assertThat(bookingDtoOutgoing.getItem().getId(), equalTo(bookingDtoDefault.getItemId()));
        assertThat(bookingDtoOutgoing.getBooker().getId(), equalTo(bookingDtoDefault.getBookerId()));
        assertThat(bookingDtoOutgoing.getStatus(), equalTo(bookingDtoDefault.getStatus()));
    }

    @Test
    public void shouldGetFutureUserBookings() {
        Mockito
                .when(memoryUser.existsById(anyInt()))
                .thenReturn(true);
        Mockito
                .when(
                        memoryBooking.findByBookerIdAndStartAfterOrderByStartDesc(
                                anyInt(),
                                any(LocalDateTime.class),
                                any(Pageable.class))
                )
                .thenReturn(List.of(
                        bookingFromDto(bookingDtoDefault, user, item)
                ));

        List<BookingDtoOutgoing> bookings = bookingService.getUserBookings(1, "FUTURE", 0, 5);
        bookingService.getUserBookings(2, "CURRENT", 0, 5);
        bookingService.getUserBookings(2, "PAST", 0, 5);
        bookingService.getUserBookings(2, "REJECTED", 0, 5);
        BookingDtoOutgoing bookingDtoOutgoing = bookings.get(0);

        assertThat(bookings.size(), equalTo(1));
        assertThat(bookingDtoOutgoing.getId(), equalTo(bookingDtoDefault.getId()));
        assertThat(bookingDtoOutgoing.getStart(), equalTo(bookingDtoDefault.getStart()));
        assertThat(bookingDtoOutgoing.getEnd(), equalTo(bookingDtoDefault.getEnd()));
        assertThat(bookingDtoOutgoing.getItem().getId(), equalTo(bookingDtoDefault.getItemId()));
        assertThat(bookingDtoOutgoing.getBooker().getId(), equalTo(bookingDtoDefault.getBookerId()));
        assertThat(bookingDtoOutgoing.getStatus(), equalTo(bookingDtoDefault.getStatus()));
    }

    @Test
    public void shouldNotGetUserBookingsWhenUnknownState() {
        BadRequestException e = Assertions.assertThrows(
                BadRequestException.class,
                () -> bookingService.getUserBookings(1, "Test", 0, 5)
        );

        assertThat(e.getMessage(), equalTo("Unknown state: Test"));
    }

    @Test
    public void shouldNotGetUserBookingsWhenUserNotFound() {
        Mockito
                .when(memoryUser.existsById(anyInt()))
                .thenReturn(false);

        NotFoundException e = Assertions.assertThrows(
                NotFoundException.class,
                () -> bookingService.getUserBookings(1, "ALL", 0, 5)
        );

        assertThat(e.getMessage(), equalTo("Not found userId: 1"));
    }

    @Test
    public void shouldGetAllOwnerBookings() {
        Mockito
                .when(memoryUser.existsById(anyInt()))
                .thenReturn(true);
        Mockito
                .when(
                        memoryBooking.findByItemOwnerIdOrderByStartDesc(
                                anyInt(),
                                any(Pageable.class))
                )
                .thenReturn(List.of(
                        bookingFromDto(bookingDtoDefault, user, item)
                ));

        List<BookingDtoOutgoing> bookings = bookingService.getOwnerBookings(2, "ALL", 0, 5);
        bookingService.getOwnerBookings(2, "CURRENT", 0, 5);
        bookingService.getOwnerBookings(2, "PAST", 0, 5);
        bookingService.getOwnerBookings(2, "REJECTED", 0, 5);
        BookingDtoOutgoing bookingDtoOutgoing = bookings.get(0);

        assertThat(bookings.size(), equalTo(1));
        assertThat(bookingDtoOutgoing.getId(), equalTo(bookingDtoDefault.getId()));
        assertThat(bookingDtoOutgoing.getStart(), equalTo(bookingDtoDefault.getStart()));
        assertThat(bookingDtoOutgoing.getEnd(), equalTo(bookingDtoDefault.getEnd()));
        assertThat(bookingDtoOutgoing.getItem().getId(), equalTo(bookingDtoDefault.getItemId()));
        assertThat(bookingDtoOutgoing.getBooker().getId(), equalTo(bookingDtoDefault.getBookerId()));
        assertThat(bookingDtoOutgoing.getStatus(), equalTo(bookingDtoDefault.getStatus()));
    }

    @Test
    public void shouldGetWaitingOwnerBookings() {
        Mockito
                .when(memoryUser.existsById(anyInt()))
                .thenReturn(true);
        Mockito
                .when(
                        memoryBooking.findByItemOwnerIdAndStatusOrderByStartDesc(
                                anyInt(),
                                any(BookingStatus.class),
                                any(Pageable.class))
                )
                .thenReturn(List.of(
                        bookingFromDto(bookingDtoDefault, user, item)
                ));

        List<BookingDtoOutgoing> bookings = bookingService.getOwnerBookings(1, "WAITING", 0, 5);
        BookingDtoOutgoing bookingDtoOutgoing = bookings.get(0);

        assertThat(bookings.size(), equalTo(1));
        assertThat(bookingDtoOutgoing.getId(), equalTo(bookingDtoDefault.getId()));
        assertThat(bookingDtoOutgoing.getStart(), equalTo(bookingDtoDefault.getStart()));
        assertThat(bookingDtoOutgoing.getEnd(), equalTo(bookingDtoDefault.getEnd()));
        assertThat(bookingDtoOutgoing.getItem().getId(), equalTo(bookingDtoDefault.getItemId()));
        assertThat(bookingDtoOutgoing.getBooker().getId(), equalTo(bookingDtoDefault.getBookerId()));
        assertThat(bookingDtoOutgoing.getStatus(), equalTo(bookingDtoDefault.getStatus()));
    }

    @Test
    public void shouldGetFutureOwnerBookings() {
        Mockito
                .when(memoryUser.existsById(anyInt()))
                .thenReturn(true);
        Mockito
                .when(
                        memoryBooking.findByItemOwnerIdAndStartAfterOrderByStartDesc(
                                anyInt(),
                                any(LocalDateTime.class),
                                any(Pageable.class))
                )
                .thenReturn(List.of(
                        bookingFromDto(bookingDtoDefault, user, item)
                ));

        List<BookingDtoOutgoing> bookings = bookingService.getOwnerBookings(1, "FUTURE", 0, 5);
        BookingDtoOutgoing bookingDtoOutgoing = bookings.get(0);

        assertThat(bookings.size(), equalTo(1));
        assertThat(bookingDtoOutgoing.getId(), equalTo(bookingDtoDefault.getId()));
        assertThat(bookingDtoOutgoing.getStart(), equalTo(bookingDtoDefault.getStart()));
        assertThat(bookingDtoOutgoing.getEnd(), equalTo(bookingDtoDefault.getEnd()));
        assertThat(bookingDtoOutgoing.getItem().getId(), equalTo(bookingDtoDefault.getItemId()));
        assertThat(bookingDtoOutgoing.getBooker().getId(), equalTo(bookingDtoDefault.getBookerId()));
        assertThat(bookingDtoOutgoing.getStatus(), equalTo(bookingDtoDefault.getStatus()));
    }

    @Test
    public void shouldNotGetOwnerBookingsWhenUnknownState() {
        BadRequestException e = Assertions.assertThrows(
                BadRequestException.class,
                () -> bookingService.getOwnerBookings(1, "Test", 0, 5)
        );

        assertThat(e.getMessage(), equalTo("Unknown state: Test"));
    }

    @Test
    public void shouldNotGetOwnerBookingsWhenUserNotFound() {
        Mockito
                .when(memoryUser.existsById(anyInt()))
                .thenReturn(false);

        NotFoundException e = Assertions.assertThrows(
                NotFoundException.class,
                () -> bookingService.getOwnerBookings(1, "ALL", 0, 5)
        );

        assertThat(e.getMessage(), equalTo("Not found userId: 1"));
    }
}