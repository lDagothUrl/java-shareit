package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.model.BadRequest;
import ru.practicum.shareit.exception.model.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.ItemDto;
import ru.practicum.shareit.item.model.ItemMapper;
import ru.practicum.shareit.item.repository.MemoryItem;
import ru.practicum.shareit.user.repository.MemoryUser;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemService {

    private final MemoryItem memoryItem;
    private final MemoryUser memoryUser;
    private int newId;

    public ItemDto postItem(Item item, int owner) {
        checkItem(item, owner);
        item.setOwner(owner);
        log.info("Create new Item: \n{}", item);
        item.setId(createId());
        return ItemMapper.itemToDto(memoryItem.postItem(item));
    }

    public List<ItemDto> getItems(int owner) {
        return memoryItem.getItems().stream().filter(item -> item.getOwner() == owner).map(ItemMapper::itemToDto).collect(Collectors.toList());
    }

    public ItemDto getItem(int id, int owner) {
        Item item = memoryItem.getItem(id);
        return ItemMapper.itemToDto(item);
    }

    public List<ItemDto> getItem(String text, int owner) {
        if (text.isBlank()) {
            return new ArrayList<>();
        }
        return memoryItem.getItem(text).stream().map(ItemMapper::itemToDto).collect(Collectors.toList());
    }

    public ItemDto putItem(int id, Item item, int owner) {
        log.info("Create new Item: \n{}\nid: {}", item, id);
        if (memoryUser.getUser(owner) == null) {
            throw new NotFoundException("The user does not exist id: " + owner);
        }
        int idItemOwen = memoryItem.getItem(id).getOwner();
        if (idItemOwen != owner) {
            throw new NotFoundException("item update with other user id: " + id + " owner: " + owner);
        }
        Item itemOriginal = memoryItem.getItem(id);
        if (item.getName() == null) {
            item.setName(itemOriginal.getName());
        }
        if (item.getDescription() == null) {
            item.setDescription(itemOriginal.getDescription());
        }
        if (item.getAvailable() == null) {
            item.setAvailable(itemOriginal.getAvailable());
        }
        item.setId(id);
        item.setOwner(owner);
        return ItemMapper.itemToDto(memoryItem.postItem(item));
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
        if (memoryUser.getUser(owner) == null) {
            throw new NotFoundException("The user does not exist id: " + owner);
        }
    }

    private int createId() {
        return ++newId;
    }
}