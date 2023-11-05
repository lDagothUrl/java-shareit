package ru.practicum.shareit.item.model;

import lombok.*;

/**
 * TODO Sprint add-controllers.
 */

@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@ToString
@Builder
public class Item {
    private int id;
    private String name;
    private String description;
    private Boolean available;
    private Integer owner;
}
