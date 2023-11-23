package ru.practicum.shareit.item.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.practicum.shareit.booking.model.BookingDtoDefault;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@Getter
@Setter
@ToString
@AllArgsConstructor
public class ItemDto {
    private Integer id;
    @NotBlank
    private final String name;
    @NotBlank
    private final String description;
    @JsonProperty("available")
    @NotNull
    private final Boolean isAvailable;
    private final Integer requestId;
    private final BookingDtoDefault lastBooking;
    private final BookingDtoDefault nextBooking;
    private final List<CommentDto> comments;
}