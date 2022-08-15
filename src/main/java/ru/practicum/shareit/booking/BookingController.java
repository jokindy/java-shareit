package ru.practicum.shareit.booking;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingInputDto;
import ru.practicum.shareit.booking.dto.BookingOutputDto;
import ru.practicum.shareit.booking.dto.BookingMapper;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.util.Collection;

@Slf4j
@RestController
@RequestMapping(path = "/bookings")
@AllArgsConstructor
public class BookingController {

    private final BookingService bookingService;
    private final BookingMapper bookingMapper;

    @PostMapping
    public BookingOutputDto add(@Valid @RequestBody BookingInputDto bookingInputDto,
                                @RequestHeader("X-Sharer-User-Id") int userId) {
        log.info("Add new booking by user id: {} to item id: {}", userId, bookingInputDto.getItemId());
        Booking booking = bookingMapper.toDomain(bookingInputDto, userId);
        bookingService.add(booking);
        return bookingMapper.toOutputDto(booking);
    }

    @PatchMapping("/{bookingId}")
    public BookingOutputDto updateAvailability(@PathVariable int bookingId, @RequestParam boolean approved,
                                               @RequestHeader("X-Sharer-User-Id") int userId) {
        log.info("Change availability by user id: {} to booking id: {}", userId, bookingId);
        Booking updatedBooking = bookingService.updateStatus(bookingId, userId, approved);
        return bookingMapper.toOutputDto(updatedBooking);
    }

    @GetMapping("/{bookingId}")
    public BookingOutputDto getBooking(@PathVariable int bookingId, @RequestHeader("X-Sharer-User-Id") int userId) {
        log.info("Get booking id: {} by user id: {}", bookingId, userId);
        Booking booking = bookingService.getBookingByUser(bookingId, userId);
        return bookingMapper.toOutputDto(booking);
    }

    @GetMapping
    public Collection<BookingOutputDto> getBookingsByIdAndState(@RequestParam(defaultValue = "ALL") BookingState state,
                                                                @RequestHeader("X-Sharer-User-Id") int userId,
                                                                @Valid @Positive
                                                                @RequestParam(defaultValue = "0") int from,
                                                                @Valid @Positive
                                                                @RequestParam(defaultValue = "10") int size) {
        log.info("Get bookings by user id: {}", userId);
        Collection<Booking> bookings = bookingService.getBookingsByUser(userId, state, from, size).getContent();
        return bookingMapper.toOutputDtoList(bookings);
    }

    @GetMapping("/owner")
    public Collection<BookingOutputDto> getBookingByOwner(@RequestParam(defaultValue = "ALL") BookingState state,
                                                          @RequestHeader("X-Sharer-User-Id") int userId,
                                                          @Valid @Positive @RequestParam(defaultValue = "0") int from,
                                                          @Valid @Positive @RequestParam(defaultValue = "10") int size) {
        log.info("Get bookings by owner id: {}", userId);
        Collection<Booking> bookings = bookingService.getBookingsByOwner(userId, state, from, size).getContent();
        return bookingMapper.toOutputDtoList(bookings);
    }
}