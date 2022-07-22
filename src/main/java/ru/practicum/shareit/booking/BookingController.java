package ru.practicum.shareit.booking;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingInputDto;
import ru.practicum.shareit.booking.dto.BookingOutputDto;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.item.Item;

import javax.validation.Valid;
import java.util.Collection;

@Slf4j
@RestController
@RequestMapping(path = "/bookings")
@AllArgsConstructor
public class BookingController {

    private final BookingService bookingService;
    private final BookingMapper bookingMapper;
    private final BookingValidationService bookingValidationService;

    @PostMapping
    public BookingOutputDto add(@Valid @RequestBody BookingInputDto bookingInputDto,
                                @RequestHeader("X-Sharer-User-Id") int userId) {
        log.info("Add new booking by user id: {} to item id: {}", userId, bookingInputDto.getItemId());
        bookingInputDto.setBookerId(userId);
        Booking booking = bookingMapper.toDomain(bookingInputDto);
        bookingValidationService.isBookingValidOrThrow(booking);
        bookingService.add(booking);
        return bookingMapper.toOutputDto(booking);
    }

    @PatchMapping("/{bookingId}")
    public BookingOutputDto updateAvailability(@PathVariable int bookingId, @RequestParam boolean approved,
                                               @RequestHeader("X-Sharer-User-Id") int userId) {
        log.info("Change availability by user id: {} to booking id: {}", userId, bookingId);
        Booking booking = bookingService.get(bookingId);
        bookingValidationService.isInputIdsIsValidOrThrow(booking.getItemId(), userId, booking.getBookerId());
        BookingStatus status = booking.getStatus();
        BookingStatus newStatus = bookingService.updateStatus(bookingId, approved, status);
        booking.setStatus(newStatus);
        return bookingMapper.toOutputDto(booking);
    }

    @GetMapping("/{bookingId}")
    public BookingOutputDto getBooking(@PathVariable int bookingId, @RequestHeader("X-Sharer-User-Id") int userId) {
        log.info("Get booking id: {}", bookingId);
        Booking booking = bookingService.get(bookingId);
        bookingValidationService.isUserCanGetBookingOrThrow(booking, userId);
        return bookingMapper.toOutputDto(booking);
    }

    @GetMapping
    public Collection<BookingOutputDto> getBookingsByIdAndState(@RequestParam(defaultValue = "ALL") BookingState state,
                                                                @RequestHeader("X-Sharer-User-Id") int userId) {
        log.info("Get bookings by user id: {}", userId);
        Collection<Booking> bookings = bookingService.getBookingsByUser(userId, state);
        return bookingMapper.toOutputDtoList(bookings);
    }

    @GetMapping("/owner")
    public Collection<BookingOutputDto> getBookingByOwner(@RequestParam(defaultValue = "ALL") BookingState state,
                                                          @RequestHeader("X-Sharer-User-Id") int userId) {
        log.info("Get bookings by owner id: {}", userId);
        Collection<Item> items = bookingValidationService.getListOfUserItemsOrThrow(userId);
        Collection<Booking> bookings = bookingService.getBookingsByOwner(items, state);
        return bookingMapper.toOutputDtoList(bookings);
    }
}