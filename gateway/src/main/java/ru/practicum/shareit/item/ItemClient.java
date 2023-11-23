package ru.practicum.shareit.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.item.model.CommentDto;
import ru.practicum.shareit.item.model.ItemDto;

import java.util.Map;

@Service
@Slf4j
public class ItemClient extends BaseClient {
    private static final String API_PREFIX = "/items";

    @Autowired
    public ItemClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }

    public ResponseEntity<Object> getById(int userId, int itemId) {
        log.info("getById itemId: {} userId: {}", itemId, userId);
        return get("/" + itemId, userId, null);
    }

    public ResponseEntity<Object> getItemsByUserId(int userId, int from, int size) {
        log.info("getItemsByUserId userId: {}", userId);
        Map<String, Object> parameters = Map.of(
                "from", from,
                "size", size
        );
        return get("?from={from}&size={size}", userId, parameters);
    }

    public ResponseEntity<Object> addItem(int userId, ItemDto itemDto) {
        log.info("addItem item: {} userId: {}", itemDto, userId);
        return post("", userId, itemDto);
    }

    public ResponseEntity<Object> updateItem(int userId, int itemId, ItemDto itemDto) {
        log.info("updateItem itemId: {} userId: {}", itemId, userId);
        return patch("/" + itemId, userId, null, itemDto);
    }

    public ResponseEntity<Object> deleteItem(int userId, int itemId) {
        log.info("deleteItem itemId: {} userId: {}", itemId, userId);
        return delete("/" + itemId, userId);
    }

    public ResponseEntity<Object> findItems(int userId, String text, int from, int size) {
        log.info("findItems userId: {} text: {}", userId, text);
        Map<String, Object> parameters = Map.of(
                "text", text,
                "from", from,
                "size", size
        );
        return get("/search?text={text}&from={from}&size={size}", userId, parameters);
    }

    public ResponseEntity<Object> addComment(int userId, int itemId, CommentDto commentDto) {
        log.info("addComment comment: {} itemId: {} userId: {}", commentDto, itemId, userId);
        return post("/" + itemId + "/comment", userId, commentDto);
    }
}