package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.model.NotFoundException;
import ru.practicum.shareit.exception.model.ReplayException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.model.UserDto;
import ru.practicum.shareit.user.model.UserMapper;
import ru.practicum.shareit.user.repository.MemoryUser;

import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.shareit.user.model.UserMapper.userToDto;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {
    private final MemoryUser memoryUser;

    @Override
    public UserDto postUser(UserDto userDto) {
        log.info("Create new User: \n{}", userDto);
        try {
            User user = memoryUser.save(UserMapper.dtoToUser(userDto));
            return userToDto(user);
        } catch (DataIntegrityViolationException e) {
            throw new ReplayException("The userEmail already exists");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserDto> getUsers() {
        log.info("Get all Users");
        return memoryUser.findAll().stream().map(UserMapper::userToDto).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public UserDto getUser(int id) {
        log.info("Get userId: {}", id);
        User user = memoryUser.findById(id)
                .orElseThrow(() -> new NotFoundException("Not found user id: " + id));
        return UserMapper.userToDto(user);
    }

    @Override
    public UserDto putUser(int userId, UserDto userDto) {
        log.info("Put User userId: {}, user: ", userId, userDto);
        userDto.setId(userId);
        User oldUser = memoryUser.findById(userId)
                .orElseThrow(() -> new NotFoundException("Not found user id: " + userId));
        User updateUser = UserMapper.dtoToUser(userDto);
        try {
            String email = updateUser.getEmail();
            String name = updateUser.getName();
            if (email == null || email.isBlank()) {
                updateUser.setEmail(oldUser.getEmail());
            }
            if (name == null || name.isBlank()) {
                updateUser.setName(oldUser.getName());
            }
            User user = memoryUser.save(updateUser);
            return userToDto(user);
        } catch (DataIntegrityViolationException e) {
            throw new ReplayException("The userEmail already exists");
        }
    }

    @Override
    public void delUser(int id) {
        log.info("Delete userId: {}", id);
        memoryUser.deleteById(id);
    }
}
