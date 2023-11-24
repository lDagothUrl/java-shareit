package ru.practicum.shareit.user.model;

import lombok.*;

@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@ToString
public class UserDto {
    private Integer id;
    private String name;
    private String email;
}