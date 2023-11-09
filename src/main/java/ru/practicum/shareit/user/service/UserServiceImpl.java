package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.model.UserDto;
import ru.practicum.shareit.user.model.UserMapper;
import ru.practicum.shareit.user.repository.MemoryUser;
import ru.practicum.shareit.exception.model.ReplayException;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final MemoryUser memoryUser;

    public UserDto postUser(UserDto user) {
        log.info("Create new User: \n{}", user);
        checkDuplicate(user);
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

    public UserDto putUser(int id, UserDto user) {
        log.info("Put User userId: {}", id);
        for (User userCheck : memoryUser.getUsers()) {
            if (id != userCheck.getId() && userCheck.getEmail().equals(user.getEmail())) {
                throw new ReplayException("The userEmail already exists");
            }
        }
        User userMap = memoryUser.getUser(id);
        if (user.getName() == null) {
            user.setName(userMap.getName());
        }
        if (user.getEmail() == null) {
            user.setEmail(userMap.getEmail());
        }
        return UserMapper.userToDto(memoryUser.putUser(id, user));
    }

    public UserDto delUser(int id) {
        log.info("Delete userId: {}", id);
        return UserMapper.userToDto(memoryUser.delUser(id));
    }

    private void checkDuplicate(UserDto user) {
        for (User userCheck : memoryUser.getUsers()) {
            if (userCheck.getEmail().equals(user.getEmail()) && userCheck.getName().equals(user.getName())) {
                throw new ReplayException("The user already exists");
            }
        }
    }
}
