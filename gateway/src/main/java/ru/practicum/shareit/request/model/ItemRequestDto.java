package ru.practicum.shareit.request.model;

import lombok.*;
import ru.practicum.shareit.item.model.ItemDto;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@ToString
@AllArgsConstructor
public class ItemRequestDto {
    private final Integer id;
    @NotBlank
    private final String description;
    private final LocalDateTime created;
    private final List<ItemDto> items;
}