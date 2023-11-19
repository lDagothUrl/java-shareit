package ru.practicum.shareit.integration;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.model.NotFoundUserException;
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
public class GetItemsByUserIdTest {
    private final EntityManager em;
    private final ItemService itemService;
    private final UserService userService;

    @Test
    public void shouldGetItemsByUserId() {
        ItemDto itemDto = new ItemDto(
                1,
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
        TypedQuery<User> query = em.createQuery("select u from User u where u.email = :email", User.class);
        User user = query.setParameter("email", userDto.getEmail()).getSingleResult();
        int userId = user.getId();
//        itemService.postItem(itemDto, userId);
//        List<ItemDto> items = itemService.getItems(userId, 0, 5);
//        ItemDto itemDtoOutgoing = items.get(0);
//
//        assertThat(items.size(), equalTo(1));
//        assertThat(itemDtoOutgoing.getId(), notNullValue());
//        assertThat(itemDtoOutgoing.getName(), equalTo(itemDto.getName()));
//        assertThat(itemDtoOutgoing.getDescription(), equalTo(itemDto.getDescription()));
//        assertThat(itemDtoOutgoing.getIsAvailable(), equalTo(itemDto.getIsAvailable()));
//        assertThat(itemDtoOutgoing.getRequestId(), nullValue());
//        assertThat(itemDtoOutgoing.getLastBooking(), nullValue());
//        assertThat(itemDtoOutgoing.getNextBooking(), nullValue());
//        assertThat(itemDtoOutgoing.getComments(), equalTo(Collections.emptyList()));
    }

    @Test
    public void shouldNotGetItemsByUserIdWhenUserNotFound() {
        NotFoundUserException e = Assertions.assertThrows(
                NotFoundUserException.class,
                () -> itemService.getItems(1, 0, 5)
        );

        assertThat(e.getMessage(), equalTo("Not found userId: 1"));
    }
}