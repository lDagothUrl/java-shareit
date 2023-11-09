package ru.practicum.shareit.exception.model;

public class NotFoundItemException extends RuntimeException {
    public NotFoundItemException(String s) {
        super(s);
    }
}
