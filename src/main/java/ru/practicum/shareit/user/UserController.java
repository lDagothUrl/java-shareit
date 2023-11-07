package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.model.UserDto;
import ru.practicum.shareit.user.service.UserService;

import javax.validation.Valid;
import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping
    public UserDto postUser(@Valid @RequestBody UserDto user) {
        return userService.postUser(user);
    }

    @GetMapping
    public List<UserDto> getUsers() {
        return userService.getUsers();
    }

    @GetMapping("/{id}")
    public UserDto getUser(@PathVariable int id) {
        return userService.getUser(id);
    }

    @PatchMapping("/{id}")
    public UserDto putUser(@PathVariable int id, @RequestBody UserDto user) {
        return userService.putUser(id, user);
    }

    @DeleteMapping("/{id}")
    public UserDto delUser(@PathVariable int id) {
        return userService.delUser(id);
    }

}
