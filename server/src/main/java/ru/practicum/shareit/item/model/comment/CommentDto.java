package ru.practicum.shareit.item.model.comment;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@AllArgsConstructor
@Getter
@Setter
@ToString
public class CommentDto {
    private Integer id;
    private String text;
    private String authorName;
    private LocalDateTime created;
}
