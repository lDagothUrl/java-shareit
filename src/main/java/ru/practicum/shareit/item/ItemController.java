package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.model.comment.CommentDto;
import ru.practicum.shareit.item.model.item.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
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
    public ItemDto postItem(
            @RequestHeader("X-Sharer-User-Id") int userId,
            @RequestBody @Valid ItemDto itemDto
    ) {
        return itemService.postItem(itemDto, userId);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto postComment(
            @RequestHeader("X-Sharer-User-Id") Integer userId,
            @PathVariable Integer itemId,
            @RequestBody @Valid CommentDto commentDto
    ) {
        return itemService.postComment(userId, itemId, commentDto);
    }

    @GetMapping("/{itemId}")
    public ItemDto getItemById(
            @RequestHeader("X-Sharer-User-Id") Integer userId,
            @PathVariable Integer itemId
    ) {
        return itemService.getItem(userId, itemId);
    }

    @GetMapping
    public List<ItemDto> getItemsUser(
            @RequestHeader("X-Sharer-User-Id") int userId,
            @RequestParam(defaultValue = "0") @PositiveOrZero int from,
            @RequestParam(defaultValue = "10") @Positive int size
    ) {
        return itemService.getItems(userId, from, size);
    }

    @GetMapping("/search")
    public List<ItemDto> getItems(
            @RequestParam String text,
            @RequestParam(defaultValue = "0") @PositiveOrZero int from,
            @RequestParam(defaultValue = "10") @Positive int size
    ) {
        return itemService.getItem(text, from, size);
    }

    @PatchMapping("/{itemId}")
    public ItemDto putItem(
            @RequestHeader("X-Sharer-User-Id") Integer userId,
            @PathVariable Integer itemId,
            @RequestBody ItemDto itemDto
    ) {
        return itemService.putItem(itemId, itemDto, userId);
    }

    @DeleteMapping("/{itemId}")
    public void deleteItem(
            @RequestHeader("X-Sharer-User-Id") Integer userId,
            @PathVariable Integer itemId
    ) {
        itemService.deleteItem(userId, itemId);
    }
}
