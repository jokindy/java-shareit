package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingState;

import javax.validation.Valid;
import javax.validation.constraints.Positive;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
@Validated
public class BookingGatewayController {

    private final BookingClient bookingClient;

    @PostMapping
    public Object add(@Valid @RequestBody BookingDto bookingDto,
                      @RequestHeader("X-Sharer-User-Id") int userId) {
        log.info("Add new booking by user id: {} to item id: {}", userId, bookingDto.getItemId());
        return bookingClient.addBooking(userId, bookingDto);
    }

    @PatchMapping("/{bookingId}")
    public Object updateAvailability(@PathVariable int bookingId, @RequestParam boolean approved,
                                     @RequestHeader("X-Sharer-User-Id") int userId) {
        log.info("Change availability by user id: {} to booking id: {}", userId, bookingId);
        return bookingClient.updateAvailability(bookingId, userId, approved);
    }

    @GetMapping("/{bookingId}")
    public Object getBooking(@PathVariable int bookingId, @RequestHeader("X-Sharer-User-Id") int userId) {
        log.info("Get booking id: {} by user id: {}", bookingId, userId);
        return bookingClient.getBooking(userId, bookingId);
    }

    @GetMapping
    public Object getBookingsByIdAndState(@RequestParam(defaultValue = "ALL") BookingState state,
                                          @RequestHeader("X-Sharer-User-Id") int userId,
                                          @Valid @Positive @RequestParam(defaultValue = "1") int from,
                                          @Valid @Positive @RequestParam(defaultValue = "100") int size) {
        log.info("Get bookings by user id: {}", userId);
        return bookingClient.getBookings("", userId, state, from, size);
    }

    @GetMapping("/owner")
    public Object getBookingByOwner(@RequestParam(defaultValue = "ALL") BookingState state,
                                    @RequestHeader("X-Sharer-User-Id") int userId,
                                    @Valid @Positive @RequestParam(defaultValue = "1") int from,
                                    @Valid @Positive @RequestParam(defaultValue = "100") int size) {
        log.info("Get bookings by owner id: {}", userId);
        return bookingClient.getBookings("/owner", userId, state, from, size);
    }
}
