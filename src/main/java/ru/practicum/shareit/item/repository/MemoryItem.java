package ru.practicum.shareit.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.model.item.Item;

import java.util.List;

public interface MemoryItem extends JpaRepository<Item, Integer> {
    List<Item> findByOwnerId(int userId);

    @Query("select it " +
            "from Item as it " +
            "where (lower(it.name) like lower(concat('%', ?1,'%')) " +
            "or lower(it.description) like lower(concat('%', ?1,'%'))) " +
            "and it.isAvailable = TRUE")
    List<Item> findByText(String text);
}
