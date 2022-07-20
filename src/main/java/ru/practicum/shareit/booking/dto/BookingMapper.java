package ru.practicum.shareit.booking.dto;

import org.modelmapper.*;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.Booking;

import static ru.practicum.shareit.booking.BookingStatus.WAITING;

@Component
public class BookingMapper {

    private final ModelMapper modelMapper;

    public BookingMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
        setUp();
    }

    public Booking toDomain(BookingInputDto bookingInputDto) {
        return modelMapper.map(bookingInputDto, Booking.class);
    }

    public BookingOutputDto toOutputDto(Booking booking, BookingInfo bookingInfo) {
        BookingOutputDto extendedDto = modelMapper.map(booking, BookingOutputDto.class);
        extendedDto.setItem(bookingInfo.getItem());
        extendedDto.setBooker(bookingInfo.getUser());
        return extendedDto;
    }

    private void setUp() {
        modelMapper.createTypeMap(BookingInputDto.class, Booking.class).setPostConverter(
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
