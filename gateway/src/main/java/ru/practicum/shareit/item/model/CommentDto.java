package ru.practicum.shareit.item.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@AllArgsConstructor
public class CommentDto {
    private final Integer id;
    @NotBlank
    private final String text;
    private final String authorName;
    private final LocalDateTime created;
}