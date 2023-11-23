package ru.practicum.shareit.exception.model;

public class BadRequestException extends RuntimeException {
    public BadRequestException(String notAvailable) {
        super(notAvailable);
    }
}
