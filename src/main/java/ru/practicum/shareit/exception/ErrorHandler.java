package ru.practicum.shareit.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.shareit.exception.model.BadRequest;
import ru.practicum.shareit.exception.model.ErrorResponse;
import ru.practicum.shareit.exception.model.NotFoundException;

@RestControllerAdvice
@Slf4j
public class ErrorHandler {
//    @ExceptionHandler
//    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
//    public ErrorResponse handlerThrowable(final Throwable e) {
//        log.error("Error", e);
//        return new ErrorResponse(e.getMessage());
//    }

    //    @ExceptionHandler(value = {EqualsUsersIdException.class, ExceptionBlockedUserFriend.class, ExceptionAlreadyInFriends.class})
//    @ResponseStatus(HttpStatus.CONFLICT)
//    public ErrorResponse handlerEqualsUsersIdException(final RuntimeException e) {
//        log.error("Error", e.getMessage());
//        return new ErrorResponse(e.getMessage());
//    }
//
    @ExceptionHandler(value = {BadRequest.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handlerMethodArgumentNotValidationException(final BadRequest e) {
        log.error("Validation {}", e.getMessage());
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handlerNotFoundException(final NotFoundException e) {
        log.error("Not found exception {}", e.getMessage());
        return new ErrorResponse(e.getMessage());
    }
}