package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.model.UserDto;

import java.util.List;

public interface UserService {
    UserDto postUser(UserDto user);

    List<UserDto> getUsers();

    UserDto getUser(int id);

    UserDto putUser(int id, UserDto user);

    void delUser(int id);
}
