package ru.practicum.shareit.item.repository;

import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.ItemDto;

import java.util.List;

public interface MemoryItem {
    Item postItem(ItemDto itemDto, int owner);

    Item getItem(Integer id);

    List<Item> getItem(String text);

    List<Item> getItems(int owner);

    Item putItem(int id, ItemDto itemDto, int owner);
}
