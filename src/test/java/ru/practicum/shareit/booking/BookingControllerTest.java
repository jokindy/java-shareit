package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import ru.practicum.shareit.booking.dto.BookingInputDto;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.practicum.shareit.booking.BookingStatus.APPROVED;
import static ru.practicum.shareit.booking.BookingStatus.WAITING;

@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class BookingControllerTest {

    @Autowired
    private WebApplicationContext webApplicationContext;
    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    private BookingInputDto bookingInputDto;

    @MockBean
    private BookingService bookingService;

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        objectMapper = new ObjectMapper();
        bookingInputDto = new BookingInputDto(0,
                LocalDateTime.of(2022, 8, 2, 11, 0),
                LocalDateTime.of(2022, 8, 3, 15, 0), 1, 1, WAITING);
    }

    @Order(1)
    @Test
    public void shouldPostBooking() throws Exception {
        doNothing()
                .when(bookingService)
                .add(any());
        mockMvc.perform(
                        post("/bookings")
                                .header("X-Sharer-User-Id", 1)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(bookingInputDto))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingInputDto.getId()), Integer.class))
                .andExpect(jsonPath("$.status", is("WAITING")));
    }

    @Order(2)
    @Test
    public void shouldUpdateBooking() throws Exception {
        when(bookingService.updateStatus(1, 1, true))
                .thenReturn(getBooking(1, 1));
        mockMvc.perform(
                        patch("/bookings/{id}", 1)
                                .param("approved", "true")
                                .header("X-Sharer-User-Id", 1)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingInputDto.getId()), Integer.class))
                .andExpect(jsonPath("$.status", is("APPROVED")));
    }

    @Order(3)
    @Test
    public void shouldGetBooking() throws Exception {
        when(bookingService.getBookingByUser(1, 1))
                .thenReturn(getBooking(1, 1));
        mockMvc.perform(
                        get("/bookings/{id}", 1)
                                .header("X-Sharer-User-Id", 1)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingInputDto.getId()), Integer.class))
                .andExpect(jsonPath("$.status", is("APPROVED")));
    }

    @Order(4)
    @Test
    public void shouldGetBookings() throws Exception {
        when(bookingService.getBookingsByUser(1, BookingState.ALL, 0, 10))
                .thenReturn(new PageImpl<>(List.of(getBooking(1, 1),
                        getBooking(2, 2))));
        mockMvc.perform(
                        get("/bookings")
                                .param("state", "ALL")
                                .header("X-Sharer-User-Id", 1)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Order(5)
    @Test
    public void shouldGetBookingsByOwner() throws Exception {
        when(bookingService.getBookingsByOwner(1, BookingState.ALL, 0, 10))
                .thenReturn(new PageImpl<>(List.of(getBooking(1, 1),
                        getBooking(2, 2))));
        mockMvc.perform(
                        get("/bookings/owner")
                                .param("state", "ALL")
                                .header("X-Sharer-User-Id", 1)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }

    private Booking getBooking(int bookerId, int itemId) {
        Booking booking = new Booking();
        booking.setBookerId(bookerId);
        booking.setItemId(itemId);
        booking.setStatus(APPROVED);
        return booking;
    }

}
