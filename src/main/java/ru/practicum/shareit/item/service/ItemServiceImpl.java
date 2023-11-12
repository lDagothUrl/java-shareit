package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.MemoryBooking;
import ru.practicum.shareit.exception.model.*;
import ru.practicum.shareit.item.model.comment.Comment;
import ru.practicum.shareit.item.model.comment.CommentDto;
import ru.practicum.shareit.item.model.item.Item;
import ru.practicum.shareit.item.model.item.ItemDto;
import ru.practicum.shareit.item.model.item.ItemMapper;
import ru.practicum.shareit.item.repository.MemoryComment;
import ru.practicum.shareit.item.repository.MemoryItem;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.MemoryUser;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static ru.practicum.shareit.item.model.comment.CommentMapper.commentFromDto;
import static ru.practicum.shareit.item.model.comment.CommentMapper.commentToDto;
import static ru.practicum.shareit.item.model.item.ItemMapper.itemToDto;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class ItemServiceImpl implements ItemService {

    private final MemoryItem memoryItem;
    private final MemoryUser memoryUser;
    private final MemoryBooking memoryBooking;
    private final MemoryComment memoryComment;


    @Override
    public ItemDto postItem(ItemDto itemDto, int userId) {
        log.info("Create new Item: \n{}\nowner: {}", itemDto, userId);
        User user = memoryUser.findById(userId)
                .orElseThrow(() -> new NotFoundUserException("Not found userId: " + userId));
        Item item = memoryItem.save(ItemMapper.itemFromDto(itemDto, user));
        return itemToDto(item, null, null, null);
    }

    @Override
    public CommentDto postComment(int userId, int itemId, CommentDto commentDto) {
        log.info("Create comment userId: {} itemId: {} comment:\n{}", userId, itemId, commentDto);
        User author = memoryUser.findById(userId)
                .orElseThrow(() -> new NotFoundUserException("Not found userId: " + userId));
        Item item = memoryItem.findById(itemId)
                .orElseThrow(() -> new NotFoundItemException("Not found itemId: " + itemId));
        Booking booking = null;
        try {
            booking = memoryBooking.findByIdAndItemOwnerId(itemId, userId).orElseThrow(() -> new NotFoundException("Not found booking itemId: " + itemId + " userId: " + userId));
        } catch (NotFoundException e) {
            log.info(e.getMessage());
        }
        BookingStatus status = BookingStatus.APPROVED;
        if (booking != null) {
            status = booking.getStatus();
        }
        if (status != BookingStatus.APPROVED || !memoryBooking.existsByBookerIdAndItemIdAndEndBefore(userId, itemId, LocalDateTime.now())) {
            throw new BookingTimeException("Error booking");
        }
        Comment comment = memoryComment.save(commentFromDto(commentDto, item, author));
        return commentToDto(comment, author.getName());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemDto> getItems(int userId) {
        log.info("Get list item by userId: {}", userId);
        if (!memoryUser.existsById(userId)) {
            throw new NotFoundUserException("Not found userId: " + userId);
        }
        return memoryItem.findByOwnerId(userId).stream().map(item -> {
            LocalDateTime now = LocalDateTime.now();
            return itemToDto(item, memoryBooking.findFirstByItemIdAndStatusNotAndStartBeforeOrderByStartDesc(item.getId(), BookingStatus.REJECTED, now), memoryBooking.findFirstByItemIdAndStatusNotAndStartAfterOrderByStartAsc(item.getId(), BookingStatus.REJECTED, now), memoryComment.findByItemIdOrderByCreatedDesc(item.getId()).stream().map(comment -> commentToDto(comment, comment.getAuthor().getName())).collect(Collectors.toList()));
        }).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public ItemDto getItem(int userId, int itemId) {
        log.info("Get item userId: {} itemId: {}", userId, itemId);
        Item item = memoryItem.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Not found itemId: " + itemId));
        Booking last = null;
        Booking next = null;
        if (item.getOwner().getId() == userId) {
            LocalDateTime now = LocalDateTime.now();
            last = memoryBooking.findFirstByItemIdAndStatusNotAndStartBeforeOrderByStartDesc(itemId, BookingStatus.REJECTED, now);
            next = memoryBooking.findFirstByItemIdAndStatusNotAndStartAfterOrderByStartAsc(itemId, BookingStatus.REJECTED, now);
        }
        List<CommentDto> comments = memoryComment.findByItemIdOrderByCreatedDesc(itemId).stream().map(comment -> commentToDto(comment, comment.getAuthor().getName())).collect(Collectors.toList());
        return itemToDto(item, last, next, comments);
    }

    @Override
    public List<ItemDto> getItem(String text) {
        log.info("Get list item by text: {}", text);
        if (text.isBlank()) {
            return Collections.emptyList();
        }
        return memoryItem.findByText(text).stream().map(item -> itemToDto(item, null, null, null)).collect(Collectors.toList());
    }

    @Override
    public ItemDto putItem(int itemId, ItemDto itemDto, int userId) {
        log.info("Put item: \n{}\nitemId: {}\nuserId: {}", itemDto, itemId, userId);
        itemDto.setId(itemId);
        Item oldItem = memoryItem.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Not found itemId: " + itemId));
        User owner = oldItem.getOwner();
        if (owner.getId() != userId) {
            throw new OwnerItemException("No access userId: " + userId + " itemId: " + itemId);
        }
        Item updateItem = ItemMapper.itemFromDto(itemDto, owner);
        String name = updateItem.getName();
        String description = updateItem.getDescription();
        if (name == null || name.isBlank()) {
            updateItem.setName(oldItem.getName());
        }
        if (description == null || description.isBlank()) {
            updateItem.setDescription(oldItem.getDescription());
        }
        if (updateItem.getIsAvailable() == null) {
            updateItem.setIsAvailable(oldItem.getIsAvailable());
        }
        Item item = memoryItem.save(updateItem);
        return itemToDto(item, null, null, null);
    }

    @Override
    public void deleteItem(int userId, int itemId) {
        log.info("Del item itemId: {} userId: {}", itemId, userId);
        if (!memoryUser.existsById(userId)) {
            throw new NotFoundUserException("Not found userId: " + userId);
        }
        memoryItem.deleteById(itemId);
    }
}