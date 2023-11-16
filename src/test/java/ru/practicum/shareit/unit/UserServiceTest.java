package ru.practicum.shareit.unit;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import ru.practicum.shareit.exception.model.NotFoundException;
import ru.practicum.shareit.exception.model.ReplayException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.model.UserDto;
import ru.practicum.shareit.user.repository.MemoryUser;
import ru.practicum.shareit.user.service.UserServiceImpl;

import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static ru.practicum.shareit.user.model.UserMapper.userToDto;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    @Mock
    private MemoryUser memoryUser;
    @InjectMocks
    private UserServiceImpl userService;

    private User user;

    @BeforeEach
    public void setUp() {
        user = new User(1, "user1", "user1@email.com");
    }

    @Test
    public void shouldGetUserById() {
        Mockito
                .when(memoryUser.findById(anyInt()))
                .thenReturn(Optional.of(user));

        UserDto userDtoOutgoing = userService.getUser(1);

        assertThat(userDtoOutgoing.getId(), equalTo(user.getId()));
        assertThat(userDtoOutgoing.getName(), equalTo(user.getName()));
        assertThat(userDtoOutgoing.getEmail(), equalTo(user.getEmail()));
    }

    @Test
    public void shouldNotGetUserByIdWhenUserNotFound() {
        Mockito
                .when(memoryUser.findById(anyInt()))
                .thenReturn(Optional.empty());

        NotFoundException e = Assertions.assertThrows(
                NotFoundException.class,
                () -> userService.getUser(1)
        );

        assertThat(e.getMessage(), equalTo("Not found user id: 1"));
    }

    @Test
    public void shouldGetUsers() {
        Mockito
                .when(memoryUser.findAll())
                .thenReturn(List.of(user));

        List<UserDto> users = userService.getUsers();
        UserDto userDtoOutgoing = users.get(0);

        assertThat(users.size(), equalTo(1));
        assertThat(userDtoOutgoing.getId(), equalTo(user.getId()));
        assertThat(userDtoOutgoing.getName(), equalTo(user.getName()));
        assertThat(userDtoOutgoing.getEmail(), equalTo(user.getEmail()));
    }

    @Test
    public void shouldAddUser() {
        Mockito
                .when(memoryUser.save(any(User.class)))
                .then(returnsFirstArg());

        UserDto userDtoOutgoing = userService.postUser(userToDto(user));

        assertThat(userDtoOutgoing.getId(), equalTo(user.getId()));
        assertThat(userDtoOutgoing.getName(), equalTo(user.getName()));
        assertThat(userDtoOutgoing.getEmail(), equalTo(user.getEmail()));
    }

    @Test
    public void shouldNotAddUserWhenEmailNotUnique() {
        Mockito
                .when(memoryUser.save(any(User.class)))
                .thenThrow(DataIntegrityViolationException.class);

        ReplayException e = Assertions.assertThrows(
                ReplayException.class,
                () -> userService.postUser(userToDto(user))
        );

        assertThat(e.getMessage(), equalTo("The userEmail already exists"));
    }

    @Test
    public void shouldUpdateUser() {
        Mockito
                .when(memoryUser.findById(anyInt()))
                .thenReturn(Optional.of(user));
        Mockito
                .when(memoryUser.save(any(User.class)))
                .then(returnsFirstArg());

        UserDto userDtoOutgoing = userService.putUser(user.getId(), userToDto(user));

        assertThat(userDtoOutgoing.getId(), equalTo(user.getId()));
        assertThat(userDtoOutgoing.getName(), equalTo(user.getName()));
        assertThat(userDtoOutgoing.getEmail(), equalTo(user.getEmail()));
    }

    @Test
    public void shouldNotUpdateUserWhenUserNotFound() {
        Mockito
                .when(memoryUser.findById(anyInt()))
                .thenReturn(Optional.empty());

        NotFoundException e = Assertions.assertThrows(
                NotFoundException.class,
                () -> userService.putUser(user.getId(), userToDto(user))
        );

        assertThat(e.getMessage(), equalTo("Not found user id: 1"));
    }

    @Test
    public void shouldNotUpdateUserWhenEmailNotUnique() {
        Mockito
                .when(memoryUser.findById(anyInt()))
                .thenReturn(Optional.of(user));
        Mockito
                .when(memoryUser.save(any(User.class)))
                .thenThrow(DataIntegrityViolationException.class);

        ReplayException e = Assertions.assertThrows(
                ReplayException.class,
                () -> userService.putUser(user.getId(), userToDto(user))
        );

        assertThat(e.getMessage(), equalTo("The userEmail already exists"));
    }

    @Test
    public void deleteUser() {
        userService.delUser(1);

        Mockito.verify(memoryUser).deleteById(anyInt());
    }
}