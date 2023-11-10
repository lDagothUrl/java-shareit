package ru.practicum.shareit.item.model.comment;

import lombok.*;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@AllArgsConstructor
@Data
public class CommentDto {
    private Integer id;
    @NotBlank
    private String text;
    private String authorName;
    private LocalDateTime created;
}
