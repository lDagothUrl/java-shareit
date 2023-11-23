package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.model.BookingDtoDefault;
import ru.practicum.shareit.booking.model.BookingDtoOutgoing;
import ru.practicum.shareit.booking.service.BookingService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequestMapping("/bookings")
@RequiredArgsConstructor
@Validated
public class BookingController {
    private final BookingService bookingService;

    @PostMapping
    public BookingDtoOutgoing postBooking(@RequestHeader("X-Sharer-User-Id") Integer userId, @Valid @RequestBody BookingDtoDefault bookingDtoDefault) {
        return bookingService.postBooking(bookingDtoDefault, userId);
    }

    @GetMapping("/{bookingId}")
    public BookingDtoOutgoing getBooking(@RequestHeader("X-Sharer-User-Id") Integer userId, @PathVariable Integer bookingId) {
        return bookingService.getBooking(userId, bookingId);
    }

    @GetMapping
    public List<BookingDtoOutgoing> getUserBookings(
            @RequestHeader("X-Sharer-User-Id") int userId,
            @RequestParam(defaultValue = "ALL") String state,
            @RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
            @RequestParam(defaultValue = "10") @Positive Integer size
    ) {
        return bookingService.getUserBookings(userId, state, from, size);
    }

    @GetMapping("/owner")
    public List<BookingDtoOutgoing> getOwnerBookings(
            @RequestHeader("X-Sharer-User-Id") int userId,
            @RequestParam(defaultValue = "ALL") String state,
            @RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
            @RequestParam(defaultValue = "10") @Positive Integer size
    ) {
        return bookingService.getOwnerBookings(userId, state, from, size);
    }

    @PatchMapping("/{bookingId}")
    public BookingDtoOutgoing putBooking(@RequestHeader("X-Sharer-User-Id") Integer userId, @PathVariable Integer bookingId, @RequestParam boolean approved) {
        return bookingService.putBooking(userId, bookingId, approved);
    }
}
