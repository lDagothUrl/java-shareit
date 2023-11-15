package ru.practicum.shareit.booking.model;

import ru.practicum.shareit.exception.model.BadRequestException;

public enum BookingState {
    ALL,
    CURRENT,
    PAST,
    FUTURE,
    WAITING,
    REJECTED;

    public static BookingState getBookingState(String stateString) {
        try {
            return BookingState.valueOf(stateString);
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Unknown state: " + stateString);
        }
    }
}
