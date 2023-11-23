package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.model.NotFoundException;
import ru.practicum.shareit.exception.model.NotFoundUserException;
import ru.practicum.shareit.item.model.item.Item;
import ru.practicum.shareit.item.model.item.ItemMapper;
import ru.practicum.shareit.item.repository.MemoryItem;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.request.model.RequestDto;
import ru.practicum.shareit.request.model.RequestMapper;
import ru.practicum.shareit.request.repository.MemoryRequest;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.MemoryUser;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
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
        return itemRequestToDto(memoryRequest.save(itemRequestFromDto(requestDto, user)),
                requestDto.getItems() == null ? null : requestDto.getItems().stream().map(request -> ItemMapper.itemFromDto(request, null, null))
                        .collect(Collectors.toList()));
    }

    @Override
    @Transactional(readOnly = true)
    public List<RequestDto> getItemRequestsByUserId(int userId) {
        log.info("Get requests userId: {}", userId);
        if (!memoryUser.existsById(userId)) {
            throw new NotFoundUserException("Not found userId: " + userId);
        }

//        List<Item> items = memoryItem.findAllByRequestRequestorId(userId);
//        Map<Request, List<Item>> requestListMap = items.stream()
//                .collect(Collectors.groupingBy(Item::getRequest));
//        List<RequestDto> requestDtoList = new ArrayList<>();
//        for (Request request : requestListMap.keySet()) {
//            requestDtoList.add(RequestMapper.itemRequestToDto(request, requestListMap.get(request)));
//        }
//        return requestDtoList;


        List<Request> requestList = memoryRequest.findByRequestorIdOrderByCreatedDesc(userId);
        List<Item> itemList = memoryItem.findAllByRequestRequestorId(userId);
        List<RequestDto> requestDtoList = new ArrayList<>();
        for (Request request : requestList) {
            List<Item> items = new ArrayList<>();
            for (Item item : itemList) {
                if (Objects.equals(item.getRequest().getId(), request.getId())) {
                    items.add(item);
                }
            }
            requestDtoList.add(RequestMapper.itemRequestToDto(request, items));
        }
        return requestDtoList;
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
                .map(request -> RequestMapper.itemRequestToDto(request, memoryItem.findByRequestId(request.getId())))
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
        return itemRequestToDto(request, memoryItem.findByRequestId(requestId));
    }
}