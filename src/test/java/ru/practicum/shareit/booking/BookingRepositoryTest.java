package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.TestPropertySource;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

@DataJpaTest
@TestPropertySource(locations = "classpath:application-test-booking.properties")
public class BookingRepositoryTest {

    @Autowired
    private TestEntityManager em;

    @Autowired
    private BookingRepository repository;

    @Test
    public void contextLoads() {
        Assertions.assertNotNull(em);
    }

    @Test
    public void findAllByOwner() {
        User owner = getUser("owner", "user@user.com");
        User booker = getUser("booker", "booker@user.com");
        Item item1 = getItem("item 1", "test item 1");
        Item item2 = getItem("item 2", "test item 2");
        Booking booking1 = getBooking(1);
        Booking booking2 = getBooking(2);
        em.persist(owner);
        em.persist(booker);
        em.persist(item1);
        em.persist(item2);
        em.persist(booking1);
        em.persist(booking2);
        em.flush();
        List<Booking> bookings = repository.findAllByOwner(1, PageRequest.of(0, 10)).getContent();
        Assertions.assertEquals(2, bookings.size());
    }

    private Item getItem(String name, String description) {
        Item item = new Item();
        item.setName(name);
        item.setDescription(description);
        item.setAvailable(true);
        item.setOwnerId(1);
        return item;
    }

    private User getUser(String name, String email) {
        User user = new User();
        user.setName(name);
        user.setEmail(email);
        return user;
    }

    private Booking getBooking(int itemId) {
        Random random = new Random();
        Booking booking = new Booking();
        booking.setStart(LocalDateTime.of(2022, 8, 1, 11, 0));
        booking.setEnd(LocalDateTime.of(2022, 8, 1, 15, 0)
                .plusMinutes(random.nextInt(40)));
        booking.setBookerId(2);
        booking.setItemId(itemId);
        booking.setStatus(BookingStatus.WAITING);
        return booking;
    }
}