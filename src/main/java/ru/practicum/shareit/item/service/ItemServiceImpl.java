package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.model.BadRequestException;
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
public class ItemServiceImpl implements ItemService {

    private final MemoryItem memoryItem;
    private final MemoryUser memoryUser;

    @Override
    public ItemDto postItem(ItemDto item, int owner) {
        checkItem(item, owner);
        log.info("Create new Item: \n{}\nowner: {}", item, owner);
        return ItemMapper.itemToDto(memoryItem.postItem(item, owner));
    }

    @Override
    public List<ItemDto> getItems(int owner) {
        log.info("Get list item by owner: {}", owner);
        return memoryItem.getItems(owner).stream().map(ItemMapper::itemToDto).collect(Collectors.toList());
    }

    @Override
    public ItemDto getItem(int id) {
        log.info("Get item by id: {}", id);
        Item item = memoryItem.getItem(id);
        return ItemMapper.itemToDto(item);
    }

    @Override
    public List<ItemDto> getItem(String text) {
        log.info("Get list item by text: {}", text);
        if (text.isBlank()) {
            return new ArrayList<>();
        }
        return memoryItem.getItem(text).stream().map(ItemMapper::itemToDto).collect(Collectors.toList());
    }

    @Override
    public ItemDto putItem(int id, ItemDto item, int owner) {
        log.info("Put item: \n{}\nid: {}\nowner: {}", item, id, owner);
        Item itemOriginal = memoryItem.getItem(id);
        int idItemOwen = itemOriginal.getOwner();
        if (memoryUser.getUser(owner) == null) {
            throw new NotFoundException("The user does not exist id: " + owner);
        }
        if (idItemOwen != owner) {
            throw new NotFoundException("item update with other user id: " + id + " owner: " + owner);
        }
        if (item.getName() == null) {
            item.setName(itemOriginal.getName());
        }
        if (item.getDescription() == null) {
            item.setDescription(itemOriginal.getDescription());
        }
        if (item.getAvailable() == null) {
            item.setAvailable(itemOriginal.getAvailable());
        }
        return ItemMapper.itemToDto(memoryItem.putItem(id, item, owner));
    }

    private void checkItem(ItemDto item, int owner) {
        if (item.getAvailable() == null) {
            throw new BadRequestException("Not available");
        }
        if (item.getName() == null || item.getName().isBlank()) {
            throw new BadRequestException("Is blank name");
        }
        if (item.getDescription() == null || item.getDescription().isBlank()) {
            throw new BadRequestException("Is blank description");
        }
        if (memoryUser.getUser(owner) == null) {
            throw new NotFoundException("The user does not exist id: " + owner);
        }
    }
}