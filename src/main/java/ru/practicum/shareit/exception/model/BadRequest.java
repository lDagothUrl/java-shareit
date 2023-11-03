package ru.practicum.shareit.exception.model;

public class BadRequest extends RuntimeException {
    public BadRequest(String notAvailable) {
        super(notAvailable);
    }
}
