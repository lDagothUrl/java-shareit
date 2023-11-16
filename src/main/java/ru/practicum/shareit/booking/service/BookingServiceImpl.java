package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
import java.util.stream.Collectors;

import static ru.practicum.shareit.booking.model.BookingMapper.bookingFromDto;
import static ru.practicum.shareit.booking.model.BookingMapper.bookingToDtoOutgoing;

@RequiredArgsConstructor
@Service
@Slf4j
@Transactional
public class BookingServiceImpl implements BookingService {
    private final MemoryBooking memoryBooking;
    private final MemoryUser memoryUser;
    private final MemoryItem memoryItem;

    @Override
    public BookingDtoOutgoing postBooking(BookingDtoDefault bookingDtoDefault, int userId) {
        log.info("Post booking: {} id: {}", bookingDtoDefault, userId);
        bookingDtoDefault.setBookerId(userId);
        bookingDtoDefault.setStatus(BookingStatus.WAITING);
        User user = memoryUser.findById(userId)
                .orElseThrow(() -> new NotFoundUserException("Not found userId: " + userId));
        int itemId = bookingDtoDefault.getItemId();
        Item item = memoryItem.findById(itemId)
                .orElseThrow(() -> new NotFoundItemException("Not found itemId: " + itemId));
        if (item.getOwner().getId() == userId) {
            throw new NotFoundException("This is your thing");
        }
        if (!item.getIsAvailable()) {
            throw new NoAccessException("No access itemId: " + itemId);
        }
        Booking bookingAfter = memoryBooking.findFirstByItemIdAndStatusNotAndStartAfterOrderByStartAsc(itemId, BookingStatus.APPROVED, bookingDtoDefault.getStart());
        Booking bookingBefore = memoryBooking.findFirstByItemIdAndStatusNotAndStartBeforeOrderByStartDesc(itemId, BookingStatus.APPROVED, bookingDtoDefault.getStart());
        if (!bookingDtoDefault.getEnd().isAfter(bookingDtoDefault.getStart()) || (intersectionBooking(bookingDtoDefault, bookingAfter) || intersectionBooking(bookingDtoDefault, bookingBefore))) {
            throw new BookingTimeException("The end of the booking is later than the beginning");
        }
        Booking booking = memoryBooking.save(bookingFromDto(bookingDtoDefault, user, item));
        return bookingToDtoOutgoing(booking);
    }

    @Override
    public BookingDtoOutgoing putBooking(int userId, int bookingId, boolean approved) {
        log.info("Put booking userId: {} bookingId: {} status: {}", userId, bookingId, approved);
        Booking booking = memoryBooking.findByIdAndItemOwnerId(bookingId, userId)
                .orElseThrow(() -> new NotFoundException("Not found bookingId: " + bookingId));
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
        if (!memoryUser.existsById(userId)) {
            throw new NotFoundException("Not found userId: " + userId);
        }
        Booking booking = memoryBooking.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Not found bookingId: " + bookingId));
        if (booking.getItem().getOwner().getId() != userId && booking.getBooker().getId() != userId) {
            throw new NotFoundException("Not found bookingId: " + bookingId + " userId: " + userId);
        }
        return bookingToDtoOutgoing(booking);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingDtoOutgoing> getUserBookings(int userId, String stateString, int from, int size) {
        log.info("Get user booking userId: {} status: {}", userId, stateString);
        BookingState state = BookingState.getBookingState(stateString);
        if (!memoryUser.existsById(userId)) {
            throw new NotFoundException("Not found userId: " + userId);
        }
        List<Booking> bookings;

        switch (state) {
            case FUTURE:
                bookings = memoryBooking.findByBookerIdAndStartAfterOrderByStartDesc(userId, LocalDateTime.now(), getPageable(from, size));
                break;
            case CURRENT:
                LocalDateTime now = LocalDateTime.now();
                bookings = memoryBooking.findByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(userId, now, now, getPageable(from, size));
                break;
            case PAST:
                bookings = memoryBooking.findByBookerIdAndEndBeforeOrderByStartDesc(userId, LocalDateTime.now(), getPageable(from, size));
                break;
            case WAITING:
                bookings = memoryBooking.findByBookerIdAndStatusOrderByStartDesc(userId, BookingStatus.WAITING, getPageable(from, size));
                break;
            case REJECTED:
                bookings = memoryBooking.findByBookerIdAndStatusOrderByStartDesc(userId, BookingStatus.REJECTED, getPageable(from, size));
                break;
            default:
                bookings = memoryBooking.findByBookerIdOrderByStartDesc(userId, getPageable(from, size));
        }

        return bookings.stream()
                .map(BookingMapper::bookingToDtoOutgoing)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingDtoOutgoing> getOwnerBookings(int userId, String stateString, int from, int size) {
        log.info("Get owner bookings userId: {} state: {}", userId, stateString);
        BookingState state = BookingState.getBookingState(stateString);
        if (!memoryUser.existsById(userId)) {
            throw new NotFoundException("Not found userId: " + userId);
        }
        List<Booking> bookings;
        switch (state) {
            case FUTURE:
                bookings = memoryBooking.findByItemOwnerIdAndStartAfterOrderByStartDesc(userId, LocalDateTime.now(), getPageable(from, size));
                break;
            case CURRENT:
                LocalDateTime now = LocalDateTime.now();
                bookings = memoryBooking.findByItemOwnerIdAndStartBeforeAndEndAfterOrderByStartDesc(userId, now, now, getPageable(from, size));
                break;
            case PAST:
                bookings = memoryBooking.findByItemOwnerIdAndEndBeforeOrderByStartDesc(userId, LocalDateTime.now(), getPageable(from, size));
                break;
            case WAITING:
                bookings = memoryBooking.findByItemOwnerIdAndStatusOrderByStartDesc(userId, BookingStatus.WAITING, getPageable(from, size));
                break;
            case REJECTED:
                bookings = memoryBooking.findByItemOwnerIdAndStatusOrderByStartDesc(userId, BookingStatus.REJECTED, getPageable(from, size));
                break;
            default:
                bookings = memoryBooking.findByItemOwnerIdOrderByStartDesc(userId, getPageable(from, size));
        }
        return bookings.stream()
                .map(BookingMapper::bookingToDtoOutgoing)
                .collect(Collectors.toList());
    }

    private boolean intersectionBooking(BookingDtoDefault bookingDtoDefault, Booking booking) {
        if (booking == null || booking.getStart() == null || booking.getEnd() == null) {
            return false;
        }
        return (booking.getStart().isAfter(bookingDtoDefault.getStart()) && booking.getStart().isBefore(bookingDtoDefault.getEnd()))
                || (booking.getEnd().isAfter(bookingDtoDefault.getStart()) && booking.getEnd().isBefore(bookingDtoDefault.getEnd()));
    }


    private Pageable getPageable(int from, int size) {
        int page = from / size;
        return PageRequest.of(page, size);
    }
}
