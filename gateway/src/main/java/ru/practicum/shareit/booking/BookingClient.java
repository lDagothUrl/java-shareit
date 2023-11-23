package ru.practicum.shareit.booking;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.booking.model.BookingDtoDefault;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.exception.BookingStateException;

import java.util.Map;

@Service
@Slf4j
public class BookingClient extends BaseClient {
    private static final String API_PREFIX = "/bookings";

    @Autowired
    public BookingClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }

    public ResponseEntity<Object> addBooking(int userId, BookingDtoDefault bookingDtoDefault) {
        log.info("addBooking userId: {} booking: {}", bookingDtoDefault, userId);
        return post("", userId, bookingDtoDefault);
    }

    public ResponseEntity<Object> approveBooking(int userId, int bookingId, boolean approved) {
        log.info("approveBooking bookingId: {} userId: {} approved: {}", bookingId, userId, approved);
        return patch("/" + bookingId + "?approved={approved}",
                userId,
                Map.of("approved", approved),
                null
        );
    }

    public ResponseEntity<Object> getById(int userId, int bookingId) {
        log.info("getById bookingId: {} userId: {}", bookingId, userId);
        return get("/" + bookingId, userId, null);
    }

    public ResponseEntity<Object> getUserBookings(int userId, String state, int from, int size) {
        log.info("getUserBookings userId: {}", userId);
        try {
            BookingState.valueOf(state);
        } catch (IllegalArgumentException e) {
            throw new BookingStateException("Unknown state: " + state);
        }
        Map<String, Object> parameters = Map.of(
                "state", state,
                "from", from,
                "size", size
        );
        return get("?state={state}&from={from}&size={size}", userId, parameters);
    }

    public ResponseEntity<Object> getOwnerBookings(int userId, String state, int from, int size) {
        log.info("getOwnerBookings userId: {}", userId);
        try {
            BookingState.valueOf(state);
        } catch (IllegalArgumentException e) {
            throw new BookingStateException("Unknown state: " + state);
        }
        Map<String, Object> parameters = Map.of(
                "state", state,
                "from", from,
                "size", size
        );
        return get("/owner?state={state}&from={from}&size={size}", userId, parameters);
    }
}