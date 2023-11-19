package ru.practicum.shareit.integration;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exception.model.BadRequestException;
import ru.practicum.shareit.exception.model.NotFoundException;
import ru.practicum.shareit.item.model.item.ItemDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.model.UserDto;
import ru.practicum.shareit.user.service.UserService;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
public class GetUserBookingsTest {
    private final EntityManager em;
    private final BookingService bookingService;
    private final ItemService itemService;
    private final UserService userService;

    @Test
    public void shouldGetUserBookings() {
        ItemDto itemDto = new ItemDto(
                null,
                "Test name",
                "Test description",
                true,
                null,
                null,
                null,
                null
        );
        UserDto userDto = new UserDto(null, "user1", "user1@email.com");
        userService.postUser(userDto);
        TypedQuery<User> queryUser = em.createQuery("select u from User u where u.email = :email", User.class);
        User user1 = queryUser.setParameter("email", userDto.getEmail()).getSingleResult();
        int user1Id = user1.getId();
    }

    @Test
    public void shouldNotGetUserBookingsWhenUnknownState() {
        BadRequestException e = Assertions.assertThrows(
                BadRequestException.class,
                () -> bookingService.getUserBookings(1, "Test", 0, 5)
        );

        assertThat(e.getMessage(), equalTo("Unknown state: Test"));
    }

    @Test
    public void shouldNotGetUserBookingsWhenUserNotFound() {
        NotFoundException e = Assertions.assertThrows(
                NotFoundException.class,
                () -> bookingService.getUserBookings(1, "ALL", 0, 5)
        );

        assertThat(e.getMessage(), equalTo("Not found userId: 1"));
    }
}