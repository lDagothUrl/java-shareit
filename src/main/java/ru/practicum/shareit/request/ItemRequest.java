package ru.practicum.shareit.request;

import lombok.*;
import ru.practicum.shareit.user.User;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDate;

/**
 * TODO Sprint add-item-requests.
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
public class ItemRequest {
    private int id;
    @Size(max = 200)
    private String description;
    private User requestor;
    @NotNull
    private LocalDate created;
}
