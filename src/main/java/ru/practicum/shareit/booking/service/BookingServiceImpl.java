package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.model.*;
import ru.practicum.shareit.booking.repository.MemoryBooking;
import ru.practicum.shareit.exception.model.*;
import ru.practicum.shareit.item.model.item.Item;
import ru.practicum.shareit.item.repository.MemoryItem;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.MemoryUser;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static ru.practicum.shareit.booking.model.BookingMapper.bookingFromDto;
import static ru.practicum.shareit.booking.model.BookingMapper.bookingToDtoOutgoing;

@RequiredArgsConstructor
@Service
@Slf4j
public class BookingServiceImpl implements BookingService {
    private final MemoryBooking memoryBooking;
    private final MemoryUser memoryUser;
    private final MemoryItem memoryItem;

    @Override
    public BookingDtoOutgoing postBooking(BookingDtoDefault bookingDtoDefault, int userId) {
        log.info("Post booking: {} id: {}", bookingDtoDefault, userId);
        bookingDtoDefault.setBookerId(userId);
        bookingDtoDefault.setStatus(BookingStatus.WAITING);
        Optional<User> userOptional = memoryUser.findById(userId);
        if (userOptional.isEmpty()) {
            throw new NotFoundUserException("Not found userId: " + userId);
        }
        int itemId = bookingDtoDefault.getItemId();
        Optional<Item> itemOptional = memoryItem.findById(itemId);
        if (itemOptional.isEmpty()) {
            throw new NotFoundItemException("Not found itemId: " + itemId);
        }
        Item item = itemOptional.get();
        if (item.getOwner().getId() == userId) {
            throw new NotFoundException("This is your thing");
        }
        if (!item.getIsAvailable()) {
            throw new NoAccessException("No access itemId: " + itemId);
        }
        if (!bookingDtoDefault.getEnd().isAfter(bookingDtoDefault.getStart())) {
            throw new BookingTimeException("The end of the booking is later than the beginning");
        }
        Booking booking = memoryBooking.save(bookingFromDto(bookingDtoDefault, userOptional.get(), item));
        return bookingToDtoOutgoing(booking);
    }

    @Override
    public BookingDtoOutgoing putBooking(int userId, int bookingId, boolean approved) {
        log.info("Put booking userId: {} bookingId: {} status: {}", userId, bookingId, approved);
        Optional<Booking> bookingOptional = memoryBooking.findByIdAndItemOwnerId(bookingId, userId);
        if (bookingOptional.isEmpty())
            throw new NotFoundException("Not found bookingId: " + bookingId);
        Booking booking = bookingOptional.get();
        if (!booking.getStatus().equals(BookingStatus.WAITING)) {
            throw new BadRequestException("BookingStatus: WAITING");
        }
        if (approved) {
            booking.setStatus(BookingStatus.APPROVED);
        } else {
            booking.setStatus(BookingStatus.REJECTED);
        }
        return bookingToDtoOutgoing(memoryBooking.save(booking));
    }

    @Override
    @Transactional(readOnly = true)
    public BookingDtoOutgoing getBooking(int userId, int bookingId) {
        log.info("Get booking bookingId: {} userId {}", bookingId, userId);
        if (!memoryUser.existsById(userId))
            throw new NotFoundException("Not found userId: " + userId);
        Optional<Booking> bookingOptional = memoryBooking.findById(bookingId);
        if (bookingOptional.isEmpty())
            throw new NotFoundException("Not found bookingId: " + bookingId);
        Booking booking = bookingOptional.get();
        if (booking.getItem().getOwner().getId() != userId && booking.getBooker().getId() != userId)
            throw new NotFoundException("Not found bookingId: " + bookingId + " userId: " + userId);
        return bookingToDtoOutgoing(booking);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingDtoOutgoing> getUserBookings(int userId, String stateString) {
        log.info("Get user booking userId: {} status: {}", userId, stateString);
        BookingState state;
        try {
            state = BookingState.valueOf(stateString);
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Unknown state: " + stateString);
        }
        if (!memoryUser.existsById(userId))
            throw new NotFoundException("Not found userId: " + userId);
        List<Booking> bookings;

        switch (state) {
            case FUTURE:
                bookings = memoryBooking.findByBookerIdAndStartAfterOrderByStartDesc(userId, LocalDateTime.now());
                break;
            case CURRENT:
                LocalDateTime now = LocalDateTime.now();
                bookings = memoryBooking.findByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(userId, now, now);
                break;
            case PAST:
                bookings = memoryBooking.findByBookerIdAndEndBeforeOrderByStartDesc(userId, LocalDateTime.now());
                break;
            case WAITING:
                bookings = memoryBooking.findByBookerIdAndStatusOrderByStartDesc(userId, BookingStatus.WAITING);
                break;
            case REJECTED:
                bookings = memoryBooking.findByBookerIdAndStatusOrderByStartDesc(userId, BookingStatus.REJECTED);
                break;
            default:
                bookings = memoryBooking.findByBookerIdOrderByStartDesc(userId);
        }

        return bookings.stream()
                .map(BookingMapper::bookingToDtoOutgoing)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingDtoOutgoing> getOwnerBookings(int userId, String stateString) {
        log.info("Get owner bookings userId: {} state: {}", userId, stateString);
        BookingState state;
        try {
            state = BookingState.valueOf(stateString);
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Unknown state: UNSUPPORTED_STATUS");
        }
        if (!memoryUser.existsById(userId)) {
            throw new NotFoundException("Not found userId: " + userId);
        }
        List<Booking> bookings;
        switch (state) {
            case FUTURE:
                bookings = memoryBooking.findByItemOwnerIdAndStartAfterOrderByStartDesc(userId, LocalDateTime.now());
                break;
            case CURRENT:
                LocalDateTime now = LocalDateTime.now();
                bookings = memoryBooking.findByItemOwnerIdAndStartBeforeAndEndAfterOrderByStartDesc(userId, now, now);
                break;
            case PAST:
                bookings = memoryBooking.findByItemOwnerIdAndEndBeforeOrderByStartDesc(userId, LocalDateTime.now());
                break;
            case WAITING:
                bookings = memoryBooking.findByItemOwnerIdAndStatusOrderByStartDesc(userId, BookingStatus.WAITING);
                break;
            case REJECTED:
                bookings = memoryBooking.findByItemOwnerIdAndStatusOrderByStartDesc(userId, BookingStatus.REJECTED);
                break;
            default:
                bookings = memoryBooking.findByItemOwnerIdOrderByStartDesc(userId);
        }
        return bookings.stream()
                .map(BookingMapper::bookingToDtoOutgoing)
                .collect(Collectors.toList());
    }
}
