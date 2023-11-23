package ru.practicum.shareit.request.model;

import ru.practicum.shareit.item.model.item.Item;
import ru.practicum.shareit.item.model.item.ItemMapper;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;


public class RequestMapper {
    public static RequestDto itemRequestToDto(Request request, List<Item> itemDtoList) {
        return new RequestDto(
                request.getId(),
                request.getDescription(),
                request.getCreated(),
                (itemDtoList == null) ?
                        Collections.emptyList() : itemDtoList.stream()
                        .map(item -> ItemMapper.itemToDto(item, null, null, null))
                        .collect(Collectors.toList())
        );
    }

    public static Request itemRequestFromDto(RequestDto requestDto, User requestor) {
        return new Request(
                requestDto.getId(),
                requestDto.getDescription(),
                requestor,
                LocalDateTime.now()
        );
    }
}
