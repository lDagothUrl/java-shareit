package ru.practicum.shareit.integration;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.model.NotFoundUserException;
import ru.practicum.shareit.request.model.RequestDto;
import ru.practicum.shareit.request.service.RequestService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.model.UserDto;
import ru.practicum.shareit.user.service.UserService;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.time.LocalDateTime;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
public class GetItemRequestsByUserIdTest {
    private final EntityManager em;
    private final RequestService requestService;
    private final UserService userService;

    @Test
    public void shouldGetItemRequestsByUserId() {
        RequestDto itemRequestDto = new RequestDto(null, "Test description", LocalDateTime.now(), null);
        UserDto userDto = new UserDto(null, "user1", "user1@email.com");
        userService.postUser(userDto);
        TypedQuery<User> query = em.createQuery("select u from User u where u.email = :email", User.class);
        User user = query.setParameter("email", userDto.getEmail()).getSingleResult();
        int userId = user.getId();
        requestService.addItemRequest(userId, itemRequestDto);
//        List<RequestDto> itemRequests = requestService.getItemRequestsByUserId(userId);
//        RequestDto itemRequestDtoOutgoing = itemRequests.get(0);
//
//        assertThat(1, equalTo(itemRequests.size()));
//        assertThat(itemRequestDtoOutgoing.getId(), notNullValue());
//        assertThat(itemRequestDtoOutgoing.getDescription(), equalTo(itemRequestDto.getDescription()));
//        assertThat(itemRequestDtoOutgoing.getItems(), equalTo(Collections.emptyList()));
    }

    @Test
    public void shouldNotGetItemRequestsByUserIdWhenUserNotFound() {
        NotFoundUserException e = Assertions.assertThrows(
                NotFoundUserException.class,
                () -> requestService.getItemRequestsByUserId(1)
        );

        assertThat(e.getMessage(), equalTo("Not found userId: 1"));
    }
}