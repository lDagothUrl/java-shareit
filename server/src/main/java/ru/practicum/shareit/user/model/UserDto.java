package ru.practicum.shareit.user.model;

import lombok.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@ToString
public class UserDto {
    private Integer id;
    @NotBlank
    private String name;
    @NotBlank
    @Email
    private String email;
}