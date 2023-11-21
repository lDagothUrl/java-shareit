package ru.practicum.shareit.exception;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.exception.model.ErrorResponse;
import ru.practicum.shareit.exception.model.ReplayException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

class ErrorHandlerTest {
    private final ErrorHandler errorHandler = new ErrorHandler();

    @Test
    void handlerBadRequestException() {
        assertThat(new ErrorResponse("e").getError(), equalTo(errorHandler.handlerBadRequestException(new RuntimeException("e")).getError()));
    }

    @Test
    void handlerNotFoundException() {
        assertThat(new ErrorResponse("e").getError(), equalTo(errorHandler.handlerNotFoundException(new RuntimeException("e")).getError()));
    }

    @Test
    void handlerReplayException() {
        assertThat(new ErrorResponse("e").getError(), equalTo(errorHandler.handlerReplayException(new ReplayException("e")).getError()));
    }

    @Test
    void handleOwnerItemException() {
        assertThat(new ErrorResponse("e").getError(), equalTo(errorHandler.handleOwnerItemException(new RuntimeException("e")).getError()));
    }
}