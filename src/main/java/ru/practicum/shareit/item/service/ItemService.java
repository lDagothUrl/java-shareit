package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.model.comment.CommentDto;
import ru.practicum.shareit.item.model.item.ItemDto;

import java.util.List;

public interface ItemService {
    ItemDto postItem(ItemDto item, int owner);

    CommentDto postComment(int userId, int itemId, CommentDto commentDto);

    List<ItemDto> getItems(int owner);

    ItemDto getItem(int userId, int itemId);

    List<ItemDto> getItem(String text);

    ItemDto putItem(int id, ItemDto item, int owner);

    void deleteItem(int userId, int itemId);
}
