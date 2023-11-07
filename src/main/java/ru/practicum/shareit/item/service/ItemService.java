package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.model.ItemDto;

import java.util.List;

public interface ItemService {
    ItemDto postItem(ItemDto item, int owner);

    List<ItemDto> getItems(int owner);

    ItemDto getItem(int id, int owner);

    List<ItemDto> getItem(String text, int owner);

    ItemDto putItem(int id, ItemDto item, int owner);
}
