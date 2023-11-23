package ru.practicum.shareit.request.repository;


import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.request.model.Request;

import java.util.List;

public interface MemoryRequest extends JpaRepository<Request, Integer> {
    List<Request> findByRequestorIdOrderByCreatedDesc(int userId);

    List<Request> findByRequestorIdNotOrderByCreatedDesc(int userId, Pageable pageable);
}