package ru.practicum.shareit.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.request.ItemRequestController;
import ru.practicum.shareit.request.model.RequestDto;
import ru.practicum.shareit.request.service.RequestService;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemRequestController.class)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemRequestControllerTest {
    @MockBean
    private RequestService requestService;
    private final ObjectMapper mapper;
    private final MockMvc mvc;

    private final RequestDto itemRequestDto = new RequestDto(
            1,
            "item request description",
            LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS),
            Collections.emptyList()
    );

    @Test
    public void shouldAddItemRequest() throws Exception {
        Mockito
                .when(requestService.addItemRequest(anyInt(), any(RequestDto.class)))
                .thenReturn(itemRequestDto);

        mvc.perform(post("/requests")
                        .content(mapper.writeValueAsString(itemRequestDto))
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(itemRequestDto.getId()))
                .andExpect(jsonPath("$.description").value(itemRequestDto.getDescription()))
                .andExpect(jsonPath("$.created").value(itemRequestDto.getCreated().toString()));
    }

    @Test
    public void shouldNotAddItemRequestWhenBlankDescription() throws Exception {
        mvc.perform(post("/requests")
                        .content(mapper.writeValueAsString(
                                new RequestDto(
                                        1,
                                        "",
                                        LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS),
                                        Collections.emptyList()
                                )
                        ))
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void shouldGetItemRequestsByUserId() throws Exception {
        Mockito
                .when(requestService.getItemRequestsByUserId(anyInt()))
                .thenReturn(List.of(itemRequestDto));

        mvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$.[0].id").value(itemRequestDto.getId()))
                .andExpect(jsonPath("$.[0].description").value(itemRequestDto.getDescription()))
                .andExpect(jsonPath("$.[0].created").value(itemRequestDto.getCreated().toString()));
    }

    @Test
    public void shouldGetAllItemRequests() throws Exception {
        Mockito
                .when(requestService.getAllItemRequests(anyInt(), anyInt(), anyInt()))
                .thenReturn(List.of(itemRequestDto));

        mvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$.[0].id").value(itemRequestDto.getId()))
                .andExpect(jsonPath("$.[0].description").value(itemRequestDto.getDescription()))
                .andExpect(jsonPath("$.[0].created").value(itemRequestDto.getCreated().toString()));
    }

    @Test
    public void shouldGetItemRequestById() throws Exception {
        Mockito
                .when(requestService.getItemRequestById(anyInt(), anyInt()))
                .thenReturn(itemRequestDto);

        mvc.perform(get("/requests/1")
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(itemRequestDto.getId()))
                .andExpect(jsonPath("$.description").value(itemRequestDto.getDescription()))
                .andExpect(jsonPath("$.created").value(itemRequestDto.getCreated().toString()));
    }
}