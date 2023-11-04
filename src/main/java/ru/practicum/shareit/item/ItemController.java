package ru.practicum.shareit.item;

import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.item.model.Item;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequestMapping("/items")
public class ItemController {
    private final ItemService userMapper = new ItemService();

    @PostMapping
    public Item postItem(@RequestBody Item item, @NotNull @RequestHeader("X-Sharer-User-Id") Integer owner) {
        return userMapper.postItem(item, owner);
    }

    @GetMapping
    public List<Item> getItems(@NotNull @RequestHeader("X-Sharer-User-Id") Integer owner) {
        return userMapper.getItems(owner);
    }

    @GetMapping("/{itemId}")
    public Item getItem(@PathVariable(value = "itemId") Integer itemId, @NotNull @RequestHeader("X-Sharer-User-Id") Integer owner) {
        return userMapper.getItem(itemId);
    }

    @GetMapping("/search{text}")
    public List<Item> getItem(@RequestParam(value = "text") String text, @NotNull @RequestHeader("X-Sharer-User-Id") Integer owner) {
        return userMapper.getItem(text);
    }

    @PatchMapping("/{id}")
    public Item putItem(@PathVariable int id, @RequestBody Item item, @NotNull @RequestHeader("X-Sharer-User-Id") Integer owner) {
        return userMapper.putItem(id, item, owner);
    }
}
