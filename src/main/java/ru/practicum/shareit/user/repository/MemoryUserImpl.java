package ru.practicum.shareit.user.repository;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.model.UserDto;
import ru.practicum.shareit.user.model.UserMapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class MemoryUserImpl implements MemoryUser {
    private final Map<Integer, User> userMap = new HashMap<>();
    private int newId;

    public User postUser(UserDto userDto) {
        userDto.setId(createId());
        User user = UserMapper.dtoToUser(userDto);
        userMap.put(userDto.getId(), user);
        return user;
    }

    public List<User> getUsers() {
        return new ArrayList<>(userMap.values());
    }

    public User getUser(int id) {
        return userMap.get(id);
    }

    public User putUser(int id, UserDto userDto) {
        userDto.setId(id);
        User user = UserMapper.dtoToUser(userDto);
        userMap.put(userDto.getId(), user);
        return user;
    }

    public User delUser(int id) {
        return userMap.remove(id);
    }

    private int createId() {
        return ++newId;
    }
}
