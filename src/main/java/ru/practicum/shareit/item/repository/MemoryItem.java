package ru.practicum.shareit.item.repository;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * TODO Sprint add-controllers.
 */

@Repository
public class MemoryItem {
    private final Map<Integer, Item> itemMap = new HashMap<>();

    public Item postItem(Item item) {
        itemMap.put(item.getId(), item);
        return item;
    }

    public Item getItem(Integer id) {
        return itemMap.get(id);
    }

    public List<Item> getItem(String text) {
        List<Item> itemList = new ArrayList<>();
        text = text.toLowerCase();
        for (Item item : itemMap.values()) {
            if (item.getAvailable() && (item.getName().toLowerCase().contains(text) || item.getDescription().toLowerCase().contains(text))) {
                itemList.add(item);
            }
        }
        return itemList;
    }

    public List<Item> getItems() {
        return new ArrayList<>(itemMap.values());
    }
}
