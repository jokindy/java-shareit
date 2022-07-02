package ru.practicum.shareit.booking.dto;

import org.modelmapper.*;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.Booking;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.shareit.booking.BookingStatus.WAITING;

@Component
public class BookingMapper {

    private final ModelMapper modelMapper;

    public BookingMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
        setUp();
    }

    public BookingDto toDto(Booking booking) {
        return modelMapper.map(booking, BookingDto.class);
    }

    public Booking toDomain(BookingDto bookingDto) {
        return modelMapper.map(bookingDto, Booking.class);
    }

    public List<BookingDto> toDtoList(Collection<Booking> list) {
        return list.stream()
                .map(booking -> modelMapper.map(booking, BookingDto.class))
                .collect(Collectors.toList());
    }

    private void setUp() {
        modelMapper.createTypeMap(BookingDto.class, Booking.class).setPostConverter(
                ctx -> {
                    Booking booking = ctx.getDestination();
                    if (booking.getStatus() == null) {
                        booking.setStatus(WAITING);
                    }
                    return booking;
                }
        );
    }
}
