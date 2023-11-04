package ru.practicum.shareit.item.service;

import lombok.extern.slf4j.Slf4j;
import ru.practicum.shareit.exception.model.BadRequest;
import ru.practicum.shareit.exception.model.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.MemoryItem;
import ru.practicum.shareit.user.repository.MemoryUser;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class ItemService {

    private final MemoryItem memoryItem = new MemoryItem();
    private int newId;

    public Item postItem(Item item, int owner) {
        checkItem(item, owner);
        item.setOwner(owner);
        log.info("Create new Item: \n{}", item);
        item.setId(createId());
        return memoryItem.postItem(item);
    }

    public List<Item> getItems(Integer owner) {
        return memoryItem.getItems(owner);
    }

    public Item getItem(int id) {
        Item item = memoryItem.getItem(id);
        return item;
    }

    public List<Item> getItem(String text) {
        if (text.isBlank()) {
            return new ArrayList<>();
        }
        return memoryItem.getItem(text);
    }

    public Item putItem(int id, Item item, int owner) {
        log.info("Create new Item: \n{}\nid: {}", item, id);
        if (MemoryUser.getUser(owner) == null) {
            throw new NotFoundException("The user does not exist id: " + owner);
        }
        int idItemOwen = memoryItem.getItem(id).getOwner();
        if (idItemOwen != owner) {
            throw new NotFoundException("item update with other user id: " + id + " owner: " + owner);
        }
        Item itemMap = memoryItem.getItem(id);
        if (item.getName() == null) {
            item.setName(itemMap.getName());
        }
        if (item.getDescription() == null) {
            item.setDescription(itemMap.getDescription());
        }
        if (item.getAvailable() == null) {
            item.setAvailable(itemMap.getAvailable());
        }
        item.setId(id);
        item.setOwner(owner);
        return memoryItem.postItem(item);
    }


    private int createId() {
        return ++newId;
    }

    private void checkItem(Item item, int owner) {
        if (item.getAvailable() == null) {
            throw new BadRequest("Not available");
        }
        if (item.getName() == null || item.getName().isBlank()) {
            throw new BadRequest("Is blank name");
        }
        if (item.getDescription() == null || item.getDescription().isBlank()) {
            throw new BadRequest("Is blank description");
        }
        if (MemoryUser.getUser(owner) == null) {
            throw new NotFoundException("The user does not exist id: " + owner);
        }
    }
}