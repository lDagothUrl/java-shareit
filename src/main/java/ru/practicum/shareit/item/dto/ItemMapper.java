package ru.practicum.shareit.item.dto;

import lombok.extern.slf4j.Slf4j;
import ru.practicum.shareit.exception.model.BadRequest;
import ru.practicum.shareit.exception.model.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class ItemMapper {

    private final ItemDto itemDto = new ItemDto();
    private int newId;

    public Item postItem(Item item, int owner) {
        checkItem(item, owner);
        item.setOwner(owner);
        log.info("Create new Item: \n{}", item);
        item.setId(createId());
        return itemDto.postItem(item);
    }

    public List<Item> getItems(Integer owner) {
        return itemDto.getItems(owner);
    }

    public Item getItem(int id, Integer owner) {
        Item item = itemDto.getItem(id, owner);
        return item;
    }

    public List<Item> getItem(String text, Integer owner) {
        if (text.isBlank()){
            return new ArrayList<>();
        }
        return itemDto.getItem(text, owner);
    }

    public Item putItem(int id, Item item, Integer owner) {
        log.info("Create new Item: \n{}\nid: {}", item, id);
        if (UserDto.getUser(owner) == null) {
            throw new NotFoundException("The user does not exist id: " + owner);
        }
        if (itemDto.getItem(id).getOwner() != owner) {
            throw new NotFoundException("item update with other user id: " + id + " owner: " + owner);
        }
        Item itemMap = itemDto.getItem(id);
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
        return itemDto.postItem(item);
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
        if (UserDto.getUser(owner) == null) {
            throw new NotFoundException("The user does not exist id: " + owner);
        }
    }
}