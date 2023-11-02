package ru.practicum.shareit.user.service;

import lombok.extern.slf4j.Slf4j;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.MemoryUser;

import java.util.List;

@Slf4j
public class ServiceUser {
    private final MemoryUser memoryUser = new MemoryUser();

    private int newId;

    public User postUser(User user) {
        log.info("Create new User: \n{}", user);
        checkDuplicate(user);
        user.setId(createId());
        return memoryUser.postUser(user);
    }

    public List<User> getUsers() {
        log.info("Get all Users");
        return memoryUser.getUsers();
    }

    public User getUser(int id) {
        log.info("Get userId: {}", id);
        return memoryUser.getUser(id);
    }

    public User putUser(int id, User user) {
        log.info("Put User userId: {}", id);
        checkEmail(user, id);
        User userMap = memoryUser.getUser(id);
        if (user.getName() == null) {
            user.setName(userMap.getName());
        }
        if (user.getEmail() == null) {
            user.setEmail(userMap.getEmail());
        }
        user.setId(id);
        return memoryUser.putUser(user);
    }

    public User delUser(int id) {
        log.info("Delete userId: {}", id);
        return memoryUser.delUser(id);
    }

    private int createId() {
        return ++newId;
    }

    private void checkDuplicate(User user) {
        List<User> userList = memoryUser.getUsers();
        if (userList.size() != 0) {
            for (User userCheck : userList) {
                if (userCheck.equals(user)) {
                    throw new RuntimeException("The user already exists");
                }
            }
        }
    }

    private void checkEmail(User user, int id) {
        List<User> userList = memoryUser.getUsers();
        if (userList.size() != 0) {
            for (User userCheck : userList) {
                if (id != userCheck.getId() && userCheck.getEmail().equals(user.getEmail())) {
                    throw new RuntimeException("The userEmail already exists");
                }
            }
        }
    }
}
