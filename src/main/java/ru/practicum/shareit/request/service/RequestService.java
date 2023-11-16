package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.model.RequestDto;

import java.util.List;

public interface RequestService {
    RequestDto addItemRequest(int userId, RequestDto requestDto);

    List<RequestDto> getItemRequestsByUserId(int userId);

    List<RequestDto> getAllItemRequests(int userId, int from, int size);

    RequestDto getItemRequestById(int userId, int requestId);
}
