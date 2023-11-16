package ru.practicum.shareit.request.model;

import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.stream.Collectors;

import static ru.practicum.shareit.item.model.item.ItemMapper.itemToDto;


public class RequestMapper {
    public static RequestDto itemRequestToDto(Request request) {
        return new RequestDto(
                request.getId(),
                request.getDescription(),
                request.getCreated(),
                (request.getItems() == null) ?
                        Collections.emptyList() :
                        request.getItems().stream()
                                .map(item -> itemToDto(item, null, null, null))
                                .collect(Collectors.toList())
        );
    }

    public static Request itemRequestFromDto(RequestDto requestDto, User requestor) {
        return new Request(
                requestDto.getId(),
                requestDto.getDescription(),
                requestor,
                LocalDateTime.now(),
                null
        );
    }
}
