package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.*;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.item.comment.Comment;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserService;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;

@Transactional
@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@TestPropertySource(locations = "classpath:application-test-item.properties")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ItemServiceTest {

    private final EntityManager em;
    private final ItemService itemService;

    @BeforeEach
    void setUp() {
        User user = new User();
        user.setName("John");
        user.setEmail("john@mail.com");
        em.persist(user);
        em.flush();
        UserService mockUserService = mock(UserService.class);
        itemService.setUserService(mockUserService);
        when(mockUserService.get(Mockito.anyInt()))
                .thenReturn(new User());
    }

    @Order(1)
    @Test
    void addItem() {
        Item item = getItem("item", "test item", 1);
        itemService.add(item);
        TypedQuery<Item> query = em.createQuery("Select i from Item i where i.ownerId = :ownerId", Item.class);
        Item itemDb = query.setParameter("ownerId", item.getOwnerId()).getSingleResult();
        assertThat(itemDb.getId(), notNullValue());
        assertThat(itemDb.getName(), equalTo(item.getName()));
        assertThat(itemDb.getDescription(), equalTo(item.getDescription()));
        assertThat(itemDb.isAvailable(), equalTo(item.isAvailable()));
        assertThat(itemDb.getOwnerId(), equalTo(item.getOwnerId()));
    }

    @Order(2)
    @Test
    void getItem() {
        Item item = getItem("item", "test item", 2);
        em.persist(item);
        em.flush();
        Item itemDb = itemService.getByItemId(2);
        assertThat(itemDb.getId(), notNullValue());
        assertThat(itemDb.getName(), equalTo(item.getName()));
        assertThat(itemDb.getDescription(), equalTo(item.getDescription()));
        assertThat(itemDb.isAvailable(), equalTo(item.isAvailable()));
        assertThat(itemDb.getOwnerId(), equalTo(item.getOwnerId()));
    }

    @Order(3)
    @Test
    void getItemByOwner() {
        List<Item> items = List.of(
                getItem("item 1", "test item 1", 3),
                getItem("item 2", "test item 2", 3),
                getItem("item 3", "test item 3", 3)
        );
        for (Item item : items) {
            em.persist(item);
        }
        em.flush();
        Collection<Item> targetItems = itemService.getItemsByOwnerIdInPage(3, 0, 10);
        assertThat(targetItems, hasSize(items.size()));
        for (Item item : items) {
            assertThat(targetItems, hasItem(allOf(
                    hasProperty("id", notNullValue()),
                    hasProperty("name", equalTo(item.getName())),
                    hasProperty("description", equalTo(item.getDescription())),
                    hasProperty("available", equalTo(item.isAvailable())),
                    hasProperty("ownerId", equalTo(item.getOwnerId()))
            )));
        }
    }

    @Order(4)
    @Test
    void getItemsBySearch() {
        Item item1 = getItem("item 1", "violin", 4);
        Item item2 = getItem("item 2", "guitar", 4);
        Item item3 = getItem("item 3", "acoustic guitar", 4);
        List<Item> items = List.of(item1, item2, item3);
        List<Item> guitarItems = List.of(item2, item3);
        for (Item item : items) {
            em.persist(item);
        }
        em.flush();
        Collection<Item> targetGuitarItems = itemService.getItemsBySearch("GuItAr", 0, 10);
        for (Item item : guitarItems) {
            assertThat(targetGuitarItems, hasItem(allOf(
                    hasProperty("id", notNullValue()),
                    hasProperty("name", equalTo(item.getName())),
                    hasProperty("description", equalTo(item.getDescription())),
                    hasProperty("available", equalTo(item.isAvailable())),
                    hasProperty("ownerId", equalTo(item.getOwnerId()))
            )));
        }
        Collection<Item> targetFoundItems = itemService.getItemsBySearch("tem", 0, 10);
        for (Item item : targetFoundItems) {
            assertThat(targetFoundItems, hasItem(allOf(
                    hasProperty("id", notNullValue()),
                    hasProperty("name", equalTo(item.getName())),
                    hasProperty("description", equalTo(item.getDescription())),
                    hasProperty("available", equalTo(item.isAvailable())),
                    hasProperty("ownerId", equalTo(item.getOwnerId()))
            )));
        }
    }

    @Order(5)
    @Test
    void addComment() {
        prepareDb();
        Comment comment = getComment();
        itemService.addComment(comment);
        TypedQuery<Comment> query = em.createQuery("Select c from Comment c where c.authorId = :authorId",
                Comment.class);
        Comment commentDb = query.setParameter("authorId", comment.getAuthorId()).getSingleResult();
        assertThat(commentDb.getId(), notNullValue());
        assertThat(commentDb.getItemId(), equalTo(comment.getItemId()));
        assertThat(commentDb.getAuthorId(), equalTo(comment.getAuthorId()));
        assertThat(commentDb.getText(), equalTo(comment.getText()));
        assertThat(commentDb.getCreated(), equalTo(comment.getCreated()));
    }

    private Item getItem(String name, String description, int ownerId) {
        Item item = new Item();
        item.setName(name);
        item.setDescription(description);
        item.setAvailable(true);
        item.setOwnerId(ownerId);
        return item;
    }

    private Comment getComment() {
        Comment comment = new Comment();
        comment.setItemId(9);
        comment.setAuthorId(6);
        comment.setText("Test comment");
        comment.setCreated(LocalDateTime.now());
        return comment;
    }

    private Booking getBooking() {
        Booking booking = new Booking();
        booking.setStart(LocalDateTime.now().minusDays(4));
        booking.setEnd(LocalDateTime.now().minusDays(2));
        booking.setBookerId(6);
        booking.setItemId(9);
        booking.setStatus(BookingStatus.APPROVED);
        return booking;
    }

    private void prepareDb() {
        User user = new User();
        user.setName("Booker");
        user.setEmail("booker@mail.com");
        Item item = getItem("item", "test item", 5);
        Booking booking = getBooking();
        em.persist(item);
        em.persist(user);
        em.persist(booking);
        em.flush();
        em.refresh(item);
    }
}
