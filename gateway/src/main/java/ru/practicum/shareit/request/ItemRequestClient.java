package ru.practicum.shareit.request;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.request.model.ItemRequestDto;

import java.util.Map;

@Service
@Slf4j
public class ItemRequestClient extends BaseClient {
    private static final String API_PREFIX = "/requests";

    @Autowired
    public ItemRequestClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }

    public ResponseEntity<Object> addItemRequest(int userId, ItemRequestDto itemRequestDto) {
        log.info("addItemRequest itemRequest: {} userId: {}", itemRequestDto, userId);
        return post("", userId, itemRequestDto);
    }

    public ResponseEntity<Object> getItemRequestsByUserId(int userId) {
        log.info("getItemRequestsByUserId userId: {}", userId);
        return get("", userId, null);
    }

    public ResponseEntity<Object> getAllItemRequests(int userId, int from, int size) {
        log.info("getAllItemRequests userId: {}", userId);
        Map<String, Object> parameters = Map.of(
                "from", from,
                "size", size
        );
        return get("/all?from={from}&size={size}", userId, parameters);
    }

    public ResponseEntity<Object> getById(int userId, int requestId) {
        log.info("getById requestId: {} userId: {}", requestId, userId);
        return get("/" + requestId, userId, null);
    }
}