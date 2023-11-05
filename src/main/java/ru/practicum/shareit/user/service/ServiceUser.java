package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.model.UserDto;
import ru.practicum.shareit.user.model.UserMapper;
import ru.practicum.shareit.user.repository.MemoryUser;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ServiceUser {
    private final MemoryUser memoryUser;

    private int newId;

    public UserDto postUser(User user) {
        log.info("Create new User: \n{}", user);
        checkDuplicate(user);
        user.setId(createId());
        return UserMapper.userToDto(memoryUser.postUser(user));
    }

    public List<UserDto> getUsers() {
        log.info("Get all Users");
        return memoryUser.getUsers().stream().map(UserMapper::userToDto).collect(Collectors.toList());
    }

    public UserDto getUser(int id) {
        log.info("Get userId: {}", id);
        return UserMapper.userToDto(memoryUser.getUser(id));
    }

    public UserDto putUser(int id, User user) {
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
        return UserMapper.userToDto(memoryUser.putUser(user));
    }

    public UserDto delUser(int id) {
        log.info("Delete userId: {}", id);
        return UserMapper.userToDto(memoryUser.delUser(id));
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

    private int createId() {
        return ++newId;
    }
}
