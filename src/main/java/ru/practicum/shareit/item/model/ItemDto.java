package ru.practicum.shareit.item.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@ToString
public class ItemDto {
    private Integer id;
    @NotBlank
    private final String name;
    @NotBlank
    private final String description;
    @JsonProperty
    @NotNull
    private final Boolean available;
}
