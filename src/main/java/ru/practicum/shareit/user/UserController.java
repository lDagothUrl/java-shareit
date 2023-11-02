package ru.practicum.shareit.user;

import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.service.ServiceUser;
import ru.practicum.shareit.user.model.User;

import javax.validation.Valid;
import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequestMapping(path = "/users")
public class UserController {

    private final ServiceUser serviceUser = new ServiceUser();

    @PostMapping
    public User postUser(@Valid @RequestBody User user) {
        return serviceUser.postUser(user);
    }

    @GetMapping
    public List<User> getUsers() {
        return serviceUser.getUsers();
    }

    @GetMapping("/{id}")
    public User getUser(@PathVariable int id) {
        return serviceUser.getUser(id);
    }

    @PatchMapping("/{id}")
    public User putUser(@PathVariable int id, @RequestBody User user) {
        return serviceUser.putUser(id, user);
    }

    @DeleteMapping("/{id}")
    public User delUser(@PathVariable int id) {
        return serviceUser.delUser(id);
    }

}
