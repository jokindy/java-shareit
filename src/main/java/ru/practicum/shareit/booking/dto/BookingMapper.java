package ru.practicum.shareit.booking.dto;

import org.modelmapper.*;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.util.Collection;
import java.util.stream.Collectors;

import static ru.practicum.shareit.booking.BookingStatus.WAITING;

@Component
public class BookingMapper {

    private final ModelMapper modelMapper;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    public BookingMapper(ModelMapper modelMapper, UserRepository userRepository, ItemRepository itemRepository) {
        this.modelMapper = modelMapper;
        this.userRepository = userRepository;
        this.itemRepository = itemRepository;
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

    @SuppressWarnings("OptionalGetWithoutIsPresent")
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
        modelMapper.createTypeMap(Booking.class, BookingOutputDto.class).setPostConverter(
                ctx -> {
                    Booking booking = ctx.getSource();
                    BookingOutputDto dto = ctx.getDestination();
                    User user = userRepository.findById(booking.getBookerId()).get();
                    Item item = itemRepository.findById(booking.getItemId()).get();
                    dto.setBooker(user);
                    dto.setItem(item);
                    return dto;
                }
        );
    }
}
