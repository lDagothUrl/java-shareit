package ru.practicum.shareit.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.item.model.comment.Comment;

import java.util.List;

public interface MemoryComment extends JpaRepository<Comment, Integer> {
    List<Comment> findByItemIdOrderByCreatedDesc(int itemId);
}