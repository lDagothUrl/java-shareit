package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.model.NotFoundException;
import ru.practicum.shareit.exception.model.NotFoundUserException;
import ru.practicum.shareit.item.repository.MemoryItem;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.request.model.RequestDto;
import ru.practicum.shareit.request.model.RequestMapper;
import ru.practicum.shareit.request.repository.MemoryRequest;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.MemoryUser;

import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.shareit.request.model.RequestMapper.itemRequestFromDto;
import static ru.practicum.shareit.request.model.RequestMapper.itemRequestToDto;

@RequiredArgsConstructor
@Service
@Slf4j
@Transactional
public class RequestServiceImpl implements RequestService {
    private final MemoryRequest memoryRequest;
    private final MemoryUser memoryUser;
    private final MemoryItem memoryItem;

    @Override
    public RequestDto addItemRequest(int userId, RequestDto requestDto) {
        log.info("Add request userId: {} request: {}", userId, requestDto);
        User user = memoryUser.findById(userId)
                .orElseThrow(() -> new NotFoundUserException("Not found userId: " + userId));
        return itemRequestToDto(memoryRequest.save(itemRequestFromDto(requestDto, user)));
    }

    @Override
    @Transactional(readOnly = true)
    public List<RequestDto> getItemRequestsByUserId(int userId) {
        log.info("Get requests userId: {}", userId);
        if (!memoryUser.existsById(userId)) {
            throw new NotFoundUserException("Not found userId: " + userId);
        }
        List<Request> requestList = memoryRequest.findByRequestorIdOrderByCreatedDesc(userId);
        return requestList.stream()
                .peek(request -> request.setItems(memoryItem.findByRequestId(request.getId())))
                .map(RequestMapper::itemRequestToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<RequestDto> getAllItemRequests(int userId, int from, int size) {
        log.info("Get requests userId: {} from: {} size: {}", userId, from, size);
        if (!memoryUser.existsById(userId)) {
            throw new NotFoundUserException("Not found userId: " + userId);
        }
        int page = from / size;
        return memoryRequest.findByRequestorIdNotOrderByCreatedDesc(userId, PageRequest.of(page, size)).stream()
                .peek(request -> request.setItems(memoryItem.findByRequestId(request.getId())))
                .map(RequestMapper::itemRequestToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public RequestDto getItemRequestById(int userId, int requestId) {
        log.info("Get request userId: {} requestId: {}", userId, requestId);
        if (!memoryUser.existsById(userId)) {
            throw new NotFoundUserException("Not found userId: " + userId);
        }
        Request request = memoryRequest.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Request requestId: " + requestId));
        request.setItems(memoryItem.findByRequestId(requestId));
        return itemRequestToDto(request);
    }
}