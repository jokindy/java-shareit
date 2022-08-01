package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserService;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.Matchers.hasProperty;
import static org.mockito.Mockito.mock;


@Transactional
@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@TestPropertySource(locations = "classpath:application-test-request.properties")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ItemRequestServiceTest {

    private final EntityManager em;
    private final ItemRequestService requestService;

    @BeforeEach
    void setUp() {
        User user = new User();
        user.setName("John");
        user.setEmail("john@mail.com");
        em.persist(user);
        em.flush();
        UserService mockUserService = mock(UserService.class);
        requestService.setUserService(mockUserService);
    }

    @Order(1)
    @Test
    void addItemRequest() {
        ItemRequest request = getItemRequest(1, "I need tools");
        requestService.add(request);
        TypedQuery<ItemRequest> query = em.createQuery("Select i from ItemRequest i where i.requesterId = :userId",
                ItemRequest.class);
        ItemRequest requestDb = query.setParameter("userId", request.getRequesterId()).getSingleResult();
        assertThat(requestDb.getId(), notNullValue());
        assertThat(requestDb.getRequesterId(), equalTo(request.getRequesterId()));
        assertThat(requestDb.getDescription(), equalTo(request.getDescription()));
        assertThat(requestDb.getCreated(), equalTo(request.getCreated()));
    }

    @Order(2)
    @Test
    void getItemRequest() {
        ItemRequest request = getItemRequest(2, "I need car");
        em.persist(request);
        em.flush();
        ItemRequest requestDb = requestService.getRequestById(2, 2);
        assertThat(requestDb.getId(), notNullValue());
        assertThat(requestDb.getRequesterId(), equalTo(request.getRequesterId()));
        assertThat(requestDb.getDescription(), equalTo(request.getDescription()));
        assertThat(requestDb.getCreated(), equalTo(request.getCreated()));
    }

    @Order(3)
    @Test
    void getBookings() {
        List<ItemRequest> requests = List.of(getItemRequest(3, "I need plane"),
                getItemRequest(3, "I need boat"),
                getItemRequest(3, "I need guitar"));
        for (ItemRequest request : requests) {
            em.persist(request);
        }
        em.flush();
        List<ItemRequest> requestsDb = requestService.getRequestsByUser(3);
        assertThat(requestsDb, hasSize(requests.size()));
        for (ItemRequest request : requests) {
            assertThat(requestsDb, hasItem(allOf(
                    hasProperty("id", notNullValue()),
                    hasProperty("requesterId", equalTo(request.getRequesterId())),
                    hasProperty("description", equalTo(request.getDescription())),
                    hasProperty("created", equalTo(request.getCreated()))
            )));
        }
    }

    private ItemRequest getItemRequest(int userId, String description) {
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setRequesterId(userId);
        itemRequest.setDescription(description);
        itemRequest.setCreated(LocalDateTime.now());
        return itemRequest;
    }
}
