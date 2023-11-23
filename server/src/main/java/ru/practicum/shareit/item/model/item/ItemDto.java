package ru.practicum.shareit.item.model.item;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import ru.practicum.shareit.booking.model.BookingDtoDefault;
import ru.practicum.shareit.item.model.comment.CommentDto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@ToString
public class ItemDto {
    private Integer id;
    @NotBlank
    private String name;
    @NotBlank
    private String description;
    @JsonProperty("available")
    @NotNull
    private Boolean isAvailable;
    private Integer requestId;
    private BookingDtoDefault lastBooking;
    private BookingDtoDefault nextBooking;
    private List<CommentDto> comments;
}
