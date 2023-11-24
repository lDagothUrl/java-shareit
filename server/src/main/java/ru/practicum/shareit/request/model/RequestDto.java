package ru.practicum.shareit.request.model;

/**
 * TODO Sprint add-item-requests.
 */

import lombok.*;
import ru.practicum.shareit.item.model.item.ItemDto;

import java.time.LocalDateTime;
import java.util.List;


@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@ToString
public class RequestDto {
    private Integer id;
    private String description;
    private LocalDateTime created;
    private List<ItemDto> items;
}
