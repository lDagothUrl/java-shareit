package ru.practicum.shareit.item;

import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequestMapping("/items")
public class ItemController {
    private final ItemMapper userMapper = new ItemMapper();

    @PostMapping
    public Item postItem(@RequestBody Item item, @NotNull @RequestHeader("X-Sharer-User-Id") Integer owner) {
        return userMapper.postItem(item, owner);
    }

    @GetMapping
    public List<Item> getItems(@NotNull @RequestHeader("X-Sharer-User-Id") Integer owner) {
        return userMapper.getItems(owner);
    }

    @GetMapping("/{itemId}")
    public Item getItem(@PathVariable(value = "itemId") int itemId, @NotNull @RequestHeader("X-Sharer-User-Id") Integer owner) {
        return userMapper.getItem(itemId, owner);
    }

    @GetMapping("/search{text}")
    public List<Item> getItem(@RequestParam(value = "text") String text, @NotNull @RequestHeader("X-Sharer-User-Id") Integer owner) {
        return userMapper.getItem(text, owner);
    }

    @PatchMapping("/{id}")
    public Item putItem(@PathVariable int id, @RequestBody Item item, @NotNull @RequestHeader("X-Sharer-User-Id") Integer owner) {
        return userMapper.putItem(id, item, owner);
    }
}
