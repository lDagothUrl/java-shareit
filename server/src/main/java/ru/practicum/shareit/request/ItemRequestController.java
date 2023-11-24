package ru.practicum.shareit.request;


import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.model.RequestDto;
import ru.practicum.shareit.request.service.RequestService;

import java.util.List;

@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
public class ItemRequestController {
    private final RequestService requestService;

    @PostMapping
    public RequestDto addItemRequest(
            @RequestHeader("X-Sharer-User-Id") int userId,
            @RequestBody RequestDto itemRequestDto) {
        return requestService.addItemRequest(userId, itemRequestDto);
    }

    @GetMapping
    public List<RequestDto> getItemRequestsByUserId(@RequestHeader("X-Sharer-User-Id") int userId) {
        return requestService.getItemRequestsByUserId(userId);
    }

    @GetMapping(path = "/all")
    public List<RequestDto> getAllItemRequests(
            @RequestHeader("X-Sharer-User-Id") int userId,
            @RequestParam(defaultValue = "0") int from,
            @RequestParam(defaultValue = "10") int size
    ) {
        return requestService.getAllItemRequests(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public RequestDto getItemRequestById(
            @RequestHeader("X-Sharer-User-Id") int userId,
            @PathVariable int requestId
    ) {
        return requestService.getItemRequestById(userId, requestId);
    }
}
