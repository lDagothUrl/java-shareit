package ru.practicum.shareit.item.repository;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.ItemDto;
import ru.practicum.shareit.item.model.ItemMapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * TODO Sprint add-controllers.
 */

@Repository
public class MemoryItemImpl implements MemoryItem {
    private final Map<Integer, Item> itemMap = new HashMap<>();
    private int newId;

    @Override
    public Item postItem(ItemDto itemDto, int owner) {
        itemDto.setId(createId());
        Item item = ItemMapper.dtoToItem(owner, itemDto);
        itemMap.put(item.getId(), item);
        return item;
    }

    @Override
    public Item getItem(Integer id) {
        return itemMap.get(id);
    }

    @Override
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

    @Override
    public List<Item> getItems() {
        return new ArrayList<>(itemMap.values());
    }

    @Override
    public Item putItem(int id, ItemDto itemDto, int owner) {
        itemDto.setId(id);
        Item item = ItemMapper.dtoToItem(owner, itemDto);
        itemMap.put(itemDto.getId(), item);
        return item;
    }

    private int createId() {
        return ++newId;
    }
}
