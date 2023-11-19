package ru.practicum.shareit.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.shareit.exception.model.*;

@RestControllerAdvice
@Slf4j
public class ErrorHandler {
    @ExceptionHandler(value = {BadRequestException.class, BookingTimeException.class, NoAccessException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handlerBadRequestException(final RuntimeException e) {
        log.error("Validation {}", e.getMessage(), e);
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler(value = {NotFoundUserException.class, NotFoundItemException.class, NotFoundException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handlerNotFoundException(final RuntimeException e) {
        log.error("Not found exception {}", e.getMessage(), e);
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handlerReplayException(final ReplayException e) {
        log.error("Replay exception {}", e.getMessage(), e);
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler({OwnerItemException.class})
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ErrorResponse handleOwnerItemException(RuntimeException e) {
        log.error(e.getMessage());
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONTINUE)
    public ErrorResponse handleOwnerItemException(NewEx e) {
        log.error(e.getMessage());
        return new ErrorResponse(e.getMessage());
    }
}