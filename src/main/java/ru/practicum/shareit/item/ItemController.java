package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.model.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;

    @PostMapping
    public ItemDto postItem(@RequestBody ItemDto item, @NotNull @RequestHeader("X-Sharer-User-Id") final Integer owner) {
        return itemService.postItem(item, owner);
    }

    @GetMapping
    public List<ItemDto> getItems(@NotNull @RequestHeader("X-Sharer-User-Id") final Integer owner) {
        return itemService.getItems(owner);
    }

    @GetMapping("/{itemId}")
    public ItemDto getItem(@PathVariable(value = "itemId") final Integer itemId, @NotNull @RequestHeader("X-Sharer-User-Id") final Integer owner) {
        return itemService.getItem(itemId, owner);
    }

    @GetMapping("/search{text}")
    public List<ItemDto> getItem(@RequestParam(value = "text") final String text, @NotNull @RequestHeader("X-Sharer-User-Id") final Integer owner) {
        return itemService.getItem(text, owner);
    }

    @PatchMapping("/{id}")
    public ItemDto putItem(@PathVariable int id, @RequestBody ItemDto item, @NotNull @RequestHeader("X-Sharer-User-Id") final Integer owner) {
        return itemService.putItem(id, item, owner);
    }
}
