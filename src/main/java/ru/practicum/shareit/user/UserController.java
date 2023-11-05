package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.model.UserDto;
import ru.practicum.shareit.user.service.ServiceUser;

import javax.validation.Valid;
import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
public class UserController {

    private final ServiceUser serviceUser;

    @PostMapping
    public UserDto postUser(@Valid @RequestBody User user) {
        return serviceUser.postUser(user);
    }

    @GetMapping
    public List<UserDto> getUsers() {
        return serviceUser.getUsers();
    }

    @GetMapping("/{id}")
    public UserDto getUser(@PathVariable int id) {
        return serviceUser.getUser(id);
    }

    @PatchMapping("/{id}")
    public UserDto putUser(@PathVariable int id, @RequestBody User user) {
        return serviceUser.putUser(id, user);
    }

    @DeleteMapping("/{id}")
    public UserDto delUser(@PathVariable int id) {
        return serviceUser.delUser(id);
    }

}
