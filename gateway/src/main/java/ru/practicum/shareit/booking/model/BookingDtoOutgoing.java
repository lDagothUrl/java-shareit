package ru.practicum.shareit.booking.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.practicum.shareit.item.model.ItemDto;
import ru.practicum.shareit.user.model.UserDto;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@AllArgsConstructor
public class BookingDtoOutgoing {
    private final Integer id;
    private final LocalDateTime start;
    private final LocalDateTime end;
    private final ItemDto item;
    private final UserDto booker;
    private final BookingStatus status;
}