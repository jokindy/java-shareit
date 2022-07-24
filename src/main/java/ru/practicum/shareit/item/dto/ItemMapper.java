package ru.practicum.shareit.item.dto;

import org.modelmapper.Conditions;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.item.Item;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class ItemMapper {

    private final ModelMapper modelMapper;

    public ItemMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
        setUp();
    }

    public ItemInputDto toDto(Item item) {
        return modelMapper.map(item, ItemInputDto.class);
    }

    public ItemOutputDto toOutputDto(Item item, int userId) {
        ItemOutputDto dto = modelMapper.map(item, ItemOutputDto.class);
        if (item.getOwnerId() != userId) {
            dto.setLastBooking(null);
            dto.setNextBooking(null);
        }
        return dto;
    }

    public Item toDomain(ItemInputDto itemInputDto) {
        return modelMapper.map(itemInputDto, Item.class);
    }

    public Item update(ItemInputDto itemInputDto, Item item) {
        modelMapper.map(itemInputDto, item);
        return item;
    }

    public List<ItemOutputDto> toDtoList(Collection<Item> list) {
        return list.stream()
                .map(item -> modelMapper.map(item, ItemOutputDto.class))
                .collect(Collectors.toList());
    }

    private void setUp() {
        modelMapper.getConfiguration().setPropertyCondition(Conditions.isNotNull());
        modelMapper.createTypeMap(Item.class, ItemOutputDto.class).setPostConverter(
                ctx -> {
                    ItemOutputDto dto = ctx.getDestination();
                    List<Booking> bookings = ctx.getSource().getBookings();
                    if (bookings.size() != 0) {
                        List<Booking> bookingList = bookings.stream()
                                .sorted((e1, e2) -> e2.getStart().compareTo(e1.getStart()))
                                .collect(Collectors.toList());
                        dto.setNextBooking(bookingList.get(0));
                        dto.setLastBooking(bookingList.get(bookingList.size() - 1));
                    }
                    return dto;
                }
        );
    }
}
