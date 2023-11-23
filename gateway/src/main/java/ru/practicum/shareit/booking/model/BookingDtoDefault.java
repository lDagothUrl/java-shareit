package ru.practicum.shareit.booking.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.Future;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@AllArgsConstructor
public class BookingDtoDefault {
    private Integer id;
    @NotNull
    @Future
    private final LocalDateTime start;
    @NotNull
    @Future
    private final LocalDateTime end;
    @NotNull
    private final Integer itemId;
    private Integer bookerId;
    private BookingStatus status;
}