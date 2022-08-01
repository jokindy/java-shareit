package ru.practicum.shareit.request;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.TestPropertySource;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.List;

@DataJpaTest
@TestPropertySource(locations = "classpath:application-test-request.properties")
public class ItemRequestRepositoryTest {

    @Autowired
    private TestEntityManager em;

    @Autowired
    private ItemRequestRepository repository;

    @Test
    public void contextLoads() {
        Assertions.assertNotNull(em);
    }

    @Test
    void shouldFindAnotherRequests() {
        List<User> users = List.of(
                getUser("new", "new@test.com"),
                getUser("test", "login@test.com"),
                getUser("vasya", "vasya@test.com")
        );
        for (User user : users) {
            em.persist(user);
        }
        List<ItemRequest> requests = List.of(
                getItemRequest(1, "I need violin"),
                getItemRequest(2, "I need boat"),
                getItemRequest(3, "I need guitar"));
        for (ItemRequest request : requests) {
            em.persist(request);
        }
        em.flush();
        List<ItemRequest> anotherRequests = repository.findAllOtherRequests(2, PageRequest.of(0, 10))
                .getContent();
        Assertions.assertEquals(2, anotherRequests.size());
        Assertions.assertEquals("I need violin", anotherRequests.get(0).getDescription());
    }

    private ItemRequest getItemRequest(int userId, String description) {
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setRequesterId(userId);
        itemRequest.setDescription(description);
        itemRequest.setCreated(LocalDateTime.now());
        return itemRequest;
    }

    private User getUser(String name, String email) {
        User user = new User();
        user.setName(name);
        user.setEmail(email);
        return user;
    }
}