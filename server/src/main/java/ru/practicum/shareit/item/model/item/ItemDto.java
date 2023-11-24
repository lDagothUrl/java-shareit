package ru.practicum.shareit.item.model.item;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import ru.practicum.shareit.booking.model.BookingDtoDefault;
import ru.practicum.shareit.item.model.comment.CommentDto;

import java.util.List;

@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@ToString
public class ItemDto {
    private Integer id;
    private String name;
    private String description;
    @JsonProperty("available")
    private Boolean isAvailable;
    private Integer requestId;
    private BookingDtoDefault lastBooking;
    private BookingDtoDefault nextBooking;
    private List<CommentDto> comments;
}
