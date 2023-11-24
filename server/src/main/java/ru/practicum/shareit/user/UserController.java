package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.model.UserDto;
import ru.practicum.shareit.user.service.UserService;

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
    public UserDto postUser(@RequestBody UserDto user) {
        return userService.postUser(user);
    }

    @GetMapping
    public List<UserDto> getUsers() {
        return userService.getUsers();
    }

    @GetMapping("/{id}")
    public UserDto getUser(@PathVariable Integer id) {
        return userService.getUser(id);
    }

    @PatchMapping("/{id}")
    public UserDto putUser(@PathVariable Integer id, @RequestBody UserDto user) {
        return userService.putUser(id, user);
    }

    @DeleteMapping("/{id}")
    public void delUser(@PathVariable Integer id) {
        userService.delUser(id);
    }

}
