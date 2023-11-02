package ru.practicum.shareit.user;

import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserMapperDto;

import javax.validation.Valid;
import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequestMapping(path = "/users")
public class UserController {

    private final UserMapperDto userMapperDto = new UserMapperDto();

    @PostMapping
    public User postUser(@Valid @RequestBody User user) {
        return userMapperDto.postUser(user);
    }

    @GetMapping
    public List<User> getUsers() {
        return userMapperDto.getUsers();
    }

    @GetMapping("/{id}")
    public User getUser(@PathVariable int id) {
        return userMapperDto.getUser(id);
    }

    @PatchMapping("/{id}")
    public User putUser(@PathVariable int id, @RequestBody User user) {
        return userMapperDto.putUser(id, user);
    }

    @DeleteMapping("/{id}")
    public User delUser(@PathVariable int id) {
        return userMapperDto.delUser(id);
    }

}
