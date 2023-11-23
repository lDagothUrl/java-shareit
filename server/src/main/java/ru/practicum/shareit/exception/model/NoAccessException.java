package ru.practicum.shareit.exception.model;

public class NoAccessException extends RuntimeException {
    public NoAccessException(String s) {
        super(s);
    }
}
