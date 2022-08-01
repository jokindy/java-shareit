package ru.practicum.shareit.item;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.TestPropertySource;
import ru.practicum.shareit.user.User;

import java.util.List;

@DataJpaTest
@TestPropertySource(locations = "classpath:application-test-item.properties")
public class ItemRepositoryTest {

    @Autowired
    private TestEntityManager em;

    @Autowired
    private ItemRepository repository;

    @Test
    public void contextLoads() {
        Assertions.assertNotNull(em);
    }

    @Test
    void shouldUpdateItem() {
        User user = getUser("user", "user@user.com");
        Item item = getItem("item 1", "test item 1", 1);
        em.persist(user);
        em.persist(item);
        em.flush();
        repository.setItemInfoById("name", "test@test.com", true, 1);
        Item updatedItem = repository.findById(1).get();
        em.refresh(updatedItem);
        Assertions.assertEquals("name", updatedItem.getName());
        Assertions.assertEquals("test@test.com", updatedItem.getDescription());
    }

    @Test
    void shouldGetItemsByOwner() {
        User user = getUser("user 2", "user2@user.com");
        Item item1 = getItem("item 1", "test item 1", 2);
        Item item2 = getItem("item 2", "test item 2", 2);
        em.persist(user);
        em.persist(item1);
        em.persist(item2);
        em.flush();
        List<Item> foundItems = repository.getItemsByOwnerId(2, PageRequest.of(0, 10)).getContent();
        Assertions.assertEquals(2, foundItems.size());
    }

    private Item getItem(String name, String description, int ownerId) {
        Item item = new Item();
        item.setName(name);
        item.setDescription(description);
        item.setAvailable(true);
        item.setOwnerId(ownerId);
        return item;
    }

    private User getUser(String name, String email) {
        User user = new User();
        user.setName(name);
        user.setEmail(email);
        return user;
    }
}
