package ru.practicum.shareit.repository;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.item.model.item.Item;
import ru.practicum.shareit.item.repository.MemoryItem;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.MemoryUser;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

@DataJpaTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemStorageTest {
    private final MemoryItem memoryItem;
    private final MemoryUser memoryUser;

    @Test
    public void shouldFindByText() {
        User user = new User(null, "user", "user@email.com");
        memoryUser.save(user);
        Item item = new Item(
                null,
                "item name",
                "item description",
                true,
                user,
                null
        );
        memoryItem.save(item);
        List<Item> items = memoryItem.findByText("item", PageRequest.of(0, 5));

        assertThat(items.size(), equalTo(1));
        assertThat(items.get(0), equalTo(item));

        items = memoryItem.findByText("scissors", PageRequest.of(0, 5));

        assertThat(items.size(), equalTo(0));
    }
}