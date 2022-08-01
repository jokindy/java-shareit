package ru.practicum.shareit.booking.dto;

import org.modelmapper.*;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.dto.ItemShortDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.Collection;
import java.util.stream.Collectors;

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

    public BookingOutputDto toOutputDto(Booking booking) {
        return modelMapper.map(booking, BookingOutputDto.class);
    }

    public Collection<BookingOutputDto> toOutputDtoList(Collection<Booking> list) {
        return list.stream()
                .map(booking -> modelMapper.map(booking, BookingOutputDto.class))
                .collect(Collectors.toList());
    }

    private void setUp() {
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        modelMapper.createTypeMap(BookingInputDto.class, Booking.class).setPostConverter(
                ctx -> {
                    Booking booking = ctx.getDestination();
                    if (booking.getStatus() == null) {
                        booking.setStatus(WAITING);
                    }
                    return booking;
                }
        );
        modelMapper.createTypeMap(Booking.class, BookingOutputDto.class).setPostConverter(
                ctx -> {
                    Booking booking = ctx.getSource();
                    BookingOutputDto dto = ctx.getDestination();
                    Item item = booking.getItem();
                    if (item != null) {
                        ItemShortDto itemShortDto = modelMapper.map(item, ItemShortDto.class);
                        dto.setItem(itemShortDto);
                    }
                    User booker = booking.getBooker();
                    if (booker != null) {
                        UserDto userDto = modelMapper.map(booker, UserDto.class);
                        dto.setBooker(userDto);
                    }
                    return dto;
                }
        );
    }
}
