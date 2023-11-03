package ru.practicum.shareit.item.dto;

import ru.practicum.shareit.item.model.Item;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * TODO Sprint add-controllers.
 */

public class ItemDto {
    private final Map<Integer, Item> itemMap = new HashMap<>();

    public Item postItem(Item item) {
        itemMap.put(item.getId(), item);
        return item;
    }

    public Item getItem(Integer id) {
        return itemMap.get(id);
    }

    public Item getItem(Integer id, Integer owner) {
        return itemMap.get(id);
    }

    public List<Item> getItem(String text, Integer owner) {
        List<Item> itemList = new ArrayList<>();
        text = text.toLowerCase();
        for (Item item : itemMap.values()) {
            String itemStr = (item.getName() + item.getDescription()).toLowerCase();
            if (itemStr.indexOf(text) >= 0) {
                itemList.add(item);
            }
        }
        return itemList;
    }

    public List<Item> getItems(Integer owner) {
        List<Item> itemList = new ArrayList<>();
        for (Item item : itemMap.values()) {
            if (item.getOwner() == owner) {
                itemList.add(item);
            }
        }
        return itemList;
    }
}
