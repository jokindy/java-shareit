package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.*;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.user.User;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.Matchers.hasProperty;
import static org.mockito.Mockito.*;
import static ru.practicum.shareit.booking.BookingState.ALL;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

@Transactional
@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@TestPropertySource(locations = "classpath:application-test.properties")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class BookingServiceTest {

    private final EntityManager em;
    private final BookingService bookingService;

    @BeforeEach
    void setUp() {
        User user = new User();
        user.setName("John");
        user.setEmail("john@mail.com");
        em.persist(user);
        em.flush();
        BookingValidationService mockValidationService = mock(BookingValidationService.class);
        bookingService.setBookingValidationService(mockValidationService);
        doNothing()
                .when(mockValidationService)
                .isUserHasItemsOrThrow(Mockito.anyInt());
    }

    @Order(1)
    @Test
    void addBooking() {
        Booking booking = getBooking(1, 1);
        Item item = getItem(1);
        em.persist(item);
        em.flush();
        bookingService.add(booking);
        TypedQuery<Booking> query = em.createQuery("Select b from Booking b where b.bookerId = :bookerId",
                Booking.class);
        Booking bookingDb = query.setParameter("bookerId", booking.getBookerId()).getSingleResult();
        assertThat(bookingDb.getId(), notNullValue());
        assertThat(bookingDb.getStart(), equalTo(booking.getStart()));
        assertThat(bookingDb.getEnd(), equalTo(booking.getEnd()));
    }

    @Order(2)
    @Test
    void getBooking() {
        Booking booking = getBooking(2, 2);
        Item item = getItem(2);
        em.persist(item);
        em.flush();
        em.persist(booking);
        em.flush();
        Booking bookingDb = bookingService.getBookingByUser(2, 2);
        assertThat(bookingDb.getId(), notNullValue());
        assertThat(bookingDb.getStart(), equalTo(booking.getStart()));
        assertThat(bookingDb.getEnd(), equalTo(booking.getEnd()));
    }

    @Order(3)
    @Test
    void getBookings() {
        List<Booking> bookings = List.of(getBooking(3, 3),
                getBooking(3, 3),
                getBooking(3, 3));
        Item item = getItem(3);
        em.persist(item);
        for (Booking booking : bookings) {
            em.persist(booking);
        }
        em.flush();
        List<Booking> bookingsDb = bookingService.getBookingsByUser(3, ALL, 0, 10).getContent();
        assertThat(bookingsDb, hasSize(bookings.size()));
        for (Booking booking : bookings) {
            assertThat(bookingsDb, hasItem(allOf(
                    hasProperty("id", notNullValue()),
                    hasProperty("start", equalTo(booking.getStart())),
                    hasProperty("end", equalTo(booking.getEnd()))
            )));
        }
    }

    private Booking getBooking(int bookerId, int itemId) {
        Random random = new Random();
        Booking booking = new Booking();
        booking.setStart(LocalDateTime.of(2022, 8, 1, 11, 0));
        booking.setEnd(LocalDateTime.of(2022, 8, 1, 15, 0)
                .plusMinutes(random.nextInt(40)));
        booking.setBookerId(bookerId);
        booking.setItemId(itemId);
        booking.setStatus(BookingStatus.WAITING);
        return booking;
    }

    private Item getItem(int ownerId) {
        Item item = new Item();
        item.setName("name");
        item.setDescription("test item");
        item.setAvailable(true);
        item.setOwnerId(ownerId);
        return item;
    }

}
