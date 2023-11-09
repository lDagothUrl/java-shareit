package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.model.BookingDtoDefault;
import ru.practicum.shareit.booking.model.BookingDtoOutgoing;

import java.util.List;

public interface BookingService {
    BookingDtoOutgoing postBooking(BookingDtoDefault bookingDtoDefault, int id);

    BookingDtoOutgoing putBooking(int userId, int bookingId, boolean approved);

    BookingDtoOutgoing getBooking(int userId, int bookingId);

    List<BookingDtoOutgoing> getUserBookings(int userId, String stateString);

    List<BookingDtoOutgoing> getOwnerBookings(int userId, String stateString);
}
