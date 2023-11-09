package ru.practicum.shareit.user.repository;

import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.model.UserDto;

import java.util.List;

public interface MemoryUser {

    User postUser(UserDto user);

    List<User> getUsers();

    User getUser(int id);

    User putUser(int id, UserDto user);

    User delUser(int id);
}
