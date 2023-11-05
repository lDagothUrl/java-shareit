package ru.practicum.shareit.user.repository;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class MemoryUser {
    private static final Map<Integer, User> userMap = new HashMap<>();

    public User postUser(User user) {
        userMap.put(user.getId(), user);
        return user;
    }

    public List<User> getUsers() {
        return new ArrayList<>(userMap.values());
    }

    public User getUser(int id) {
        return userMap.get(id);
    }

    public User putUser(User user) {
        userMap.put(user.getId(), user);
        return user;
    }

    public User delUser(int id) {
        return userMap.remove(id);
    }
}
